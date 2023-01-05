package com.ricardofaria.demofilas

import org.springframework.stereotype.Service


@Service
class SimpleReceiver {

    fun receiveMessage(message: String) {
        println("Received <$message>")
    }

}