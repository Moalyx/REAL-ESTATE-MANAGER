package com.tuto.realestatemanager.ui.createproperty

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tuto.realestatemanager.databinding.ItemSearchBinding


class SearchAdapter(
    private val listener: OnSearchClickListener,
) : ListAdapter<PredictionViewState, SearchAdapter.SearchViewHolder>(
    SearchDiffCallBack
) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(ItemSearchBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    class SearchViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(predictionViewState: PredictionViewState, listener: OnSearchClickListener) {

            binding.address.text = predictionViewState.address

            binding.rawSearchview.setOnClickListener {
                listener.onPredictionClicked(predictionViewState.id)
            }
        }
    }

    object SearchDiffCallBack : DiffUtil.ItemCallback<PredictionViewState>() {
        override fun areItemsTheSame(
            oldItem: PredictionViewState,
            newItem: PredictionViewState,
        ): Boolean = oldItem.address == newItem.address

        override fun areContentsTheSame(
            oldItem: PredictionViewState,
            newItem: PredictionViewState,
        ): Boolean = oldItem == newItem
    }

    interface OnSearchClickListener {
        fun onPredictionClicked(id: String) {}
    }


}