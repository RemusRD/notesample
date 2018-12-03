package com.remusrd.notesample.service

import arrow.core.Option
import arrow.data.NonEmptyList
import com.remusrd.notesample.data.NoteRepository
import com.remusrd.notesample.domain.Note
import com.remusrd.notesample.domain.event.NoteEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class JpaNoteService : NoteService {
    val TOPIC_NAME = "notes"

    @Autowired
    private lateinit var noteRepository: NoteRepository
    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, NoteEvent>

    override fun getAllNotes(): Option<NonEmptyList<Note>> =
        NonEmptyList.fromList(noteRepository.findAll())

    override fun createNote(note: Option<Note>) {
        note.map {
            noteRepository.save(it)
            kafkaTemplate.send(TOPIC_NAME, NoteEvent.Created(it))
        }
    }

    @Override
    @Transactional(readOnly = true)
    override fun getNotesByAuthor(author: String): Option<NonEmptyList<Note>> {
        val noteList = noteRepository.findByAuthor(author)
        return NonEmptyList.fromList(noteList)
    }
}
