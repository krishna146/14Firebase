package com.example.a14firebase.ui.note

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.a14firebase.R
import com.example.a14firebase.databinding.ActivityAddNoteBinding
import com.example.a14firebase.models.Note
import com.example.a14firebase.repository.NoteRepository
import com.example.a14firebase.utils.*
import com.example.a14firebase.viewmodel.NoteViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddNoteActivity : AppCompatActivity() {
    private lateinit var addNoteBinding: ActivityAddNoteBinding
    private val TAG = "KRISHNA"
    var objNote: Note? = null
    var tagsList: MutableList<String> = arrayListOf()

    //ViewModel
    private lateinit var noteViewModel: NoteViewModel

    //Repository
    private lateinit var noteRepository: NoteRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addNoteBinding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(addNoteBinding.root)

        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]
        updateUI()
        observer()

    }

    private fun observer() {
        noteViewModel.addNote.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    addNoteBinding.progressBar.show()
                }
                is UiState.Failure -> {
                    addNoteBinding.progressBar.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    addNoteBinding.progressBar.hide()
                    toast(state.data.second)
                    objNote = state.data.first
                    isMakeEnableUI(false)
                    addNoteBinding.done.hide()
                    addNoteBinding.delete.show()
                    addNoteBinding.edit.show()
                }
            }
        })
        noteViewModel.updateNote.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    addNoteBinding.progressBar.show()
                }
                is UiState.Failure -> {
                    addNoteBinding.progressBar.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    addNoteBinding.progressBar.hide()
                    toast(state.data)
                    addNoteBinding.done.hide()
                    addNoteBinding.edit.show()
                    isMakeEnableUI(false)
                }
            }
        })

        noteViewModel.deleteNote.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {
                    addNoteBinding.progressBar.show()
                }
                is UiState.Failure -> {
                    addNoteBinding.progressBar.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    addNoteBinding.progressBar.hide()
                    toast(state.data)
                    startActivity(Intent(this, NoteActivity::class.java))
                }
            }
        })
    }

    private fun updateUI() {
        val sdf = SimpleDateFormat("dd MMM yyyy . hh:mm a")
        objNote = intent.getParcelableExtra("note")
        objNote?.let { note ->
            addNoteBinding.title.setText(note.title)
            addNoteBinding.date.text = sdf.format(note.date)
            tagsList = note.tags
            addTags(tagsList)
            addNoteBinding.description.setText(note.description)
            addNoteBinding.done.hide()
            addNoteBinding.edit.show()
            addNoteBinding.delete.show()
            isMakeEnableUI(false)
        } ?: run {
            addNoteBinding.title.setText("")
            addNoteBinding.date.text = sdf.format(Date())
            addNoteBinding.description.setText("")
            addNoteBinding.done.hide()
            addNoteBinding.edit.hide()
            addNoteBinding.delete.hide()
            isMakeEnableUI(true)
        }
        addNoteBinding.back.setOnClickListener {
            val intent = Intent(this, NoteActivity::class.java)
            startActivity(intent)
        }
        addNoteBinding.title.setOnClickListener {
            isMakeEnableUI(true)
        }
        addNoteBinding.description.setOnClickListener {
            isMakeEnableUI(true)
        }
        addNoteBinding.delete.setOnClickListener {
            objNote?.let { noteViewModel.deleteNote(it) }
        }
        addNoteBinding.addTagLl.setOnClickListener {
            showAddTagDialog()
        }
        addNoteBinding.edit.setOnClickListener {
            isMakeEnableUI(true)
            addNoteBinding.done.show()
            addNoteBinding.edit.hide()
            addNoteBinding.title.requestFocus()
        }
        addNoteBinding.done.setOnClickListener {
            if (validation()) {
                if (objNote == null) {
                    noteViewModel.addNote(getNote())
                } else {
                    noteViewModel.updateNote(getNote())
                }
            }
        }
        addNoteBinding.title.doAfterTextChanged {
            addNoteBinding.done.show()
            addNoteBinding.edit.hide()
        }
        addNoteBinding.description.doAfterTextChanged {
            addNoteBinding.done.show()
            addNoteBinding.edit.hide()
        }

    }

    private fun showAddTagDialog() {
        val dialog = createDialog(R.layout.add_tag_dialog, true, this)
        val button = dialog.findViewById<MaterialButton>(R.id.tag_dialog_add)
        val editText = dialog.findViewById<EditText>(R.id.tag_dialog_et)
        button.setOnClickListener {
            if (editText.text.toString().isNullOrEmpty()) {
                toast(getString(R.string.error_tag_text))
            } else {
                val text = editText.text.toString()
                tagsList.add(text)
                addNoteBinding.tags.apply {
                    addChip(text, true) {
                        tagsList.forEachIndexed { index, tag ->
                            if (text == tag) {
                                tagsList.removeAt(index)
                                addNoteBinding.tags.removeViewAt(index)
                            }
                        }

                    }
                    addNoteBinding.done.show()
                    addNoteBinding.edit.hide()
                    dialog.dismiss()
                }
            }
        }
        dialog.show()

    }

    private fun addTags(note: MutableList<String>) {
        if (note.size > 0) {
            addNoteBinding.tags.apply {
                removeAllViews()
                note.forEachIndexed { index, tag ->
                    addChip(tag, true) {
                        if (isEnabled) {
                            note.removeAt(index)
                            this.removeViewAt(index)
                        }
                    }
                }
            }
        }
    }

    private fun isMakeEnableUI(isDisable: Boolean = false) {
        addNoteBinding.title.isEnabled = isDisable
        addNoteBinding.date.isEnabled = isDisable
        addNoteBinding.tags.isEnabled = isDisable
        addNoteBinding.description.isEnabled = isDisable
    }

    private fun validation(): Boolean {
        var isValid = true
        if (addNoteBinding.title.text.toString().isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.error_title))
        }
        if (addNoteBinding.description.text.toString().isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.error_description))
        }
        return isValid
    }

    private fun getNote(): Note {
        return Note(
            id = objNote?.id ?: "",
            title = addNoteBinding.title.text.toString(),
            description = addNoteBinding.description.text.toString(),
            tags = tagsList,
            date = Date()
        )
    }
}

