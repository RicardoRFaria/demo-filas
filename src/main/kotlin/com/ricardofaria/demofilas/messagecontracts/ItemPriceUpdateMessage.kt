package com.ricardofaria.demofilas.messagecontracts

import com.ricardofaria.demofilas.restmodels.VersionCheckMode
import java.math.BigDecimal
import java.util.*

data class ItemPriceUpdateMessage(val itemId: UUID, val price: BigDecimal, val devoFalhar: Boolean, val messageTime: Long = System.currentTimeMillis(), val versionCheckMode: VersionCheckMode)
