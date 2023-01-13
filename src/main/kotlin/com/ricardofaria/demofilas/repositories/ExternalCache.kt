package com.ricardofaria.demofilas.repositories

import org.springframework.stereotype.Repository
import java.util.*

@Repository
class ExternalCache {

    private val cache = mutableMapOf<UUID, Long>()

    fun cacheReset() {
        cache.clear()
    }

    fun updateItemLastVersion(itemId: UUID, version: Long) {
        if (!cache.containsKey(itemId)) {
            cache[itemId] = version
            return
        }
        if (cache[itemId]!! < version) {
            cache[itemId] = version
        }
    }

    fun versionIsNewer(itemId: UUID, version: Long): Boolean {
        if (!cache.containsKey(itemId)) {
            return true
        }
        return cache[itemId]!! < version
    }

    fun getItemVersion(itemId: UUID): Long {
        return cache[itemId]!!
    }

}