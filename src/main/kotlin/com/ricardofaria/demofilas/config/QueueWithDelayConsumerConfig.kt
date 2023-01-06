package com.ricardofaria.demofilas.config

import com.google.gson.Gson
import com.ricardofaria.demofilas.messagecontracts.MessageWithDelay
import com.ricardofaria.demofilas.queueprocessors.ReceiverWithDelay
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
class QueueWithDelayConsumerConfig(@Value("\${aws.sqs.queuewithdelay.url}") private val queueName: String,
                                   private val sqsClient: SqsClient,
                                   private val receiver: ReceiverWithDelay) {

    @Bean("queuewithdelay")
    fun createQueue() {
        val createQueueRequest = CreateQueueRequest.builder().queueName(queueName).attributes(
                mapOf(QueueAttributeName.VISIBILITY_TIMEOUT to "5")
        ).build()
        sqsClient.createQueue(createQueueRequest)
    }

    /**
     * https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/examples-sqs-messages.html#sqs-messages-receive
     */
    @EventListener(ApplicationReadyEvent::class)
    fun sqsConsumer() {
        Thread {
            loopOfMessageGet("Thread 01")
        }.start()
        Thread {
            loopOfMessageGet("Thread 02")
        }.start()
    }

    fun loopOfMessageGet(threadName: String) {
        while (true) {
            val queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).queueUrl()
            val receiveMessageRequest = ReceiveMessageRequest.builder().queueUrl(queueUrl).maxNumberOfMessages(10).build()
            val receiveMessage = sqsClient.receiveMessage(receiveMessageRequest)
            if (receiveMessage.hasMessages()) {
                println("Sou a thread $threadName e obtive ${receiveMessage.messages().size} mensagens")
                receiveMessage.messages().forEach { message ->
                    try {
                        val parsedMessageBody = Gson().fromJson(message.body(), MessageWithDelay::class.java)
                        receiver.receiveMessage(parsedMessageBody)
                        sqsClient.deleteMessage { it.queueUrl(queueUrl).receiptHandle(message.receiptHandle()) }
                    } catch (e: Exception) {
                        println("Failed to consume message: $e")
                        e.printStackTrace()
                    }
                }
            }
            sleep(1000)
        }
    }
}