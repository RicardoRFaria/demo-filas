package com.ricardofaria.demofilas.restmodels

import java.math.BigDecimal

data class ItemPriceUpdate(var price: BigDecimal,
                           var devoFalhar: Boolean,
                           var versionCheckMode: VersionCheckMode = VersionCheckMode.NO_CHECK)