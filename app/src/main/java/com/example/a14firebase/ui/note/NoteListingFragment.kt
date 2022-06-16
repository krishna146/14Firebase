package com.example.a14firebase.ui.note


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.a14firebase.R
import com.example.a14firebase.adapter.NoteListingAdapter
import com.example.a14firebase.databinding.FragmentNoteListingBinding
import com.example.a14firebase.utils.UiState
import com.example.a14firebase.utils.hide
import com.example.a14firebase.utils.show
import com.example.a14firebase.utils.toast
import com.example.a14firebase.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NoteListingFragment : Fragment() {
    private lateinit var noteViewModel: NoteViewModel
    private var _binding: FragmentNoteListingBinding? = null
    private val binding: FragmentNoteListingBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteListingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        //to fix the bug which was being caused due to deleting a note item
        binding.recyclerView.itemAnimator = null
        val noteListingAdapter = NoteListingAdapter(
            onItemClicked = { position, note ->
                findNavController().navigate(R.id.action_noteListingFragment_to_noteDetailFragment)
            }
        )
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = staggeredGridLayoutManager
        binding.recyclerView.adapter = noteListingAdapter
        //observing Notes Data
        noteViewModel.notes.observe(requireActivity(), Observer { state ->
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
                    noteListingAdapter.updateList(state.data.toMutableList())
                }
            }
        })

        binding.btnCreate.setOnClickListener {
            findNavController().navigate(R.id.action_noteListingFragment_to_noteDetailFragment)
        }

    }

    override fun onStart() {
        super.onStart()
        noteViewModel.getNotes()
    }


}