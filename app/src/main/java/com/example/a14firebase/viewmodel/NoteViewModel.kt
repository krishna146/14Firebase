package com.example.a14firebase.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.a14firebase.models.Note
import com.example.a14firebase.models.SignupData
import com.example.a14firebase.repository.NoteRepository
import com.example.a14firebase.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val noteRepository: NoteRepository) : ViewModel() {

    val userData: LiveData<SignupData>
        get() = noteRepository.userData


    private val _notes = MutableLiveData<UiState<List<Note>>>()
    val notes: LiveData<UiState<List<Note>>>
        get() = _notes


    private val _addNote = MutableLiveData<UiState<Pair<Note,String>>>()
    val addNote: LiveData<UiState<Pair<Note,String>>>
        get() = _addNote

    private val _updateNote = MutableLiveData<UiState<String>>()
    val updateNote: LiveData<UiState<String>>
        get() = _updateNote

    private val _deleteNote = MutableLiveData<UiState<String>>()
    val deleteNote: LiveData<UiState<String>>
        get() = _deleteNote



    //getting user data from our realtime db
//    fun getUserData(userId: String) {
//        noteRepository.getUserData(userId)
//    }

    fun getNotes() {
        _notes.postValue(UiState.Loading)
        noteRepository.getNotes {
            _notes.postValue(it)
        }
    }
    fun addNote(note: Note){
        _addNote.postValue(UiState.Loading)
        noteRepository.insertNote(note) {
            _addNote.postValue(it)
        }
    }
    fun updateNote(note: Note){
        _updateNote.postValue(UiState.Loading)
        noteRepository.updateNote(note) {
            _updateNote.postValue(it)
        }
    }

    fun deleteNote(note: Note) {
        _deleteNote.postValue(UiState.Loading)
        noteRepository.deleteNote(note){
            _deleteNote.postValue(it)
        }
    }
}