package com.tuto.realestatemanager.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.ItemPropertyBinding

class PropertyListAdapter : ListAdapter<PropertyViewState, PropertyListAdapter.ViewHolder>(PropertyDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemPropertyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemPropertyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(propertyViewState: PropertyViewState) {
            binding.propertyType.text = propertyViewState.type
            binding.propertyCounty.text = propertyViewState.city
            binding.propertyPrice.text = propertyViewState.price
            binding.itemProperty.setOnClickListener {
                propertyViewState.onItemClicked.invoke()
            }

            if (propertyViewState.isSold){
                binding.isSoldText.visibility = View.VISIBLE
            }else{
                binding.isSoldText.visibility = View.GONE
            }

            val firstPhoto = propertyViewState.photoList.firstOrNull()

            if (firstPhoto != null) {
                Glide
                    .with(binding.propertyPhoto)
                    .load(firstPhoto.photoUri)
                    .centerCrop()
                    .into(binding.propertyPhoto)
            } else {
                binding.propertyPhoto.setImageResource(R.drawable.real_estate)
            }
        }
    }

    object PropertyDiffCallback : DiffUtil.ItemCallback<PropertyViewState>() {
        override fun areItemsTheSame(
            oldItem: PropertyViewState,
            newItem: PropertyViewState
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: PropertyViewState,
            newItem: PropertyViewState
        ): Boolean = oldItem == newItem
    }

}