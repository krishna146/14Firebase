package com.example.a14firebase.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.a14firebase.databinding.NoteSingleRowBinding
import com.example.a14firebase.models.Note
import com.example.a14firebase.utils.addChip
import com.example.a14firebase.utils.hide
import java.text.SimpleDateFormat


class NoteListingAdapter(
    val onItemClicked: (Note) -> Unit,
) : RecyclerView.Adapter<NoteListingAdapter.MyViewHolder>() {
    val sdf = SimpleDateFormat("dd MM yyyy")

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
    inner class MyViewHolder(private val noteSingleRowBinding: NoteSingleRowBinding) : RecyclerView.ViewHolder(noteSingleRowBinding.root) {
    fun bind(item: Note){
        noteSingleRowBinding.title.text = item.title
        noteSingleRowBinding.date.text = sdf.format(item.date)
        noteSingleRowBinding.tags.apply {
            if (item.tags.isNullOrEmpty()){
                hide()
            }else {
                removeAllViews()
                if (item.tags.size > 2) {
                    item.tags.subList(0, 2).forEach { tag -> addChip(tag) }
                    addChip("+${item.tags.size - 2}")
                } else {
                    item.tags.forEach { tag -> addChip(tag) }
                }
            }
        }
        noteSingleRowBinding.desc.apply {
            text = if (item.description.length > 120){
                "${item.description.substring(0,120)}..."
            }else{
                item.description
            }
        }
        noteSingleRowBinding.itemLayout.setOnClickListener { onItemClicked(item) }
    }
    }
}