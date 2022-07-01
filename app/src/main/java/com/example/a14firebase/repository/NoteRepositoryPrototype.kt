package com.example.a14firebase.repository

import android.net.Uri
import com.example.a14firebase.models.Note
import com.example.a14firebase.models.User
import com.example.a14firebase.utils.UiState

interface NoteRepositoryPrototype {
//    fun getUserData(userId: String)
    fun insertNote(note: Note, result: (UiState<Pair<Note,String>>) -> Unit)
    fun getNotes(user: User?, result: (UiState<List<Note>>) -> Unit)
    fun updateNote(note: Note, result: (UiState<String>) -> Unit)
    fun deleteNote(note: Note, result: (UiState<String>) -> Unit)
    suspend fun uploadSingleImage(imgUri: Uri, result: (UiState<Uri>) -> Unit)
    suspend fun uploadMultipleImage(imgListUri: List<Uri>, result: (UiState<List<Uri>>) -> Unit)
}