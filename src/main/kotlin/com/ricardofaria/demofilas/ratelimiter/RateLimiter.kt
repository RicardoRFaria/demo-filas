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
        val key = buildKey(queueName)
        val valor = qualquerInterfaceExterna.getValue(key)
        //println("Verificando o rate limit da chave $key, valor atual: $valor")
        return valor <= getQueueRateLimitThreshold()
    }

    private fun getQueueRateLimitThreshold(): Int {
        return sqsRateLimit
    }

    private fun buildKey(queueName: String): String {
        val minute = LocalDateTime.now().minute
        val splitByOnEveryFive = if (minute == MINUTE_ZERO) {
            MINUTE_ZERO
        } else {
            minute / NUMBER_TO_GENERATE_12_TIME_WINDOWS
        }
        return "rlcount:$queueName:$splitByOnEveryFive"
    }

    companion object {
        const val MINUTE_ZERO = 0
        const val NUMBER_TO_GENERATE_12_TIME_WINDOWS = 5
    }

}