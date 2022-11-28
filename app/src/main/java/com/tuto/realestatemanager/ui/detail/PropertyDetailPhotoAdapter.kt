package com.tuto.realestatemanager.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tuto.realestatemanager.databinding.ItemPropertyBinding
import com.tuto.realestatemanager.databinding.ItemPropertyPhotoDetailBinding
import com.tuto.realestatemanager.ui.list.PropertyListAdapter
import com.tuto.realestatemanager.ui.list.PropertyViewState

class PropertyDetailPhotoAdapter : ListAdapter<String, PropertyDetailPhotoAdapter.ViewHolder>(
    PropertyDetailPhotoAdapter.PropertyDiffCallback
) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemPropertyPhotoDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemPropertyPhotoDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(propertyDetailViewState: String) {

            Glide
                .with(binding.itemPropertyPhotoDetail)
                .load(propertyDetailViewState)
                .into(binding.itemPropertyPhotoDetail)
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