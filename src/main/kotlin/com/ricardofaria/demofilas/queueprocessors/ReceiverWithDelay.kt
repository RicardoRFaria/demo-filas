package com.ricardofaria.demofilas.queueprocessors

import com.ricardofaria.demofilas.messagecontracts.MessageWithDelay
import org.springframework.stereotype.Service


@Service
class ReceiverWithDelay {

    fun receiveMessage(message: MessageWithDelay) {
        println("Iniciei o consumo da mensagem <${message.texto}>")
        for (i in 1..message.numeroDeParadas) {
            println("Parada $i")
            Thread.sleep(1000)
        }
        println("Finalizei o consumo da mensagem <${message.texto}>")
    }

}