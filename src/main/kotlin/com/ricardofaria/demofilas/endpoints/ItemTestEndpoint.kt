package com.ricardofaria.demofilas.endpoints

import com.ricardofaria.demofilas.queuesender.ItemMessageSender
import com.ricardofaria.demofilas.restmodels.Item
import com.ricardofaria.demofilas.restmodels.ItemPriceUpdate
import com.ricardofaria.demofilas.service.ItemService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping(value = ["/items"], produces = [MediaType.APPLICATION_JSON_VALUE])
class ItemTestEndpoint(private val itemService: ItemService, private val itemMessageSender: ItemMessageSender) {

    @GetMapping
    fun listItems(): List<Item> {
        return itemService.listAllItems()
    }

    @PatchMapping("/price/{itemId}")
    fun sendSimpleMessage(@PathVariable itemId: UUID, @RequestBody itemPriceUpdate: ItemPriceUpdate): ResponseEntity<String> {
        itemMessageSender.sendItemPriceUpdateMessage(itemId, itemPriceUpdate.price, itemPriceUpdate.devoFalhar, itemPriceUpdate.versionCheckMode)
        return ResponseEntity.ok("Mensagem enviada para a fila de update de preço")
    }

    @GetMapping("/processmydlq")
    fun processMyDlq(): ResponseEntity<String> {
        itemMessageSender.processMyDlq()
        return ResponseEntity.ok("Mensagens da DLQ processadas com sucesso")
    }
}