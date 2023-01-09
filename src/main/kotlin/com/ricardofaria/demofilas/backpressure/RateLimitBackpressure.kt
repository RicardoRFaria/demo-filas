package com.ricardofaria.demofilas.backpressure

import com.ricardofaria.demofilas.ratelimiter.RateLimiter

class RateLimitBackpressure(private val queueName: String, private val rateLimiter: RateLimiter) : Backpressure {
    override fun shouldWait(): Boolean {
        return !rateLimiter.canProceed(queueName)
    }
}