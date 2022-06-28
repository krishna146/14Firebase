package com.example.a14firebase.ui.note

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a14firebase.R
import com.example.a14firebase.adapter.ImageListingAdapter
import com.example.a14firebase.databinding.FragmentNoteDetailBinding
import com.example.a14firebase.models.Note
import com.example.a14firebase.utils.*
import com.example.a14firebase.viewmodel.AuthViewModel
import com.example.a14firebase.viewmodel.NoteViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class NoteDetailFragment : Fragment() {
    private var _binding: FragmentNoteDetailBinding? = null
    private val binding: FragmentNoteDetailBinding
        get() = _binding!!
    var objNote: Note? = null
    var tagsList: MutableList<String> = arrayListOf()
    private val noteViewModel by viewModels<NoteViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private val imgUriList: MutableList<Uri> = arrayListOf()
    private val imgAdapter by lazy {
        ImageListingAdapter(imgUriList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imgRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.imgRecyclerView.adapter = imgAdapter
        updateUI()
        observer()

    }

    private fun observer() {
        noteViewModel.addNote.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                }
                is UiState.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.progressBar.hide()
                    toast(state.data.second)
                    objNote = state.data.first
                    isMakeEnableUI(false)
                    binding.done.hide()
                    binding.delete.show()
                    binding.edit.show()
                }
            }
        }
        noteViewModel.updateNote.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                }
                is UiState.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.progressBar.hide()
                    toast(state.data)
                    binding.done.hide()
                    binding.edit.show()
                    isMakeEnableUI(false)
                }
            }
        }

        noteViewModel.deleteNote.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                }
                is UiState.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.progressBar.hide()
                    toast(state.data)
                }
            }
        }
    }

    private fun updateUI() {
        val sdf = SimpleDateFormat("dd MMM yyyy . hh:mm a")
        objNote = arguments?.getParcelable("note")
        objNote?.let { note ->
            binding.title.setText(note.title)
            binding.date.text = sdf.format(note.date)
            tagsList = note.tags
            addTags(tagsList)
            binding.description.setText(note.description)
            binding.done.hide()
            binding.edit.show()
            binding.delete.show()
            isMakeEnableUI(false)
        } ?: run {
            binding.title.setText("")
            binding.date.text = sdf.format(Date())
            binding.description.setText("")
            binding.done.hide()
            binding.edit.hide()
            binding.delete.hide()
            isMakeEnableUI(true)
        }

        binding.imgAdd.setOnClickListener {
            binding.progressBar.show()
            ImagePicker.with(this)
                .compress(1024)
                .galleryOnly()
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }
        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.title.setOnClickListener {
            isMakeEnableUI(true)
        }
        binding.description.setOnClickListener {
            isMakeEnableUI(true)
        }
        binding.delete.setOnClickListener {
            objNote?.let { noteViewModel.deleteNote(it) }
        }
        binding.addTagLl.setOnClickListener {
            showAddTagDialog()
        }
        binding.edit.setOnClickListener {
            isMakeEnableUI(true)
            binding.done.show()
            binding.edit.hide()
            binding.title.requestFocus()
        }
        binding.done.setOnClickListener {
            if (validation()) {
                if (objNote == null) {
                    noteViewModel.addNote(getNote())
                } else {
                    noteViewModel.updateNote(getNote())
                }
            }
        }
        binding.title.doAfterTextChanged {
            binding.done.show()
            binding.edit.hide()
        }
        binding.description.doAfterTextChanged {
            binding.done.show()
            binding.edit.hide()
        }

    }

    //call back Function when user picks the image
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
                Log.d(TAG, fileUri.toString())
                imgUriList.add(fileUri)
                imgAdapter.updateList()
                binding.progressBar.hide()
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                binding.progressBar.hide()
                toast(ImagePicker.getError(data))
            } else {
                binding.progressBar.hide()
                Log.e(TAG, "Task Cancelled")
            }
        }

    private fun showAddTagDialog() {
        val dialog = createDialog(R.layout.add_tag_dialog, true, requireContext())
        val button = dialog.findViewById<MaterialButton>(R.id.tag_dialog_add)
        val editText = dialog.findViewById<EditText>(R.id.tag_dialog_et)
        button.setOnClickListener {
            if (editText.text.toString().isNullOrEmpty()) {
                toast(getString(R.string.error_tag_text))
            } else {
                val text = editText.text.toString()
                tagsList.add(text)
                binding.tags.apply {
                    addChip(text, true) {
                        tagsList.forEachIndexed { index, tag ->
                            if (text == tag) {
                                tagsList.removeAt(index)
                                binding.tags.removeViewAt(index)
                            }
                        }

                    }
                    binding.done.show()
                    binding.edit.hide()
                    dialog.dismiss()
                }
            }
        }
        dialog.show()

    }

    private fun addTags(note: MutableList<String>) {
        if (note.size > 0) {
            binding.tags.apply {
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
        binding.title.isEnabled = isDisable
        binding.date.isEnabled = isDisable
        binding.tags.isEnabled = isDisable
        binding.description.isEnabled = isDisable
    }

    private fun validation(): Boolean {
        var isValid = true
        if (binding.title.text.toString().isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.error_title))
        }
        if (binding.description.text.toString().isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.error_description))
        }
        return isValid
    }

    private fun getNote(): Note {
        return Note(
            id = objNote?.id ?: "",
            title = binding.title.text.toString(),
            description = binding.description.text.toString(),
            tags = tagsList,
            date = Date()
        ).apply { authViewModel.getSession { this.user_id = it?.id ?: "" } }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}