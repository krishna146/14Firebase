package com.example.a14firebase.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.a14firebase.adapter.NoteListingAdapter
import com.example.a14firebase.application.NoteApplication
import com.example.a14firebase.databinding.ActivityNoteBinding
import com.example.a14firebase.models.Note
import com.example.a14firebase.utils.hide
import com.example.a14firebase.utils.show
import com.example.a14firebase.utils.toast
import com.example.a14firebase.utils.UiState
import com.example.a14firebase.viewmodel.NoteViewModel
import com.example.a14firebase.viewmodelfactory.NoteViewModelFactory

class NoteActivity : AppCompatActivity() {
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var bindingNoteActivity: ActivityNoteBinding
    //item to be deleted from recycler view position
    private var deleteItemPosition: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingNoteActivity = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(bindingNoteActivity.root)
        //to fix the bug which was being caused due to deleting a nute
        bindingNoteActivity.recyclerView.itemAnimator = null
        val noteListingAdapter = NoteListingAdapter(
            onItemClicked = { position, note ->
                val intent = Intent(this@NoteActivity, AddNoteActivity::class.java)
                intent.putExtra("txtMsg", note.text)
                intent.putExtra("type", "view");
                startActivity(intent)
            },
            onEditClicked = { position, note ->
                val intent = Intent(this@NoteActivity, AddNoteActivity::class.java)
                intent.putExtra("type", "edit");
                intent.putExtra("id", note.id)
                intent.putExtra("noteText", note.text)
                startActivity(intent)

            },
            onDeleteClicked = { position, note ->
                deleteItemPosition = position
                noteViewModel.deleteNote(note)
            }
        )
        bindingNoteActivity.recyclerView.adapter = noteListingAdapter
        val noteRepository = (application as NoteApplication).noteRepository
        noteViewModel = ViewModelProvider(
            this,
            NoteViewModelFactory(noteRepository)
        ).get(NoteViewModel::class.java)
        //observing Notes Data
        noteViewModel.notes.observe(this, Observer { state ->
            //Observing UiState
            when (state) {
                is UiState.Loading -> {
                    bindingNoteActivity.progressBar.show()
                }
                is UiState.Failure -> {
                    bindingNoteActivity.progressBar.hide()
                    toast(state.error!!)
                }
                is UiState.Success -> {
                    bindingNoteActivity.progressBar.hide()
                    noteListingAdapter.updateList(state.data.toMutableList())
                }
            }
        })
        bindingNoteActivity.btnCreate.setOnClickListener {
            val intent = Intent(this@NoteActivity, AddNoteActivity::class.java)
            intent.putExtra("type", "create");
            startActivity(intent)
        }
        //observing on deletion of notes
        noteViewModel.deleteNote.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    bindingNoteActivity.progressBar.show()
                }
                is UiState.Failure -> {
                    bindingNoteActivity.progressBar.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    bindingNoteActivity.progressBar.hide()
                    toast(state.data)
                    if (deleteItemPosition != -1)
                        noteListingAdapter.removeItem(deleteItemPosition)
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        noteViewModel.getNotes()
    }


}