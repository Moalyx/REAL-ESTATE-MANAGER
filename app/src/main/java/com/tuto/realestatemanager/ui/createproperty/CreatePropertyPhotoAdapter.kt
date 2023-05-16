package com.tuto.realestatemanager.ui.createproperty

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tuto.realestatemanager.databinding.ItemAddPictureRecyclerviewBinding
import com.tuto.realestatemanager.model.TemporaryPhoto

class CreatePropertyPhotoAdapter(
    //private val listener: OnPhotoClickListener
) : ListAdapter<TemporaryPhoto, CreatePropertyPhotoAdapter.ViewHolder>(PropertyDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemAddPictureRecyclerviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemAddPictureRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tempPhoto: TemporaryPhoto) {
            Glide
                .with(binding.photo)
                .load(tempPhoto.uri)
                .into(binding.photo)

            binding.photoTitle.text = tempPhoto.title
        }
    }

    object PropertyDiffCallback : DiffUtil.ItemCallback<TemporaryPhoto>() {
        override fun areItemsTheSame(
            oldItem: TemporaryPhoto,
            newItem: TemporaryPhoto
        ): Boolean = oldItem.uri == newItem.uri

        override fun areContentsTheSame(
            oldItem: TemporaryPhoto,
            newItem: TemporaryPhoto
        ): Boolean = oldItem == newItem
    }
}



