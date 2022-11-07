package com.hyunju.weatherwear.screen.main.weather

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hyunju.weatherwear.databinding.ItemWeekWeatherBinding
import com.hyunju.weatherwear.extension.clear
import com.hyunju.weatherwear.extension.load
import com.hyunju.weatherwear.model.WeekWeatherModel

class WeekWeatherAdapter : ListAdapter<WeekWeatherModel, WeekWeatherAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemWeekWeatherBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: WeekWeatherModel) = with(binding) {
            dateTextView.text = item.dayOfWeek

            weatherImageView.clear()
            weatherImageView.load(item.icon)

            temperatureTextView.text = "${item.minTemperature}° / ${item.maxTemperature}°"
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WeekWeatherAdapter.ViewHolder = ViewHolder(
        ItemWeekWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: WeekWeatherAdapter.ViewHolder, position: Int): Unit =
        holder.bind(getItem(position))

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<WeekWeatherModel>() {
            override fun areItemsTheSame(
                oldItem: WeekWeatherModel,
                newItem: WeekWeatherModel
            ): Boolean {
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(
                oldItem: WeekWeatherModel,
                newItem: WeekWeatherModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}