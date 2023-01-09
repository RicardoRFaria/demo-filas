package com.ricardofaria.demofilas.ratelimiter

import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger

@Service
class QualquerInterfaceExterna {

    val rateLimitMemory = mutableMapOf<String, AtomicInteger>()

    fun increment(key: String) {
        if (!rateLimitMemory.containsKey(key)) {
            rateLimitMemory[key] = AtomicInteger(1);
        }
        rateLimitMemory[key]?.incrementAndGet()
    }

    fun getValue(key: String): Int {
        return rateLimitMemory[key]?.get() ?: 0
    }

}