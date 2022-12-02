package net.djvk.fireflyPlaidConnector2.sync

import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import net.djvk.fireflyPlaidConnector2.api.firefly.models.TransactionSplit
import net.djvk.fireflyPlaidConnector2.api.firefly.models.TransactionTypeProperty
import net.djvk.fireflyPlaidConnector2.api.plaid.apis.PlaidApi
import net.djvk.fireflyPlaidConnector2.api.plaid.models.*
import net.djvk.fireflyPlaidConnector2.transactions.FireflyTransactionDto
import net.djvk.fireflyPlaidConnector2.transactions.TransactionConverter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*

/**
 * Batch sync runner.
 *
 * Handles the "batch" sync mode, which syncs a large batch of transactions at once, then exits.
 */
@ConditionalOnProperty(name = ["fireflyPlaidConnector2.syncMode"], havingValue = "batch")
@Component
class BatchSyncRunner(
    @Value("\${fireflyPlaidConnector2.batch.maxSyncDays}")
    private val syncDays: Int,
    @Value("\${fireflyPlaidConnector2.batch.setInitialBalance:false}")
    private val setInitialBalance: Boolean,
    @Value("\${fireflyPlaidConnector2.plaid.batchSize}")
    private val plaidBatchSize: Int,
    @Value("\${fireflyPlaidConnector2.timeZone}")
    private val timeZoneString: String,

    private val plaidApi: PlaidApi,
    private val syncHelper: SyncHelper,

    private val converter: TransactionConverter,

    ) : Runner {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val timeZone = TimeZone.getTimeZone(timeZoneString)

    override fun run() {
        syncHelper.setApiCreds()

        val allPlaidTxs = mutableMapOf<PlaidAccessToken, MutableList<Transaction>>()

        val startDate = LocalDate.now().minusDays(syncDays.toLong())
        val endDate = LocalDate.now()

        runBlocking {
            val (accountMap, accountAccessTokenSequence) = syncHelper.getAllPlaidAccessTokenAccountIdSets()
            for ((accessToken, accountIds) in accountAccessTokenSequence) {
                var offset = 0
                do {
                    /**
                     * Iterate through batches of Plaid transactions
                     *
                     * We're storing all this data in memory so we can try to match up offsetting transfers before inserting
                     *  into Firefly.
                     * Note that the heap size may need to be increased if you're handling a ton of transactions.
                     */
                    /**
                     * Iterate through batches of Plaid transactions
                     *
                     * We're storing all this data in memory so we can try to match up offsetting transfers before inserting
                     *  into Firefly.
                     * We don't use fireflyPlaidConnector2.transferMatchWindowDays here because if we did we'd have to
                     *  do some complex rolling window shenanigans that I have no interest in implementing, and it's
                     *  easy to run batch mode once on a high-spec machine.
                     * Note that the heap size may need to be increased if you're handling a ton of transactions.
                     */
                    val request = TransactionsGetRequest(
                        accessToken,
                        startDate,
                        endDate,
                        null,
                        TransactionsGetRequestOptions(
                            accountIds,
                            plaidBatchSize,
                            offset,
                            includeOriginalDescription = true,
                            includePersonalFinanceCategoryBeta = false,
                            includePersonalFinanceCategory = true,
                        )
                    )
                    val plaidTxs: List<Transaction>
                    try {
                        plaidTxs = plaidApi.transactionsGet(request).body().transactions
                    } catch (cre: ClientRequestException) {
                        logger.error("Error requesting Plaid transactions. Request: $request; ")
                        throw cre
                    }
                    allPlaidTxs
                        .getOrPut(accessToken) { mutableListOf() }
                        .addAll(plaidTxs)

                    /**
                     * This would be where we query transactions from Firefly and look for dupes, but the Firefly
                     *  API doesn't have a way to query by external id and I don't think it's worth the effort to
                     *  do date range queries and sift through all transactions, so for now we'll rely on Firefly's
                     *  "duplicate hash" dupe checking mechanism.
                     */

                    offset += plaidTxs.size

                    // Keep going until we get all the transactions
                } while (plaidTxs.size == plaidBatchSize)
            }

            // Map Plaid transactions to Firefly transactions
            val fireflyTxs = converter.convertBatchSync(allPlaidTxs.values.flatten(), accountMap)

            // Insert into Firefly
            syncHelper.optimisticInsertBatchIntoFirefly(fireflyTxs)

            // Set initial balance transaction if configured
            if (setInitialBalance) {
                setInitialBalances(allPlaidTxs, plaidApi, syncHelper, startDate)
            }
        }
    }

    suspend fun setInitialBalances(
        allPlaidTxs: Map<PlaidAccessToken, List<Transaction>>,
        plaidApi: PlaidApi,
        syncHelper: SyncHelper,
        startDate: LocalDate,
    ) {
        logger.info("Attempting to set initial balances")
        val (accountMap, accountAccessTokenSequence) = syncHelper.getAllPlaidAccessTokenAccountIdSets()
        // Iterate over all Plaid items/access tokens we have configured
        for ((accessToken, accountIds) in accountAccessTokenSequence) {
            val plaidTxs = allPlaidTxs[accessToken] ?: continue
            // Request balance data for this item/access token
            logger.debug("Requesting balances for access token $accessToken and account ids ${accountIds.joinToString()}")
            val balances: AccountsGetResponse
            try {
                balances = plaidApi.accountsBalanceGet(AccountsBalanceGetRequest(
                    accessToken, null, null, AccountsBalanceGetRequestOptions(accountIds)
                )).body()
            } catch (e: Exception) {
                logger.error("Failed to fetch balances for access token $accessToken and account ids ${accountIds.joinToString()}", e)
                continue
            }

            // Group transactions and balance data by Plaid account id
            val plaidTxsByAccountId = plaidTxs.groupBy { it.accountId }
            val balancesByAccountId = balances.accounts.associate { Pair(it.accountId, it.balances.current) }

            // Iterate over account ids
            for ((accountId, currentBalance) in balancesByAccountId) {
                val fireflyAccountId = accountMap[accountId]
                if (fireflyAccountId == null) {
                    logger.warn("Failed to find Firefly account id for Plaid account $accountId")
                    continue
                }
                if (currentBalance == null) {
                    logger.warn("No current balance data received for Plaid account $accountId")
                    continue
                }
                val txs = plaidTxsByAccountId[accountId] ?: listOf()
                val total = txs.fold(0.0) { acc, tx -> acc + tx.amount }
                val initialBalance = currentBalance + total

                val earliestTimestamp = txs.fold(OffsetDateTime.now()) { acc, tx ->
                    val ts = tx.getTimestamp(timeZone.toZoneId())
                    if (ts < acc) { ts } else { acc }
                }
                logger.debug("Inserting initial balance $initialBalance for Firefly account id $fireflyAccountId")
                syncHelper.optimisticInsertBatchIntoFirefly(listOf(FireflyTransactionDto(null, TransactionSplit(
                    /**
                     * Would like this to be [TransactionTypeProperty.openingBalance], but the Firefly API doesn't
                     *  let us insert with that value.
                     */
                    type = TransactionTypeProperty.deposit,
                    date = earliestTimestamp.minusHours(1),
                    amount = initialBalance.toString(),
                    description = "Plaid Connector Initial Balance",
                    sourceName = "Initial Balance",
                    sourceId = null,
                    destinationId = fireflyAccountId.toString(),
                    order = 0,
                    reconciled = false,
                    // Why the eff does the Firefly API require this
                    foreignAmount = "0",
                ))))
            }
        }
    }
}