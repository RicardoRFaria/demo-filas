package com.ricardofaria.demofilas.config

import com.google.gson.Gson
import com.ricardofaria.demofilas.messagecontracts.ItemPriceUpdateMessage
import com.ricardofaria.demofilas.restmodels.VersionCheckMode
import com.ricardofaria.demofilas.service.ItemService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.*
import java.lang.Thread.sleep

@Configuration
class ItemQueueWithDLQ(@Value("\${aws.sqs.itemqueue.url}") private val queueName: String,
                       private val sqsClient: SqsClient,
                       private val queueUrlService: QueueUrlService,
                       private val itemService: ItemService) {

    @Bean("itemqueue_with_dlq")
    fun createQueue() {
        val dlqCreateQueueRequest = CreateQueueRequest.builder().queueName("${queueName}_dlq").build()
        val dlqCreateResult = sqsClient.createQueue(dlqCreateQueueRequest)
        val dlqARN = sqsClient.getQueueAttributes(GetQueueAttributesRequest.builder()
                .queueUrl(dlqCreateResult.queueUrl())
                .attributeNames(QueueAttributeName.QUEUE_ARN).build()).attributes()[QueueAttributeName.QUEUE_ARN]
        val createQueueRequest = CreateQueueRequest.builder().queueName(queueName).attributes(
                mapOf(
                        QueueAttributeName.VISIBILITY_TIMEOUT to "5",
                        QueueAttributeName.REDRIVE_POLICY to "{\"maxReceiveCount\":\"2\",\"deadLetterTargetArn\":\"${dlqARN}\"}"
                )
        ).build()
        sqsClient.createQueue(createQueueRequest)
    }

    @EventListener(ApplicationReadyEvent::class)
    fun sqsConsumer() {
        Thread {
            while (true) {
                tryToConsumeMessage(queueName, ignorarFlagDeFalha = false)
                sleep(1000)
            }
        }.start()
    }

    fun consumeFromDLQ() {
        tryToConsumeMessage("${queueName}_dlq", ignorarFlagDeFalha = true)
    }

    private fun tryToConsumeMessage(queueName: String, ignorarFlagDeFalha: Boolean) {
        val queueUrl = queueUrlService.getQueueUrl(queueName)
        val receiveMessageRequest = ReceiveMessageRequest.builder().queueUrl(queueUrl).maxNumberOfMessages(10).build()
        val receiveMessage = sqsClient.receiveMessage(receiveMessageRequest)
        if (receiveMessage.hasMessages()) {
            receiveMessage.messages().forEach { message ->
                try {
                    val parsedMessageBody = Gson().fromJson(message.body(), ItemPriceUpdateMessage::class.java)
                    println("Consumindo mensagem da fila '$queueName', processando com body '$parsedMessageBody'")
                    processMessage(parsedMessageBody, ignorarFlagDeFalha = ignorarFlagDeFalha)
                    sqsClient.deleteMessage { it.queueUrl(queueUrl).receiptHandle(message.receiptHandle()) }
                } catch (e: Exception) {
                    println("Failed to consume message: $e")
                    e.printStackTrace()
                }
            }
        }
    }

    private fun processMessage(message: ItemPriceUpdateMessage, ignorarFlagDeFalha: Boolean) {
        var deveFalhar = message.devoFalhar
        if (ignorarFlagDeFalha) {
            deveFalhar = false
        }
        when (message.versionCheckMode) {
            VersionCheckMode.NO_CHECK -> itemService.updateItemPrice(message.itemId, message.price, devoFalhar = deveFalhar)
            VersionCheckMode.QUEUE_CHECK -> itemService.updateItemPriceCheckingExternalCache(message.itemId, message.price, devoFalhar = deveFalhar, messageTime = message.messageTime)
            VersionCheckMode.DB_CHECK -> itemService.updateItemPriceCheckingDatabaseVersion(message.itemId, message.price, devoFalhar = deveFalhar, messageTime = message.messageTime)
        }
    }

}