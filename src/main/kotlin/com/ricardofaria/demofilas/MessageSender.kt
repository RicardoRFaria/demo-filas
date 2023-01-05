package com.ricardofaria.demofilas

import com.ricardofaria.demofilas.config.QueueConfig
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class MessageSender(private val rabbitTemplate: RabbitTemplate) {

    fun sendMessage(texto: String) {
        println("Enviando mensagem com o texto recebido: $texto")
        rabbitTemplate.convertAndSend(QueueConfig.TOPIC_EXCHANGE_NAME, "foo.bar.baz", texto);
        println("Mensagem enviada")
    }

}