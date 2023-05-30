package com.tuto.realestatemanager.ui.createproperty

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tuto.realestatemanager.databinding.ItemAddPictureRecyclerviewBinding
import com.tuto.realestatemanager.model.TemporaryPhoto

class CreatePropertyPhotoAdapter(
    //private val listener: OnDeletePhotoListener
) : ListAdapter<TemporaryPhoto, CreatePropertyPhotoAdapter.ViewHolder>(PropertyDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemAddPictureRecyclerviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), /*listener*/)
    }

    class ViewHolder(private val binding: ItemAddPictureRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tempPhoto: TemporaryPhoto,/* listener: OnDeletePhotoListener*/) {
            Glide
                .with(binding.photo)
                //.apply { RequestOptions().override(800, 800) }
                .load(tempPhoto.uri)
                .centerCrop()
                .into(binding.photo)

//            binding.deleteButton.setOnClickListener{
//                listener.onDeletePhotoListener(tempPhoto.uri) //todo utiliser ID
//            }


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

//    interface OnDeletePhotoListener{
//
//        fun onDeletePhotoListener(photoId: String)
//
//    }
}



