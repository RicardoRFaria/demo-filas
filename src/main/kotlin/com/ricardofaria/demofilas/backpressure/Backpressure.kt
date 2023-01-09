package com.ricardofaria.demofilas.backpressure

interface Backpressure {

    fun shouldWait(): Boolean

}