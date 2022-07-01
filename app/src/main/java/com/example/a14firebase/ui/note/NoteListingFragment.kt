package com.example.a14firebase.ui.note


import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.a14firebase.R
import com.example.a14firebase.adapter.NoteListingAdapter
import com.example.a14firebase.databinding.FragmentNoteListingBinding
import com.example.a14firebase.models.Note
import com.example.a14firebase.utils.*
import com.example.a14firebase.viewmodel.AuthViewModel
import com.example.a14firebase.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NoteListingFragment : Fragment() {
    private val noteViewModel by viewModels<NoteViewModel>()
    private lateinit var noteListingAdapter: NoteListingAdapter
    private var _binding: FragmentNoteListingBinding? = null
    private val binding: FragmentNoteListingBinding
        get() = _binding!!
    private val authViewModel by viewModels<AuthViewModel>()
    private lateinit var objNote: Note


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteListingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //to fix the bug which was being caused due to deleting a note item
        binding.recyclerView.itemAnimator = null
        noteListingAdapter = NoteListingAdapter(
            onItemClicked = { note ->
                objNote = note
                if (checkPermissionForExternalStorage()) {
                    navigateToNoteDetailFragment()
                } else {
                    startStoragePermissionRequest()
                }
            }
        )
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = staggeredGridLayoutManager
        binding.recyclerView.adapter = noteListingAdapter
        authViewModel.getSession {
            noteViewModel.getNotes(it)
        }
        observer()
        bindHandlers()
    }

    private fun bindHandlers() {
        binding.btnCreate.setOnClickListener {
            findNavController().navigate(R.id.action_noteListingFragment_to_noteDetailFragment)
        }
        binding.btnLogout.setOnClickListener {
            authViewModel.logout {
                findNavController().navigate(R.id.action_noteListingFragment_to_loginFragment)
            }
        }
    }
    //checking storage permission allowed or not
    private fun checkPermissionForExternalStorage(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    // Ex. Launching Storage read permission.
    private fun startStoragePermissionRequest() {
        permissionResultCallback.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    //callback function
    private val permissionResultCallback = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            navigateToNoteDetailFragment()
        } else {
            val dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle("Allow Storage Access")
            dialog.setMessage("Please Allow Storage Access to proceed further")
            dialog.setPositiveButton("Open App Setting") { _, _ ->
                Intent(
                    ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:${activity!!.packageName}")
                ).apply {
                    addCategory(Intent.CATEGORY_DEFAULT)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(this)
                }
            }
            dialog.setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()

            }
            dialog.create()
            dialog.show()

        }
    }

    private fun navigateToNoteDetailFragment() {
        findNavController().navigate(
            R.id.action_noteListingFragment_to_noteDetailFragment,
            Bundle().apply {
                putParcelable("note", objNote)
            })
    }

    private fun observer() {
        noteViewModel.notes.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                }
                is UiState.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error!!)
                }
                is UiState.Success -> {
                    binding.progressBar.hide()
                    Log.d(TAG, state.data.toString())
                    noteListingAdapter.updateList(state.data.toMutableList())
                }
            }
        }

    }


}