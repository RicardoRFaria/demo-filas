package com.ricardofaria.demofilas.repositories

import com.ricardofaria.demofilas.restmodels.Item
import org.springframework.jdbc.core.RowMapper

class ItemRowMapper : RowMapper<Item> {
    override fun mapRow(rs: java.sql.ResultSet, rowNum: Int): Item {
        return Item(
                itemId = rs.getObject("item_id", java.util.UUID::class.java),
                name = rs.getString("name"),
                description = rs.getString("description"),
                price = rs.getBigDecimal("price"),
                active = rs.getBoolean("active")
        )
    }
}