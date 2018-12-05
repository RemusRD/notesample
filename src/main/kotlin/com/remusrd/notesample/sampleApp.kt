package com.remusrd.notesample

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient


@SpringBootApplication
@EnableEurekaClient
class NoteSampleApplication
fun main(args: Array<String>) {

    SpringApplication.run(NoteSampleApplication::class.java, *args)
}
