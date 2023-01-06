package com.ricardofaria.demofilas.queueprocessors

import com.ricardofaria.demofilas.messagecontracts.MessageWithDelay
import org.springframework.stereotype.Service


@Service
class ReceiverThatFails {

    fun receiveMessage(message: MessageWithDelay) {
        println("Iniciei o consumo da mensagem <${message.texto}> mas vou falhar")
        Thread.sleep(2000)
        println("Infelizmente falhei, vou lançar exception")
        throw RuntimeException("Falhei")
    }

}