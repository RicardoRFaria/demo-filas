package com.ricardofaria.demofilas

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
class DemoFilasApplication

fun main(args: Array<String>) {
    runApplication<DemoFilasApplication>(*args)
}
