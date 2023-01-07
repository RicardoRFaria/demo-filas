package com.ricardofaria.demofilas.endpoints

import com.ricardofaria.demofilas.queuesender.MessageSender
import com.ricardofaria.demofilas.restmodels.Item
import com.ricardofaria.demofilas.service.ItemService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam


@Controller("/items")
class ItemTestEndpoint(private val itemService: ItemService) {

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun listItems(): ResponseEntity<List<Item>> {
        return ResponseEntity.ok(itemService.listAllItems())
    }

    @PostMapping("/send-simple-message", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun sendSimpleMessage(@RequestParam texto: String): ResponseEntity<String> {
        return ResponseEntity.ok("Mensagem enviada")
    }
}