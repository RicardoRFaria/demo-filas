package com.ricardofaria.demofilas.config

import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource


@Configuration
class DBHealthndicatorConfig {

    @Bean
    fun dbHealthIndicator(dataSource: DataSource): DataSourceHealthIndicator {
        return DataSourceHealthIndicator(dataSource, "SELECT 1")
    }

}