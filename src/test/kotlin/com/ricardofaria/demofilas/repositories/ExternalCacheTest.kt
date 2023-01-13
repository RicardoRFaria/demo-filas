package com.ricardofaria.demofilas.repositories

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.util.*

class ExternalCacheTest {

    private lateinit var sut: ExternalCache
    private lateinit var itemId: UUID

    @BeforeEach
    fun setUp() {
        sut = ExternalCache()
        itemId = UUID.randomUUID()
    }

    @Test
    fun `Update last version should write value when there is no previous value`() {
        val newValue = 1L
        sut.updateItemLastVersion(itemId, newValue)
        val valueGot = sut.getItemVersion(itemId)
        assertEquals(newValue, valueGot)
    }

    @Test
    fun `Update last version should write value when the previous value is lower than the new`() {
        val previousValue = 1L
        sut.updateItemLastVersion(itemId, previousValue)

        val newValue = 2L
        sut.updateItemLastVersion(itemId, newValue)
        val valueGot = sut.getItemVersion(itemId)
        assertEquals(newValue, valueGot)
    }

    @Test
    fun `Update last version should NOT write value when the previous value is higher than the new`() {
        val previousValue = 5L
        sut.updateItemLastVersion(itemId, previousValue)

        val newValue = 4L
        sut.updateItemLastVersion(itemId, newValue)
        val valueGot = sut.getItemVersion(itemId)

        assertNotEquals(newValue, valueGot)
        assertEquals(previousValue, valueGot)
    }

    @Test
    fun `Version check should return TRUE if there is no previous value`() {
        val newValue = 2L
        val versionIsNewer = sut.versionIsNewer(itemId, newValue)
        assertTrue(versionIsNewer)
    }

    @Test
    fun `Version check should return TRUE if the value is higher than the existing`() {
        val previousValue = 1L
        sut.updateItemLastVersion(itemId, previousValue)

        val newValue = 2L
        val versionIsNewer = sut.versionIsNewer(itemId, newValue)
        assertTrue(versionIsNewer)
    }

    @Test
    fun `Version check should return FALSE the value is lower than the existing`() {
        val previousValue = 3L
        sut.updateItemLastVersion(itemId, previousValue)

        val newValue = 1L
        val versionIsNewer = sut.versionIsNewer(itemId, newValue)
        assertFalse(versionIsNewer)
    }

}