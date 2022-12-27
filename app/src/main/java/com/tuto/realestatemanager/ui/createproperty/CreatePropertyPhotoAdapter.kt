package com.tuto.realestatemanager.ui.createproperty

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tuto.realestatemanager.databinding.ItemAddPictureRecyclerviewBinding
import com.tuto.realestatemanager.databinding.ItemPropertyPhotoDetailBinding

class CreatePropertyPhotoAdapter : ListAdapter<String, CreatePropertyPhotoAdapter.ViewHolder>(
    PropertyDiffCallback
) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemAddPictureRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemAddPictureRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(createProperty: String) {


            Glide
                .with(binding.photo)
                .load(createProperty)
                .into(binding.photo)
        }
    }

    object PropertyDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean = oldItem.get(index = 0) == newItem.get(index = 0)

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean = oldItem == newItem
    }

}