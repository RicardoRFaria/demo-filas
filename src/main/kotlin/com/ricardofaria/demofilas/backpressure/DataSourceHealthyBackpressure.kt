package com.ricardofaria.demofilas.backpressure

import org.springframework.boot.actuate.health.Status
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator
import org.springframework.stereotype.Service

@Service("dbhealthybackpressure")
class DataSourceHealthyBackpressure(private val healthIndicator: DataSourceHealthIndicator): Backpressure {
    override fun shouldWait(): Boolean {
        return healthIndicator.health().status != Status.UP
    }
}