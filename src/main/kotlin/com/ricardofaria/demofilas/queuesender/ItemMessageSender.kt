package com.ricardofaria.demofilas.queuesender

import com.google.gson.Gson
import com.ricardofaria.demofilas.config.ItemQueueWithDLQ
import com.ricardofaria.demofilas.messagecontracts.ItemPriceUpdateMessage
import com.ricardofaria.demofilas.restmodels.VersionCheckMode
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import java.math.BigDecimal
import java.util.*


@Service
class ItemMessageSender(private val sqsClient: SqsClient,
                        private val consumerWithDLQ: ItemQueueWithDLQ,
                        @Value("\${aws.sqs.itemqueue.url}") private val itemQueue: String
) {

    fun sendItemPriceUpdateMessage(itemId: UUID, price: BigDecimal, devoFalhar: Boolean, versionCheckMode: VersionCheckMode) {
        sendMessageRequest(ItemPriceUpdateMessage(itemId = itemId,
                price = price,
                devoFalhar = devoFalhar,
                versionCheckMode = versionCheckMode,
                messageTime = System.currentTimeMillis()))
        println("Mensagem de item enviada")
    }

    private fun sendMessageRequest(messageContract: ItemPriceUpdateMessage) {
        val messageContractAsJSON = Gson().toJson(messageContract)
        val messageRequest = SendMessageRequest.builder()
                .queueUrl(itemQueue)
                .messageBody(messageContractAsJSON)
                .build()
        sqsClient.sendMessage(messageRequest)
    }

    fun processMyDlq() {
        consumerWithDLQ.consumeFromDLQ()
    }

}