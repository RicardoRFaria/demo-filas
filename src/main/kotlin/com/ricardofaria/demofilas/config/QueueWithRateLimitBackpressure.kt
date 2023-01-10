package com.ricardofaria.demofilas.config

import com.ricardofaria.demofilas.backpressure.RateLimitBackpressure
import com.ricardofaria.demofilas.queueprocessors.SimpleReceiver
import com.ricardofaria.demofilas.ratelimiter.RateLimiter
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
class QueueWithRateLimitBackpressure(@Value("\${aws.sqs.queuewithratelimit}") private val queueName: String,
                                     private val rateLimiter: RateLimiter,
                                     private val sqsClient: SqsClient,
                                     private val simpleReceiver: SimpleReceiver) {

    private val rateLimitBackpressure = RateLimitBackpressure(queueName, rateLimiter)

    @Bean("queuewithratelimitbackpressure")
    fun createQueue() {
        val createQueueRequest = CreateQueueRequest.builder().queueName(queueName).attributes(
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
                while (rateLimitBackpressure.shouldWait()) {
                    println("aguardando para consumir mensagens, pois o backpressure com rate limit nao permitiu prosseguir")
                    sleep(5000)
                }
                val queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).queueUrl()
                val receiveMessageRequest = ReceiveMessageRequest.builder().queueUrl(queueUrl).maxNumberOfMessages(10).build()
                val receiveMessage = sqsClient.receiveMessage(receiveMessageRequest)
                if (receiveMessage.hasMessages()) {
                    receiveMessage.messages().forEach { message ->
                        try {
                            // increment on every message received
                            rateLimiter.increment(queueName)
                            simpleReceiver.receiveMessage(message.body())
                            sqsClient.deleteMessage { it.queueUrl(queueUrl).receiptHandle(message.receiptHandle()) }
                        } catch (e: Exception) {
                            println("Failed to consume message: $e")
                            e.printStackTrace()
                        }
                    }
                } else {
                    sleep(10000)
                }
                sleep(1000)
            }
        }.start()
    }
}