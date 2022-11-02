package com.hyunju.weatherwear.screen.main.wear

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hyunju.weatherwear.data.entity.WeatherWearEntity
import com.hyunju.weatherwear.databinding.ItemWearBinding
import com.hyunju.weatherwear.extension.clear
import com.hyunju.weatherwear.extension.load
import com.hyunju.weatherwear.util.date.setMillisDateFormat

class WearAdapter(val clickItem: (Long) -> Unit) :
    ListAdapter<WeatherWearEntity, WearAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemWearBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: WeatherWearEntity) = with(binding) {
            binding.wearImageView.clear()
            binding.wearImageView.load(item.photo, 8f)

            binding.dateTextView.text = setMillisDateFormat(item.date.time)
            binding.temperatureTextView.text =
                "최고 ${item.maxTemperature}° / 최저 ${item.minTemperature}°"
            binding.locationTextView.text = item.location

            root.setOnClickListener { clickItem(item.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WearAdapter.ViewHolder =
        ViewHolder(
            ItemWearBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: WearAdapter.ViewHolder, position: Int): Unit =
        holder.bind(getItem(position))

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<WeatherWearEntity>() {
            override fun areItemsTheSame(
                oldItem: WeatherWearEntity,
                newItem: WeatherWearEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: WeatherWearEntity,
                newItem: WeatherWearEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}