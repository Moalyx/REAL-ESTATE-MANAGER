package com.tuto.realestatemanager.ui.editproperty

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tuto.realestatemanager.databinding.ItemPropertyPhotoDetailBinding
import com.tuto.realestatemanager.model.PhotoEntity

class EditPropertyPhotoAdapter(
    private val listener: OnDeletePhotoListener
) : ListAdapter<EditPropertyPhotoViewState, EditPropertyPhotoAdapter.ViewHolder>(
    PropertyDiffCallback
) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemPropertyPhotoDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    class ViewHolder(private val binding: ItemPropertyPhotoDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: EditPropertyPhotoViewState, listener: OnDeletePhotoListener) {

            Glide
                .with(binding.itemPropertyPhotoDetail)
                .load(photo.photoUri)
                .centerCrop()
                .into(binding.itemPropertyPhotoDetail)

            binding.itemPropertyPhotoTitle.text = photo.photoTitle
//
//            binding.de.setOnClickListener{  //todo implemente delete dans la recyclerview de l'edit activity
//                listener.onDeletePhotoListener(photo.id)
//            }

        }
    }

    object PropertyDiffCallback : DiffUtil.ItemCallback<EditPropertyPhotoViewState>() {
        override fun areItemsTheSame(
            oldItem: EditPropertyPhotoViewState,
            newItem: EditPropertyPhotoViewState
        ): Boolean = oldItem.photoUri== newItem.photoUri

        override fun areContentsTheSame(
            oldItem: EditPropertyPhotoViewState,
            newItem: EditPropertyPhotoViewState
        ): Boolean = oldItem == newItem
    }

    interface OnDeletePhotoListener {

        fun onDeletePhotoListener(photoId: Long)

    }

}