package com.ricardofaria.demofilas.repositories

import com.ricardofaria.demofilas.restmodels.Item
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

@Repository
class ItemRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    fun updateItemPrice(itemId: UUID, newPrice: BigDecimal, messageTime: Long? = System.currentTimeMillis()): Boolean {
        val params = mapOf("item_id" to itemId, "new_price" to newPrice)
        return jdbcTemplate.update("UPDATE demofilas.item SET price = :new_price WHERE item_id = :item_id ", params) > 0
    }

    fun updateItemPriceIfLastUpdateIsBefore(itemId: UUID, newPrice: BigDecimal, messageTime: Long? = System.currentTimeMillis()): Boolean {
        val params = mapOf("item_id" to itemId, "new_price" to newPrice, "version" to messageTime)
        return jdbcTemplate.update("""
            UPDATE demofilas.item 
            SET price = :new_price, version = :version 
            WHERE item_id = :item_id AND version < :version """.trimMargin(), params) > 0
    }

    @Transactional
    fun updatePriceIfEventWasntProcessed(itemId: UUID, newPrice: BigDecimal, eventId: UUID): Boolean {
        val historyParam = mapOf("item_id" to itemId, "new_price" to newPrice, "event_id" to eventId)
        val affectedLines = jdbcTemplate.update("""INSERT INTO demofilas.price_change_history (item_id, new_price, event_id) 
                                                        VALUES (:item_id, :new_price, :event_id) ON CONFLICT DO NOTHING""", historyParam)
        if (affectedLines == 0) {
            return false
        }
        val itemParam = mapOf("item_id" to itemId, "new_price" to newPrice)
        return jdbcTemplate.update("UPDATE demofilas.item SET price = :new_price WHERE item_id = :item_id", itemParam) > 0
    }

    fun listAllItems(): List<Item> {
        return jdbcTemplate.query("SELECT * FROM demofilas.item ORDER BY name", emptyMap<String, Any>(), ItemRowMapper())
    }

    fun getItemVersion(itemId: UUID): Long? {
        return jdbcTemplate.queryForObject("SELECT version FROM demofilas.item WHERE item_id = :item_id", mapOf("item_id" to itemId), Long::class.java)
    }

}