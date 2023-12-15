package com.tuto.realestatemanager.ui.detail

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.get
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tuto.realestatemanager.databinding.ItemPropertyPhotoDetailBinding
import com.tuto.realestatemanager.model.PhotoEntity

class PropertyDetailPhotoAdapter(val onPhotoClickListener: OnPhotoClickListener) : ListAdapter<PhotoEntity, PropertyDetailPhotoAdapter.ViewHolder>(
    PropertyDiffCallback
) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemPropertyPhotoDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false), listener = onPhotoClickListener
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemPropertyPhotoDetailBinding, private val listener: OnPhotoClickListener) : RecyclerView.ViewHolder(binding.root) {
        fun bind(propertyPhotoViewState: PhotoEntity) {

            Glide
                .with(binding.itemPropertyPhotoDetail)
                .load(propertyPhotoViewState.photoUri)
                .centerCrop()
                .into(binding.itemPropertyPhotoDetail)

            Log.d("MOMO", "bind() called with: propertyPhotoViewState = ${propertyPhotoViewState.photoUri} for ${propertyPhotoViewState.photoTitle}")

            binding.itemPropertyPhotoTitle.text = propertyPhotoViewState.photoTitle
            binding.root.setOnClickListener{
                listener.onPhotoClick(propertyPhotoViewState.photoUri)
            }
        }
    }

    object PropertyDiffCallback : DiffUtil.ItemCallback<PhotoEntity>() {
        override fun areItemsTheSame(
            oldItem: PhotoEntity,
            newItem: PhotoEntity
        ): Boolean = oldItem.photoUri == newItem.photoUri
        override fun areContentsTheSame(
            oldItem: PhotoEntity,
            newItem: PhotoEntity
        ): Boolean = oldItem == newItem
    }

}

interface OnPhotoClickListener {
    fun onPhotoClick(photoUri: String)
}