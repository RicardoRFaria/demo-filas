package com.ricardofaria.demofilas.service

import com.ricardofaria.demofilas.repositories.ExternalCache
import com.ricardofaria.demofilas.repositories.ItemRepository
import com.ricardofaria.demofilas.restmodels.Item
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

@Service
class ItemService(private val itemRepository: ItemRepository, private val externalCache: ExternalCache) {

    /**
     *     ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Pipoca', 'Feita em casa, nao de cinema', 2.50, true),
     *     ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Cerveja', 'Geladinha', 2.99, true),
     *     ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Pamonha', 'Direto do carro da pamonha', 4.00, true),
     *     ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'Coca cola', 'Pro almoço de domingo', 3.50, true);
     */

    fun listAllItems(): List<Item> {
        return itemRepository.listAllItems()
    }

    @Transactional
    fun updateItemPrice(itemId: UUID, price: BigDecimal, devoFalhar: Boolean) {
        if (devoFalhar) {
            throw RuntimeException("Falhei")
        }
        itemRepository.updateItemPrice(itemId, price)
        println("Atualizei o item $itemId para o preço $price")
    }

    @Transactional
    fun updateItemPriceCheckingExternalCache(itemId: UUID, price: BigDecimal, devoFalhar: Boolean, messageTime: Long) {
        if (devoFalhar) {
            throw RuntimeException("Falhei")
        }
        if (!externalCache.versionIsNewer(itemId, messageTime)) {
            println("A versão do item $itemId é mais antiga que a mensagem recebida.  Ignorando a mensagem que alteraria o preço para $price.")
            println("Versão atual: ${externalCache.getItemVersion(itemId)}, versão da mensagem: $messageTime")
            return
        }
        itemRepository.updateItemPrice(itemId, price)
        externalCache.updateItemLastVersion(itemId, messageTime)
        println("Atualizei o item $itemId para o preço $price")
    }

    @Transactional
    fun updateItemPriceCheckingDatabaseVersion(itemId: UUID, price: BigDecimal, devoFalhar: Boolean, messageTime: Long) {
        if (devoFalhar) {
            throw RuntimeException("Falhei")
        }
        val atualizou = itemRepository.updateItemPriceIfLastUpdateIsBefore(itemId, price, messageTime)
        if (atualizou) {
            println("Atualizei o item $itemId para o preço $price")
        } else {
            println("A versão do item $itemId é mais antiga que a mensagem recebida. Ignorando a mensagem que alteraria o preço para $price.")
            println("A versão da mensagem era $messageTime, a versão atual é ${itemRepository.getItemVersion(itemId)}")
        }
    }

}