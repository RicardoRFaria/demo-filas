package com.ricardofaria.demofilas.endpoints

import com.ricardofaria.demofilas.queuesender.MessageSender
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam


@Controller
class MessagesTestEndpoint(private val messageSender: MessageSender) {

    @GetMapping("/send-simple-message", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun sendSimpleMessage(@RequestParam texto: String): ResponseEntity<String> {
        messageSender.sendSimpleMessage(texto)
        return ResponseEntity.ok("Mensagem enviada")
    }

    @GetMapping("/send-message-for-queue-with-waiting", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun sendMessageForQueueWithWaiting(@RequestParam texto: String, @RequestParam numeroDeParadas: Int): ResponseEntity<String> {
        messageSender.sendMessageToDelayedQueue(texto, numeroDeParadas)
        return ResponseEntity.ok("Mensagem com aguardo enviada")
    }

    @GetMapping("/send-message-for-queue-with-dlq", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun sendMessageForQueueWithDLQ(@RequestParam texto: String, @RequestParam numeroDeParadas: Int): ResponseEntity<String> {
        messageSender.sendMessageToQueueWithDLQ(texto, numeroDeParadas)
        return ResponseEntity.ok("Mensagem com dlq enviada")
    }

    @GetMapping("/send-message-for-queue-with-health-check-backpressure", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun sendMessageForQueueWithHealthCheckBackpressure(@RequestParam texto: String): ResponseEntity<String> {
        messageSender.sendMessageForQueueWithHealthCheckBackpressure(texto)
        return ResponseEntity.ok("Mensagem para queue com health check backpressure enviada")
    }

    @GetMapping("/send-message-for-queue-with-rate-limit", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun sendMessageForQueueWithRateLimit(@RequestParam texto: String): ResponseEntity<String> {
        messageSender.sendMessageForQueueWithRateLimit(texto)
        return ResponseEntity.ok("Mensagem para queue com rate limit")
    }
}