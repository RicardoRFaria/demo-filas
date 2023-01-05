package com.ricardofaria.demofilas

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam


@Controller
class TestEndpoint(private val messageSender: MessageSender) {

    @GetMapping("/send-simple-message", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun testExistent(@RequestParam texto: String): ResponseEntity<String> {
        messageSender.sendMessage(texto)
        return ResponseEntity.ok("Mensagem enviada")
    }
}