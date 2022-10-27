package com.hyunju.weatherwear.screen.wirte.location

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hyunju.weatherwear.data.entity.SearchResultEntity
import com.hyunju.weatherwear.databinding.ItemSearchLocationBinding

class SearchLocationAdapter(val clickItem: (SearchResultEntity) -> Unit) :
    ListAdapter<SearchResultEntity, SearchLocationAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemSearchLocationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchResultEntity) {
            binding.locationTextView.text = item.name

            binding.root.setOnClickListener { clickItem(item) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchLocationAdapter.ViewHolder = ViewHolder(
        ItemSearchLocationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: SearchLocationAdapter.ViewHolder, position: Int): Unit =
        holder.bind(getItem(position))

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<SearchResultEntity>() {
            override fun areItemsTheSame(
                oldItem: SearchResultEntity,
                newItem: SearchResultEntity
            ): Boolean {
                return oldItem.locationLatLng == newItem.locationLatLng
            }

            override fun areContentsTheSame(
                oldItem: SearchResultEntity,
                newItem: SearchResultEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}