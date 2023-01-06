package com.ricardofaria.demofilas.queueprocessors

import org.springframework.stereotype.Service


@Service
class SimpleReceiver {

    fun receiveMessage(message: String) {
        println("Received <$message>")
    }

}