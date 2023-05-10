package com.tuto.realestatemanager.ui.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.ItemPropertyBinding

class PropertyListAdapter(private val context: Context) : ListAdapter<PropertyViewState, PropertyListAdapter.ViewHolder>(PropertyDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemPropertyBinding.inflate(LayoutInflater.from(parent.context), parent, false), context
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemPropertyBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(propertyViewState: PropertyViewState) {
            binding.propertyType.text = propertyViewState.type
            binding.propertyCounty.text = propertyViewState.city
            binding.propertyPrice.text = propertyViewState.price.toString()
            binding.itemProperty.setOnClickListener {
                propertyViewState.onItemClicked.invoke()
                //binding.itemProperty.setBackgroundColor(context.resources.getColor(R.color.teal_700))
            }

            Glide
                .with(binding.propertyPhoto)
                .load(propertyViewState.photoList.first().photoUri)
                .into(binding.propertyPhoto)
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