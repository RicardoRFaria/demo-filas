package com.ricardofaria.demofilas.config

import org.springframework.stereotype.Service
import software.amazon.awssdk.services.sqs.SqsClient

@Service
class QueueUrlService(private val sqsClient: SqsClient) {

    private val queueUrlCache = mutableMapOf<String, String>()
    fun getQueueUrl(queueName: String): String {
        if (queueUrlCache.containsKey(queueName)) {
            return queueUrlCache[queueName]!!
        }
        val queueUrl = sqsClient.getQueueUrl { it.queueName(queueName) }.queueUrl()
        queueUrlCache[queueName] = queueUrl
        return queueUrl
    }

}