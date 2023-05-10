package com.tuto.realestatemanager.ui.editproperty

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tuto.realestatemanager.databinding.ItemPropertyPhotoDetailBinding
import com.tuto.realestatemanager.model.PhotoEntity

class EditPropertyPhotoAdapter : ListAdapter<PhotoEntity, EditPropertyPhotoAdapter.ViewHolder>(
    PropertyDiffCallback
) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemPropertyPhotoDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemPropertyPhotoDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: PhotoEntity) {

            Glide
                .with(binding.itemPropertyPhotoDetail)
                .load(photo.photoUri)
                .into(binding.itemPropertyPhotoDetail)

            binding.itemPropertyPhotoTitle.text = photo.photoTitle

        }
    }

    object PropertyDiffCallback : DiffUtil.ItemCallback<PhotoEntity>() {
        override fun areItemsTheSame(
            oldItem: PhotoEntity,
            newItem: PhotoEntity
        ): Boolean = oldItem.photoUri== newItem.photoUri

        override fun areContentsTheSame(
            oldItem: PhotoEntity,
            newItem: PhotoEntity
        ): Boolean = oldItem == newItem
    }

}