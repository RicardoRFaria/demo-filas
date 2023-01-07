package com.ricardofaria.demofilas.service

import com.ricardofaria.demofilas.repositories.ItemRepository
import com.ricardofaria.demofilas.restmodels.Item
import org.springframework.stereotype.Service

@Service
class ItemService(private val itemRepository: ItemRepository) {

    companion object {
        /**
         *     ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Pipoca', 'Feita em casa, nao de cinema', 2.50, true),
         *     ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Cerveja', 'Geladinha', 2.99, true),
         *     ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Pamonha', 'Direto do carro da pamonha', 4.00, true),
         *     ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'Coca cola', 'Pro almoço de domingo', 3.50, true);
         */
    }

    fun listAllItems(): List<Item> {
        return itemRepository.listAllItems()
    }


}