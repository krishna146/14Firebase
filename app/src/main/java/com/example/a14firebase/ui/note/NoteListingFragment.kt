package com.example.a14firebase.ui.note


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.a14firebase.R
import com.example.a14firebase.adapter.NoteListingAdapter
import com.example.a14firebase.databinding.FragmentNoteListingBinding
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
            onItemClicked = { position, note ->
                findNavController().navigate(
                    R.id.action_noteListingFragment_to_noteDetailFragment,
                    Bundle().apply {
                        putParcelable("note", note)
                    })
            }
        )
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = staggeredGridLayoutManager
        binding.recyclerView.adapter = noteListingAdapter
        observer()
        binding.btnCreate.setOnClickListener {
            findNavController().navigate(R.id.action_noteListingFragment_to_noteDetailFragment)
        }
        binding.btnLogout.setOnClickListener {
            authViewModel.logout {
                findNavController().navigate(R.id.action_noteListingFragment_to_loginFragment)
            }
        }
        authViewModel.getSession {
            noteViewModel.getNotes(it)
        }

    }

    private fun observer() {
        noteViewModel.notes.observe(viewLifecycleOwner) { state ->
            //Observing UiState
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