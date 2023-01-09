package com.ricardofaria.demofilas.ratelimiter

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class RateLimiter(private val qualquerInterfaceExterna: QualquerInterfaceExterna, @Value("\${aws.sqs.ratelimit}") private val sqsRateLimit: Int) {

    fun increment(queueName: String) {
        qualquerInterfaceExterna.increment(buildKey(queueName))
    }

    fun canProceed(queueName: String): Boolean {
        return qualquerInterfaceExterna.getValue(buildKey(queueName)) <= getQueueRateLimitThreshold()
    }

    private fun getQueueRateLimitThreshold(): Int {
        return sqsRateLimit
    }

    private fun buildKey(queueName: String): String {
        return "rlcount:$queueName:${LocalDateTime.now().minute}"
    }

}