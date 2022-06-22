package com.example.a14firebase.repository

import com.example.a14firebase.models.Note
import com.example.a14firebase.models.User
import com.example.a14firebase.utils.FireStoreCollection.NOTE
import com.example.a14firebase.utils.FireStoreDocumentField
import com.example.a14firebase.utils.FireStoreDocumentField.USER_ID
import com.example.a14firebase.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val database: FirebaseFirestore
) : NoteRepositoryPrototype {

    override fun insertNote(note: Note, result: (UiState<Pair<Note, String>>) -> Unit) {
        val document = database.collection(NOTE).document()
        note.id = document.id
        document
            .set(note)
            .addOnSuccessListener {
                result(UiState.Success(Pair(note, "Note has been created successfully")))
            }
            .addOnFailureListener {
                result(UiState.Failure(it.localizedMessage.toString()))
            }
    }

    override fun getNotes(user: User?, result: (UiState<List<Note>>) -> Unit) {
        database.collection(NOTE)
            .whereEqualTo(USER_ID, user?.id)
            .orderBy(FireStoreDocumentField.DATE, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                val notes = arrayListOf<Note>()
                it.forEach { document ->
                    //converting our document to Note java object
                    val note = document.toObject(Note::class.java)
                    notes.add(note)

                }
                result(UiState.Success(notes))

            }
            .addOnFailureListener {
                result(UiState.Failure(it.localizedMessage.toString()))
            }
    }

    override fun updateNote(note: Note, result: (UiState<String>) -> Unit) {
        val document = database.collection(NOTE).document(note.id)
        note.id = document.id
        document
            .set(note)
            .addOnSuccessListener {
                result(UiState.Success("Note has been update successfully"))
            }
            .addOnFailureListener {
                result(UiState.Failure(it.localizedMessage.toString()))
            }
    }

    override fun deleteNote(note: Note, result: (UiState<String>) -> Unit) {
        val document = database.collection(NOTE).document(note.id)
        note.id = document.id
        document
            .delete()
            .addOnSuccessListener {
                result(UiState.Success("Note has been deleted successfully"))
            }
            .addOnFailureListener {
                result(UiState.Failure(it.localizedMessage.toString()))
            }
    }
}