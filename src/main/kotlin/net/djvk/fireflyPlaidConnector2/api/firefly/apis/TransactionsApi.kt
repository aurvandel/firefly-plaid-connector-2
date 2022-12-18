/**
 * Firefly III API v1.5.6
 *
 * This is the documentation of the Firefly III API. You can find accompanying documentation on the website of Firefly III itself (see below). Please report any bugs or issues. You may use the \"Authorize\" button to try the API below. This file was last generated on 2022-04-04T03:54:41+00:00
 *
 * The version of the OpenAPI document: 1.5.6
 * Contact: james@firefly-iii.org
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package net.djvk.fireflyPlaidConnector2.api.firefly.apis

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.*
import io.ktor.client.engine.*
import net.djvk.fireflyPlaidConnector2.api.firefly.infrastructure.*
import net.djvk.fireflyPlaidConnector2.api.firefly.models.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

typealias FireflyTransactionId = String
typealias FireflyExternalId = String
typealias FireflyTransactionSplitId = String

@Component
open class TransactionsApi(
    @Value("\${fireflyPlaidConnector2.firefly.url}")
    baseUrl: String = ApiClient.BASE_URL,
    httpClientEngine: HttpClientEngine? = null,
    httpClientConfig: ((HttpClientConfig<*>) -> Unit)? = null,
    jsonBlock: ObjectMapper.() -> Unit = ApiClient.JSON_DEFAULT,
) : ApiClient(baseUrl, httpClientEngine, httpClientConfig, jsonBlock) {

    /**
     * Delete a transaction.
     * Delete a transaction.
     * @param id The ID of the transaction.
     * @return void
     */
    open suspend fun deleteTransaction(id: FireflyTransactionId): HttpResponse<Unit> {

        val localVariableAuthNames = listOf<String>("firefly_iii_auth")

        val localVariableBody =
            io.ktor.client.utils.EmptyContent

        val localVariableQuery = mutableMapOf<String, List<String>>()

        val localVariableHeaders = mutableMapOf<String, String>()

        val localVariableConfig = RequestConfig<kotlin.Any?>(
            RequestMethod.DELETE,
            "/api/v1/transactions/$id",
            query = localVariableQuery,
            headers = localVariableHeaders
        )

        return request(
            localVariableConfig,
            localVariableBody,
            localVariableAuthNames
        ).wrap()
    }

    /**
     * Delete split from transaction
     * Delete an individual journal (split) from a transaction.
     * @param id The ID of the transaction journal (the split) you wish to delete.
     * @return void
     */
    open suspend fun deleteTransactionJournal(id: FireflyTransactionSplitId): HttpResponse<Unit> {

        val localVariableAuthNames = listOf<String>("firefly_iii_auth")

        val localVariableBody =
            io.ktor.client.utils.EmptyContent

        val localVariableQuery = mutableMapOf<String, List<String>>()

        val localVariableHeaders = mutableMapOf<String, String>()

        val localVariableConfig = RequestConfig<kotlin.Any?>(
            RequestMethod.DELETE,
            "/api/v1/transaction-journals/{id}".replace("{" + "id" + "}", "$id"),
            query = localVariableQuery,
            headers = localVariableHeaders
        )

        return request(
            localVariableConfig,
            localVariableBody,
            localVariableAuthNames
        ).wrap()
    }

    /**
     * Get a single transaction.
     * Get a single transaction.
     * @param id The ID of the transaction.
     * @return [TransactionSingle]
     */
    @Suppress("UNCHECKED_CAST")
    open suspend fun getTransaction(id: FireflyTransactionId): HttpResponse<TransactionSingle> {

        val localVariableAuthNames = listOf<String>("firefly_iii_auth")

        val localVariableBody =
            io.ktor.client.utils.EmptyContent

        val localVariableQuery = mutableMapOf<String, List<String>>()

        val localVariableHeaders = mutableMapOf<String, String>()

        val localVariableConfig = RequestConfig<kotlin.Any?>(
            RequestMethod.GET,
            "/api/v1/transactions/{id}".replace("{" + "id" + "}", "$id"),
            query = localVariableQuery,
            headers = localVariableHeaders
        )

        return request(
            localVariableConfig,
            localVariableBody,
            localVariableAuthNames
        ).wrap()
    }

    /**
     * Get a single transaction, based on one of the underlying transaction journals (transaction splits).
     * Get a single transaction by underlying journal (split).
     * @param id The ID of the transaction journal (split).
     * @return TransactionSingle
     */
    @Suppress("UNCHECKED_CAST")
    open suspend fun getTransactionByJournal(id: FireflyTransactionSplitId): HttpResponse<TransactionSingle> {

        val localVariableAuthNames = listOf<String>("firefly_iii_auth")

        val localVariableBody =
            io.ktor.client.utils.EmptyContent

        val localVariableQuery = mutableMapOf<String, List<String>>()

        val localVariableHeaders = mutableMapOf<String, String>()

        val localVariableConfig = RequestConfig<kotlin.Any?>(
            RequestMethod.GET,
            "/api/v1/transaction-journals/{id}".replace("{" + "id" + "}", "$id"),
            query = localVariableQuery,
            headers = localVariableHeaders
        )

        return request(
            localVariableConfig,
            localVariableBody,
            localVariableAuthNames
        ).wrap()
    }

    /**
     * Lists all attachments.
     * Lists all attachments.
     * @param id The ID of the transaction.
     * @param page Page number. The default pagination is 50. (optional)
     * @return AttachmentArray
     */
    @Suppress("UNCHECKED_CAST")
    open suspend fun listAttachmentByTransaction(
        id: FireflyTransactionId,
        page: kotlin.Int?
    ): HttpResponse<AttachmentArray> {

        val localVariableAuthNames = listOf<String>("firefly_iii_auth")

        val localVariableBody =
            io.ktor.client.utils.EmptyContent

        val localVariableQuery = mutableMapOf<String, List<String>>()
        page?.apply { localVariableQuery["page"] = listOf("$page") }

        val localVariableHeaders = mutableMapOf<String, String>()

        val localVariableConfig = RequestConfig<kotlin.Any?>(
            RequestMethod.GET,
            "/api/v1/transactions/{id}/attachments".replace("{" + "id" + "}", "$id"),
            query = localVariableQuery,
            headers = localVariableHeaders
        )

        return request(
            localVariableConfig,
            localVariableBody,
            localVariableAuthNames
        ).wrap()
    }

    /**
     * Lists all piggy bank events.
     * Lists all piggy bank events.
     * @param id The ID of the transaction.
     * @param page Page number. The default pagination is 50. (optional)
     * @return PiggyBankEventArray
     */
    @Suppress("UNCHECKED_CAST")
    open suspend fun listEventByTransaction(
        id: FireflyTransactionId,
        page: kotlin.Int?
    ): HttpResponse<PiggyBankEventArray> {

        val localVariableAuthNames = listOf<String>("firefly_iii_auth")

        val localVariableBody =
            io.ktor.client.utils.EmptyContent

        val localVariableQuery = mutableMapOf<String, List<String>>()
        page?.apply { localVariableQuery["page"] = listOf("$page") }

        val localVariableHeaders = mutableMapOf<String, String>()

        val localVariableConfig = RequestConfig<kotlin.Any?>(
            RequestMethod.GET,
            "/api/v1/transactions/{id}/piggy_bank_events".replace("{" + "id" + "}", "$id"),
            query = localVariableQuery,
            headers = localVariableHeaders
        )

        return request(
            localVariableConfig,
            localVariableBody,
            localVariableAuthNames
        ).wrap()
    }

    /**
     * Lists all the transaction links for an individual journal (individual split).
     * Lists all the transaction links for an individual journal (a split). Don&#39;t use the group ID, you need the actual underlying journal (the split).
     * @param id The ID of the transaction journal / the split.
     * @param page Page number. The default pagination is 50. (optional)
     * @return TransactionLinkArray
     */
    @Suppress("UNCHECKED_CAST")
    open suspend fun listLinksByJournal(
        id: FireflyTransactionSplitId,
        page: kotlin.Int?
    ): HttpResponse<TransactionLinkArray> {

        val localVariableAuthNames = listOf<String>("firefly_iii_auth")

        val localVariableBody =
            io.ktor.client.utils.EmptyContent

        val localVariableQuery = mutableMapOf<String, List<String>>()
        page?.apply { localVariableQuery["page"] = listOf("$page") }

        val localVariableHeaders = mutableMapOf<String, String>()

        val localVariableConfig = RequestConfig<kotlin.Any?>(
            RequestMethod.GET,
            "/api/v1/transaction-journals/{id}/links".replace("{" + "id" + "}", "$id"),
            query = localVariableQuery,
            headers = localVariableHeaders
        )

        return request(
            localVariableConfig,
            localVariableBody,
            localVariableAuthNames
        ).wrap()
    }

    /**
     * List all the user&#39;s transactions.
     * List all the user&#39;s transactions.
     * @param page Page number. The default pagination is 50. (optional)
     * @param start A date formatted YYYY-MM-DD. This is the start date of the selected range (inclusive).  (optional)
     * @param end A date formatted YYYY-MM-DD. This is the end date of the selected range (inclusive).  (optional)
     * @param type Optional filter on the transaction type(s) returned. (optional)
     * @return TransactionArray
     */
    @Suppress("UNCHECKED_CAST")
    open suspend fun listTransaction(
        page: kotlin.Int?,
        start: java.time.LocalDate?,
        end: java.time.LocalDate?,
        type: TransactionTypeFilter?
    ): HttpResponse<TransactionArray> {

        val localVariableAuthNames = listOf<String>("firefly_iii_auth")

        val localVariableBody =
            io.ktor.client.utils.EmptyContent

        val localVariableQuery = mutableMapOf<String, List<String>>()
        page?.apply { localVariableQuery["page"] = listOf("$page") }
        start?.apply { localVariableQuery["start"] = listOf("$start") }
        end?.apply { localVariableQuery["end"] = listOf("$end") }
        type?.apply { localVariableQuery["type"] = listOf("$type") }

        val localVariableHeaders = mutableMapOf<String, String>()

        val localVariableConfig = RequestConfig<kotlin.Any?>(
            RequestMethod.GET,
            "/api/v1/transactions",
            query = localVariableQuery,
            headers = localVariableHeaders
        )

        return request(
            localVariableConfig,
            localVariableBody,
            localVariableAuthNames
        ).wrap()
    }

    /**
     * Store a new transaction
     * Creates a new transaction. The data required can be submitted as a JSON body or as a list of parameters.
     * @param transactionStore JSON array or key&#x3D;value pairs with the necessary transaction information. See the model for the exact specifications.
     * @return TransactionSingle
     */
    @Suppress("UNCHECKED_CAST")
    open suspend fun storeTransaction(transactionStore: TransactionStore): HttpResponse<TransactionSingle> {

        val localVariableAuthNames = listOf<String>("firefly_iii_auth")

        val localVariableBody = transactionStore

        val localVariableQuery = mutableMapOf<String, List<String>>()

        val localVariableHeaders = mutableMapOf<String, String>()

        val localVariableConfig = RequestConfig<kotlin.Any?>(
            RequestMethod.POST,
            "/api/v1/transactions",
            query = localVariableQuery,
            headers = localVariableHeaders
        )

        return jsonRequest(
            localVariableConfig,
            localVariableBody,
            localVariableAuthNames
        ).wrap()
    }

    /**
     * Update existing transaction. For more information, see https://docs.firefly-iii.org/firefly-iii/api/specials
     * Update an existing transaction.
     * @param id The ID of the transaction.
     * @param transactionUpdate JSON array with updated transaction information. See the model for the exact specifications.
     * @return TransactionSingle
     */
    @Suppress("UNCHECKED_CAST")
    open suspend fun updateTransaction(
        id: FireflyTransactionId,
        transactionUpdate: TransactionUpdate
    ): HttpResponse<TransactionSingle> {

        val localVariableAuthNames = listOf<String>("firefly_iii_auth")

        val localVariableBody = transactionUpdate

        val localVariableQuery = mutableMapOf<String, List<String>>()

        val localVariableHeaders = mutableMapOf<String, String>()

        val localVariableConfig = RequestConfig<kotlin.Any?>(
            RequestMethod.PUT,
            "/api/v1/transactions/{id}".replace("{" + "id" + "}", "$id"),
            query = localVariableQuery,
            headers = localVariableHeaders
        )

        return jsonRequest(
            localVariableConfig,
            localVariableBody,
            localVariableAuthNames
        ).wrap()
    }

}
