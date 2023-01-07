package com.ricardofaria.demofilas.restmodels

import java.math.BigDecimal
import java.util.*

data class Item(var itemId: UUID,
                var name: String,
                var description: String,
                var price: BigDecimal,
                var active: Boolean)