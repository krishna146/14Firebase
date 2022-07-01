package com.example.a14firebase.ui.note


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
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
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class NoteDetailFragment : Fragment() {
    private var _binding: FragmentNoteDetailBinding? = null
    private val binding: FragmentNoteDetailBinding
        get() = _binding!!
    private var objNote: Note? = null
    private var tagsList: MutableList<String> = arrayListOf()
    private val noteViewModel by viewModels<NoteViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private val imgUriList: MutableList<Uri> = arrayListOf()
    private lateinit var imgAdapter: ImageListingAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initializing our adapter class
        imgAdapter = ImageListingAdapter(imgUriList, onDeleteItem = { pos, item ->
            onRemoveImage(pos, item)
        })
        binding.imgRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.imgRecyclerView.adapter = imgAdapter
        updateUI()
        bindHandlers()
        observer()

    }

    private fun updateUI() {
        //setting date format
        val sdf = SimpleDateFormat("dd MMM yyyy . hh:mm a")
        //getting out note object from note
        objNote = arguments?.getParcelable("note")
        objNote?.let { note ->
            binding.title.setText(note.title)
            binding.date.text = sdf.format(note.date)
            tagsList = note.tags
            addTags(tagsList)
            objNote!!.imgUri.forEach { imgUri ->
                imgUriList.add(imgUri.toUri())
            }
            imgAdapter.updateList()
            binding.description.setText(note.description)
            binding.doneIcon.hide()
            binding.editIcon.show()
            binding.deleteIcon.show()
            isMakeEnableUI(false)
        } ?: run {
            binding.title.setText("")
            binding.date.text = sdf.format(Date())
            binding.description.setText("")
            binding.doneIcon.hide()
            binding.editIcon.hide()
            binding.deleteIcon.hide()
            isMakeEnableUI(true)
        }

    }

    private fun bindHandlers() {
        binding.addImgIcon.setOnClickListener {
            binding.progressBar.show()
            openGallery()

        }
        binding.backIcon.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.title.setOnClickListener {
            isMakeEnableUI(true)
        }
        binding.description.setOnClickListener {
            isMakeEnableUI(true)
        }
        binding.deleteIcon.setOnClickListener {
            objNote?.let { noteViewModel.deleteNote(it) }
        }
        binding.addTagLl.setOnClickListener {
            showAddTagDialog()
        }
        binding.editIcon.setOnClickListener {
            isMakeEnableUI(true)
            binding.doneIcon.show()
            binding.editIcon.hide()
            binding.title.requestFocus()
        }
        binding.doneIcon.setOnClickListener {
            if (validation()) {
                onDonePressed()
            }
        }
        binding.title.doAfterTextChanged {
            binding.doneIcon.show()
            binding.editIcon.hide()
        }
        binding.description.doAfterTextChanged {
            binding.doneIcon.show()
            binding.editIcon.hide()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    //call back Function
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                binding.progressBar.hide()
                val data: Intent? = result.data
                val imgUri = data!!.data!!
                imgUriList.add(imgUri)
                imgAdapter.updateList()
                binding.editIcon.performClick()
            } else {
                toast("No image Selected")
                binding.progressBar.hide()
            }
        }

    private fun observer() {
        addNoteObserver()
        updateNoteObserver()
        deleteNoteObserver()
    }

    private fun onDonePressed() {
        if (imgUriList.isNotEmpty()) {
            noteViewModel.uploadMultipleImage(imgUriList) { state ->
                when (state) {
                    is UiState.Failure -> {
                        binding.progressBar.hide()
                        toast(state.error)
                    }
                    UiState.Loading -> {
                        binding.progressBar.show()

                    }
                    is UiState.Success -> {
                        binding.progressBar.hide()
                        //saveUri
                        if (objNote == null) {
                            noteViewModel.addNote(getNote())
                        } else {
                            noteViewModel.updateNote(getNote())
                        }
                    }
                }
            }
        } else {
            if (objNote == null) {
                noteViewModel.addNote(getNote())
            } else {
                noteViewModel.updateNote(getNote())
            }
        }
    }


    private fun showAddTagDialog() {
        val dialog = createDialog(R.layout.add_tag_dialog, true, requireContext())
        val button = dialog.findViewById<MaterialButton>(R.id.tag_dialog_add)
        val editText = dialog.findViewById<EditText>(R.id.tag_dialog_et)
        button.setOnClickListener {
            if (editText.text.toString().isEmpty()) {
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
                    binding.doneIcon.show()
                    binding.editIcon.hide()
                    dialog.dismiss()
                }
            }
        }
        dialog.show()

    }

    private fun addTags(tagList: MutableList<String>) {
        if (tagList.size > 0) {
            binding.tags.apply {
                removeAllViews()
                tagList.forEachIndexed { index, tag ->
                    addChip(tag, true) {
                        if (isEnabled) {
                            tagList.removeAt(index)
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
            date = Date(),
            imgUri = getImageUri()
        ).apply { authViewModel.getSession { this.user_id = it?.id ?: "" } }
    }

    private fun getImageUri(): List<String> {
        return if (imgUriList.isNotEmpty()) {
            imgUriList.map {
                it.toString()
            }
        } else {
            objNote?.imgUri ?: arrayListOf()
        }
    }

    private fun deleteNoteObserver() {
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
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun updateNoteObserver() {
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
                    binding.doneIcon.hide()
                    binding.editIcon.show()
                    isMakeEnableUI(false)
                }
            }
        }
    }

    private fun addNoteObserver() {
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
                    binding.doneIcon.hide()
                    binding.deleteIcon.show()
                    binding.editIcon.show()
                }
            }
        }
    }

    private fun onRemoveImage(pos: Int, item: Uri) {
        imgAdapter.removeItem(pos)
        binding.editIcon.performClick()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}