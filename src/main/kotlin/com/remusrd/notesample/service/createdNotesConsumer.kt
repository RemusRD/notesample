package com.remusrd.notesample.service

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.Message
import org.springframework.stereotype.Component

@Component
class createdNotesConsumer {


    @KafkaListener(topics = ["notes"], groupId = "noteGroup")
    fun recieve(noteEvent: Message<Any>) {
        println("received" + noteEvent + noteEvent.javaClass)
    }
}
