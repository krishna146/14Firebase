package com.example.a14firebase.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.a14firebase.databinding.NoteSingleRowBinding
import com.example.a14firebase.models.Note


class NoteListingAdapter(
    val onItemClicked: (Int, Note) -> Unit,
    val onEditClicked: (Int, Note) -> Unit,
    val onDeleteClicked: (Int,Note) -> Unit
) : RecyclerView.Adapter<NoteListingAdapter.MyViewHolder>() {

    private var list: MutableList<Note> = arrayListOf()

    //creating viewHolder
    //standard method of recycler view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //binding object for out item
        val itemView = NoteSingleRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        //returning viewholder
        return MyViewHolder(itemView)
    }
    //binding data with viewHolder
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }
    override fun getItemCount(): Int {
        return list.size
    }
    //

    fun updateList(list: MutableList<Note>){
        this.list = list
        notifyDataSetChanged()
    }

    fun removeItem(position: Int){
        list.removeAt(position)
        notifyItemChanged(position)
    }



//there is no significance of ViewHolder without adapter, hence nested class
    inner class MyViewHolder(private val binding: NoteSingleRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Note){
            binding.noteIdValue.text = item.id
            binding.msg.text = item.text
            binding.edit.setOnClickListener { onEditClicked(bindingAdapterPosition,item) }
            binding.delete.setOnClickListener { onDeleteClicked(bindingAdapterPosition,item) }
            binding.itemLayout.setOnClickListener { onItemClicked(bindingAdapterPosition,item) }
        }
    }
}