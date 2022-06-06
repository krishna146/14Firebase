package com.example.a14firebase.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.a14firebase.application.NoteApplication
import com.example.a14firebase.databinding.ActivityAddNoteBinding
import com.example.a14firebase.utils.hide
import com.example.a14firebase.utils.show
import com.example.a14firebase.utils.toast
import com.example.a14firebase.models.Note
import com.example.a14firebase.repository.NoteRepository
import com.example.a14firebase.utils.UiState
import com.example.a14firebase.viewmodel.NoteViewModel
import com.example.a14firebase.viewmodelfactory.NoteViewModelFactory
import java.util.*

class AddNoteActivity : AppCompatActivity() {
    private lateinit var addNoteBinding: ActivityAddNoteBinding
    private val TAG = "KRISHNA"
    private var isedit = false

    //ViewModel
    private lateinit var noteViewModel: NoteViewModel

    //Repository
    private lateinit var noteRepository: NoteRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addNoteBinding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(addNoteBinding.root)
        updateUi()
        noteRepository = (application as NoteApplication).noteRepository

        noteViewModel = ViewModelProvider(
            this,
            NoteViewModelFactory(noteRepository)
        )[NoteViewModel::class.java]

        //inserting Note into fire store
        addNoteBinding.btnInsert.setOnClickListener {
            if (isedit) {
                updateNote()
            } else {
                addNote()
            }

        }
        //observing when note is being Updated
        noteViewModel.updateNote.observe(this, Observer { state ->
            //Observing UiState
            when (state) {
                is UiState.Loading -> {
                    addNoteBinding.btnProgressAr.show()
                    addNoteBinding.btnInsert.text = ""
                }
                is UiState.Success -> {
                    addNoteBinding.btnProgressAr.hide()
                    toast("note updated Successfully")
                    addNoteBinding.btnInsert.text = "Update"
                }
                is UiState.Failure -> {
                    addNoteBinding.btnProgressAr.hide()
                    toast(state.error)
                }
            }
        })
        //observing when note is being Added
        noteViewModel.addNote.observe(this, Observer { state ->
            //Observing UiState
            when (state) {
                is UiState.Loading -> {
                    addNoteBinding.btnProgressAr.show()
                    addNoteBinding.btnInsert.text = ""
                }
                is UiState.Success -> {
                    addNoteBinding.btnProgressAr.hide()
                    toast("note inserted Successfully")
                    addNoteBinding.etNoteMsg.text.clear()
                    addNoteBinding.btnInsert.text = "Create"
                }
                is UiState.Failure -> {
                    addNoteBinding.btnProgressAr.hide()
                    toast(state.error)
                }
            }
        })


    }

    private fun updateUi() {
        val type = intent.getStringExtra("type")
        if (type != null) {
            when (type) {
                "view" -> {
                    addNoteBinding.etNoteMsg.setText(intent.getStringExtra("txtMsg"))
                    addNoteBinding.etNoteMsg.isEnabled = false
                    addNoteBinding.btnInsert.hide()
                }
                "create" -> {
                    isedit = false
                    addNoteBinding.btnInsert.text = "insert"
                }
                "edit" -> {
                    isedit = true
                    addNoteBinding.btnInsert.text = "update"
                    val noteText = intent.getStringExtra("noteText")
                    addNoteBinding.etNoteMsg.setText(noteText)
                }
            }
        }

    }

    private fun addNote() {
        val text = addNoteBinding.etNoteMsg.text.toString()
        if (text.isNotEmpty()) {
            noteViewModel.addNote(
                Note(
                    id = "xyze",
                    text = text,
                    date = Date()
                )
            )
        } else {
            toast("please enter the message")
        }
    }

    private fun updateNote() {
        val text = addNoteBinding.etNoteMsg.text.toString()
        if (text.isNotEmpty()) {
            noteViewModel.updateNote(
                Note(
                    id = intent.getStringExtra("id")!!,
                    text = text,
                    date = Date()
                )
            )
        } else {
            toast("please enter the message")
        }

    }


//    private fun signOut() {
//        //signing out
//        Firebase.auth.signOut()
//        val intent = Intent(this, LoginActivity::class.java);
//        startActivity(intent)
//    }
}