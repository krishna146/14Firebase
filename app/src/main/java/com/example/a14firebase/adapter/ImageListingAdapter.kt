package com.example.a14firebase.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.a14firebase.databinding.ImageItemBinding

class ImageListingAdapter(
    private val imgUriList: MutableList<Uri>
) : RecyclerView.Adapter<ImageListingAdapter.ImageListingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageListingViewHolder {
        val binding = ImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageListingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageListingViewHolder, position: Int) {
        val item = imgUriList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return imgUriList.size
    }

    fun updateList() {
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        imgUriList.removeAt(position)
        notifyDataSetChanged()
    }

    inner class ImageListingViewHolder(private val binding: ImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Uri) {
            binding.imgNote.setImageURI(item)
            binding.imgRemove.setOnClickListener {
                removeItem(absoluteAdapterPosition)
            }
        }

    }

}