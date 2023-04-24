package com.tuto.realestatemanager.ui.createproperty

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.persistableBundleOf
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tuto.realestatemanager.databinding.ItemAddPictureRecyclerviewBinding
import com.tuto.realestatemanager.model.TemporaryPhoto

class CreatePropertyPhotoAdapterTwo(
    //private val listener: OnPhotoClickListener
) : ListAdapter<TemporaryPhoto, CreatePropertyPhotoAdapterTwo.ViewHolder>(
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
        fun bind(tempPhoto: TemporaryPhoto/*, listener : CreatePropertyPhotoAdapter.OnPhotoClickListener*/) {


            Glide
                .with(binding.photo)
                .load(tempPhoto.uri)
                .into(binding.photo)

            binding.photoTitle.setText(tempPhoto.title)

            //binding.photoTitle.setOnClickListener{listener.onPhotoClicked(tempPhoto.uri)}


        }

//        override fun onClick(p0: View?) {
//            val position: Int = adapterPosition
//            if (position != RecyclerView.NO_POSITION) listener.onPhotoClicked(position)
//        }


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

//    interface OnPhotoClickListener {
//        fun onPhotoClicked(position: Int) {}
//    }

}



