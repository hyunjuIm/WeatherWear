package com.hyunju.weatherwear.screen.main.wear

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hyunju.weatherwear.data.entity.WeatherWearEntity
import com.hyunju.weatherwear.databinding.ItemWearBinding
import com.hyunju.weatherwear.extension.clear
import com.hyunju.weatherwear.extension.load
import com.hyunju.weatherwear.util.date.setTimeInMillisToStringWithDot

class WearAdapter(val clickItem: (Long) -> Unit) :
    ListAdapter<WeatherWearEntity, WearAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemWearBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: WeatherWearEntity) = with(binding) {
            wearImageView.clear()
            wearImageView.load(item.photo, 8f)

            dateTextView.text = setTimeInMillisToStringWithDot(item.date.time)

            if (item.maxTemperature != null && item.minTemperature != null) {
                temperatureTextView.text = "최고 ${item.maxTemperature}° / 최저 ${item.minTemperature}°"
            } else {
                temperatureTextView.text = "기온 정보 없음"
            }

            locationTextView.text = item.location

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