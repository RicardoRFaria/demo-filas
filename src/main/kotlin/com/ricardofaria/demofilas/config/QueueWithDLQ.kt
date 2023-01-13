package com.ricardofaria.demofilas.config

import com.google.gson.Gson
import com.ricardofaria.demofilas.messagecontracts.MessageWithDelay
import com.ricardofaria.demofilas.queueprocessors.ReceiverThatFails
import com.ricardofaria.demofilas.queueprocessors.ReceiverWithDelay
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.*
import java.lang.Thread.sleep


@Configuration
class QueueWithDLQ(@Value("\${aws.sqs.queuewithdlq.url}") private val queueName: String,
                   private val sqsClient: SqsClient,
                   private val receiverThatDoesntFail: ReceiverWithDelay,
                   private val receiverThatFails: ReceiverThatFails,
                   private val queueUrlService: QueueUrlService) {

    @Bean("queuewithdlq")
    fun createQueue() {
        val dlqCreateQueueRequest = CreateQueueRequest.builder().queueName("${queueName}_dlq").build()
        val dlqCreateResult = sqsClient.createQueue(dlqCreateQueueRequest)
        val dlqARN = sqsClient.getQueueAttributes(GetQueueAttributesRequest.builder()
                .queueUrl(dlqCreateResult.queueUrl())
                .attributeNames(QueueAttributeName.QUEUE_ARN).build()).attributes()[QueueAttributeName.QUEUE_ARN]
        val createQueueRequest = CreateQueueRequest.builder().queueName(queueName).attributes(
                mapOf(
                        QueueAttributeName.VISIBILITY_TIMEOUT to "5",
                        QueueAttributeName.REDRIVE_POLICY to "{\"maxReceiveCount\":\"3\",\"deadLetterTargetArn\":\"${dlqARN}\"}"
                )
        ).build()
        sqsClient.createQueue(createQueueRequest)
    }

    /**
     * https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/examples-sqs-messages.html#sqs-messages-receive
     */
    @EventListener(ApplicationReadyEvent::class)
    fun sqsConsumer() {
        Thread {
            val queueUrl = queueUrlService.getQueueUrl(queueName)
            while (true) {
                val receiveMessageRequest = ReceiveMessageRequest.builder().queueUrl(queueUrl).maxNumberOfMessages(10).build()
                val receiveMessage = sqsClient.receiveMessage(receiveMessageRequest)
                if (receiveMessage.hasMessages()) {
                    receiveMessage.messages().forEach { message ->
                        try {
                            val parsedMessageBody = Gson().fromJson(message.body(), MessageWithDelay::class.java)
                            receiverThatFails.receiveMessage(parsedMessageBody)
                            sqsClient.deleteMessage { it.queueUrl(queueUrl).receiptHandle(message.receiptHandle()) }
                        } catch (e: Exception) {
                            println("Failed to consume message: $e")
                            e.printStackTrace()
                        }
                    }
                }
                sleep(1000)
            }
        }.start()
    }

    @EventListener(ApplicationReadyEvent::class)
    fun sqsConsumerForDLQ() {
        Thread {
            val queueUrl = queueUrlService.getQueueUrl("${queueName}_dlq")
            while (true) {
                val receiveMessageRequest = ReceiveMessageRequest.builder().queueUrl(queueUrl).maxNumberOfMessages(10).build()
                val receiveMessage = sqsClient.receiveMessage(receiveMessageRequest)
                if (receiveMessage.hasMessages()) {
                    receiveMessage.messages().forEach { message ->
                        try {
                            val parsedMessageBody = Gson().fromJson(message.body(), MessageWithDelay::class.java)
                            receiverThatDoesntFail.receiveMessage(parsedMessageBody)
                            sqsClient.deleteMessage { it.queueUrl(queueUrl).receiptHandle(message.receiptHandle()) }
                        } catch (e: Exception) {
                            println("Failed to consume message: $e")
                            e.printStackTrace()
                        }
                    }
                }
                sleep(1000)
            }
        }.start()
    }

}