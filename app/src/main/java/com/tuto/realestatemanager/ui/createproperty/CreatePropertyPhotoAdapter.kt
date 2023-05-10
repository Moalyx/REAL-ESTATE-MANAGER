package com.tuto.realestatemanager.ui.createproperty

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tuto.realestatemanager.databinding.ItemAddPictureRecyclerviewBinding
import com.tuto.realestatemanager.databinding.ItemPropertyPhotoDetailBinding
import com.tuto.realestatemanager.model.PhotoEntity

class CreatePropertyPhotoAdapter(
    //private val listener: OnPhotoClickListener
) : ListAdapter<PhotoEntity, CreatePropertyPhotoAdapter.ViewHolder>(
    PropertyDiffCallback
) {

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

    inner class ViewHolder(private val binding: ItemAddPictureRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root)/*,View.OnClickListener*/ {
        fun bind(photo: PhotoEntity) {


            Glide
                .with(binding.photo)
                .load(photo)
                .into(binding.photo)

            binding.photoTitle.text = photo.photoTitle

        }

//        override fun onClick(p0: View?) {
//            val position: Int = adapterPosition
//            if (position != RecyclerView.NO_POSITION) listener.onPhotoClicked(position)
//        }


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

    interface OnPhotoClickListener {
        fun onPhotoClicked(position: Int) {}
    }

}