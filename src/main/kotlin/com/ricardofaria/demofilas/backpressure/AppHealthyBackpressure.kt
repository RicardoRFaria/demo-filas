package com.ricardofaria.demofilas.backpressure

import org.springframework.stereotype.Service

@Service("apphealthybackpressure")
class AppHealthyBackpressure: Backpressure {
    override fun shouldWait(): Boolean {
        TODO("Not yet implemented")
    }
}