package com.example.a14firebase.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.a14firebase.repository.NoteRepository
import com.example.a14firebase.viewmodel.NoteViewModel

class NoteViewModelFactory(private  val noteRepository: NoteRepository) :ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NoteViewModel(noteRepository) as T
    }

}
