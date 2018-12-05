package com.remusrd.notesample.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.remusrd.notesample.domain.event.NoteEvent
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.kafka.core.*
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Configuration
class SerializationConfig {
    @Autowired
    lateinit var jackson2ObjectMapperBuilder: Jackson2ObjectMapperBuilder
    @Autowired
    lateinit var kafkaProperties: KafkaProperties

    @Bean
    fun jackson2ObjectMapperBuilderCustomizer(): Jackson2ObjectMapperBuilderCustomizer {
        return Jackson2ObjectMapperBuilderCustomizer { jacksonObjectMapperBuilder ->
            jacksonObjectMapperBuilder.deserializerByType(
                LocalDateTime::class.java, LocalDateTimeDeserializer(
                    DateTimeFormatter.ISO_DATE
                )
            )
            jacksonObjectMapperBuilder.modulesToInstall(JavaTimeModule(), KotlinModule())
        }
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, NoteEvent> {
        return KafkaTemplate<String, NoteEvent>(kafkaProducerFactory())
    }

    @Bean
    fun kafkaConsumerFactory(): ConsumerFactory<String, NoteEvent> {
        val objectMapper = jackson2ObjectMapperBuilder.build() as ObjectMapper
        objectMapper.registerModule(JavaTimeModule())
        val jsonDeserializer = JsonDeserializer<NoteEvent>(objectMapper)
        jsonDeserializer.configure(kafkaProperties.buildConsumerProperties(), false)
        return DefaultKafkaConsumerFactory<String, NoteEvent>(
            kafkaProperties.buildConsumerProperties(),
            StringDeserializer(),
            jsonDeserializer
        )
    }

    @Bean
    fun kafkaProducerFactory(): ProducerFactory<String, NoteEvent> {
        val jsonSerializer = JsonSerializer<NoteEvent>(jackson2ObjectMapperBuilder.build())
        jsonSerializer.configure(kafkaProperties.buildProducerProperties(), false)
        return DefaultKafkaProducerFactory<String, NoteEvent>(
            kafkaProperties.buildProducerProperties(),
            StringSerializer(), jsonSerializer
        )
    }

    @Bean
    fun defaultKafkaConsumerFactory(): ConsumerFactory<Any, Any> {
        val objectMapper = jackson2ObjectMapperBuilder.build() as ObjectMapper
        objectMapper.registerModule(JavaTimeModule())
        val jsonDeserializer = JsonDeserializer<Any>(objectMapper)
        jsonDeserializer.configure(kafkaProperties.buildConsumerProperties(), false)
        val kafkaConsumerFactory = DefaultKafkaConsumerFactory<Any, Any>(
            kafkaProperties.buildConsumerProperties()
        )
        kafkaConsumerFactory.setKeyDeserializer(jsonDeserializer)
        return kafkaConsumerFactory
    }

    @Bean
    fun defaultKafkaProducerFactory(): ProducerFactory<Any, Any> {
        val factory = DefaultKafkaProducerFactory<Any, Any>(
            kafkaProperties.buildProducerProperties()
        )
        val transactionIdPrefix = kafkaProperties.producer
            .transactionIdPrefix
        if (transactionIdPrefix != null) {
            factory.setTransactionIdPrefix(transactionIdPrefix)
        }
        return factory
    }
}
