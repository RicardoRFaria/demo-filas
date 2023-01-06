package com.ricardofaria.demofilas.config

import com.ricardofaria.demofilas.queueprocessors.SimpleReceiver
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest
import software.amazon.awssdk.services.sqs.model.QueueAttributeName
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import java.lang.Exception
import java.lang.Thread.sleep

@Configuration
class QueueSimpleConsumerConfig(@Value("\${aws.sqs.simplesqswithconsuming.url}") private val simpleSqsWithConsuming: String,
                                private val sqsClient: SqsClient,
                                private val simpleReceiver: SimpleReceiver) {

    @Bean("simplequeue")
    fun createQueue() {
        val createQueueRequest = CreateQueueRequest.builder().queueName(simpleSqsWithConsuming).attributes(
                mapOf(QueueAttributeName.VISIBILITY_TIMEOUT to "10")
        ).build()
        sqsClient.createQueue(createQueueRequest)
    }

    /**
     * https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/examples-sqs-messages.html#sqs-messages-receive
     */
    @EventListener(ApplicationReadyEvent::class)
    fun sqsConsumer() {
        Thread {
            while (true) {
                val queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(simpleSqsWithConsuming).build()).queueUrl()
                val receiveMessageRequest = ReceiveMessageRequest.builder().queueUrl(queueUrl).maxNumberOfMessages(10).build()
                val receiveMessage = sqsClient.receiveMessage(receiveMessageRequest)
                if (receiveMessage.hasMessages()) {
                    receiveMessage.messages().forEach { message ->
                        try {
                            simpleReceiver.receiveMessage(message.body())
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