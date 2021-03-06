package com.remusrd.notesample.controller

import arrow.core.Option
import com.remusrd.notesample.domain.Note
import com.remusrd.notesample.service.NoteService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/notes")
class NoteController {
    @Autowired
    private lateinit var noteService: NoteService

    @GetMapping
    fun getAllNotes(): Option<List<Note>> {
        return noteService.getAllNotes().flatMap { Option.just(it.all) }
    }

    @PostMapping
    fun postNote(@RequestBody note: Note): Note {
        return noteService.createNote(Option(note))

    }

    @PatchMapping
    fun modifyNote() {

    }

}
