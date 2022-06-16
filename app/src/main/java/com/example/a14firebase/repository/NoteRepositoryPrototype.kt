package com.example.a14firebase.repository

import com.example.a14firebase.models.Note
import com.example.a14firebase.utils.UiState

interface NoteRepositoryPrototype {
//    fun getUserData(userId: String)
    fun insertNote(note: Note, result: (UiState<Pair<Note,String>>) -> Unit)
    fun getNotes(result: (UiState<List<Note>>) -> Unit)
    fun updateNote(note: Note, result: (UiState<String>) -> Unit)
    fun deleteNote(note: Note, result: (UiState<String>) -> Unit)
}