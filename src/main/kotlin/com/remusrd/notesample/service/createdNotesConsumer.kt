package com.remusrd.notesample.service

import com.remusrd.notesample.domain.event.NoteEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class createdNotesConsumer {


    @KafkaListener(topics =["notes"])
    fun recieve(noteEvent:NoteEvent){
        println("recibido" + noteEvent + noteEvent.javaClass)
    }
}
