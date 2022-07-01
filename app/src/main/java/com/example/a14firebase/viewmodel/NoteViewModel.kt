package com.example.a14firebase.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a14firebase.models.Note
import com.example.a14firebase.models.User
import com.example.a14firebase.repository.NoteRepository
import com.example.a14firebase.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val noteRepository: NoteRepository) : ViewModel() {

    private val _notes = MutableLiveData<UiState<List<Note>>>()
    val notes: LiveData<UiState<List<Note>>>
        get() = _notes


    private val _addNote = MutableLiveData<UiState<Pair<Note, String>>>()
    val addNote: LiveData<UiState<Pair<Note, String>>>
        get() = _addNote

    private val _updateNote = MutableLiveData<UiState<String>>()
    val updateNote: LiveData<UiState<String>>
        get() = _updateNote

    private val _deleteNote = MutableLiveData<UiState<String>>()
    val deleteNote: LiveData<UiState<String>>
        get() = _deleteNote

    fun getNotes(user: User?) {
        _notes.postValue(UiState.Loading)
        noteRepository.getNotes(user) {
            _notes.postValue(it)
        }
    }

    fun addNote(note: Note) {
        _addNote.postValue(UiState.Loading)
        noteRepository.insertNote(note) {
            _addNote.postValue(it)
        }
    }

    fun updateNote(note: Note) {
        _updateNote.postValue(UiState.Loading)
        noteRepository.updateNote(note) {
            _updateNote.postValue(it)
        }
    }

    fun deleteNote(note: Note) {
        _deleteNote.postValue(UiState.Loading)
        noteRepository.deleteNote(note) {
            _deleteNote.postValue(it)
        }
    }

    fun uploadSingleImage(imgUri: Uri, onResult: (UiState<Uri>) -> Unit) {
        onResult(UiState.Loading)
        viewModelScope.launch {
            noteRepository.uploadSingleImage(imgUri) { state ->
                onResult(state)
            }
        }
    }

    fun uploadMultipleImage(imgUriList: List<Uri>, onResult: (UiState<List<Uri>>) -> Unit) {
        onResult(UiState.Loading)
        viewModelScope.launch {
            noteRepository.uploadMultipleImage(imgUriList) { state ->
                onResult(state)
            }
        }
    }
}