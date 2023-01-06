package com.ricardofaria.demofilas

import com.ricardofaria.demofilas.queuesender.MessageSender
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam


@Controller
class TestEndpoint(private val messageSender: MessageSender) {

    @GetMapping("/send-simple-message", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun sendSimpleMessage(@RequestParam texto: String): ResponseEntity<String> {
        messageSender.sendSimpleMessage(texto)
        return ResponseEntity.ok("Mensagem enviada")
    }

    @GetMapping("/send-message-for-queue-with-waiting", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun sendMessageForQueueWithWaiting(@RequestParam texto: String, @RequestParam numeroDeParadas: Int): ResponseEntity<String> {
        messageSender.sendMessageToDelayedQueue(texto, numeroDeParadas)
        return ResponseEntity.ok("Mensagem com aguardo enviada enviada")
    }
}