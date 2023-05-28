package com.tender.codingtest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.tender.codingtest.databinding.ItemViewWeatherBinding
import com.tender.codingtest.entity.WeatherEntity
import com.tender.codingtest.model.WeatherResponse

class ViewWeatherAdapter (val weatherEntityArrayList: ArrayList<WeatherEntity>) : RecyclerView.Adapter<ViewWeatherAdapter.MyViewHolder>() {

    private var onClickListener : OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemViewWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return weatherEntityArrayList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = weatherEntityArrayList[position]

        val weatherResponse = Gson().fromJson(model.weatherResponse, WeatherResponse::class.java)

        holder.tvCityName.text = weatherResponse.city.name
        holder.tvLatitude.text = weatherResponse.city.coord.lat.toString()
        holder.tvLongitude.text = weatherResponse.city.coord.lon.toString()

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(model)
            }
        }
    }

    inner class MyViewHolder(binding: ItemViewWeatherBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvCityName = binding.tvCityName
        val tvLatitude = binding.tvLatitude
        val tvLongitude = binding.tvLongitude
    }

    /**
     * A function for OnClickListener where the Interface is the expected parameter..
     */
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    fun removeAt(position: Int) {
        weatherEntityArrayList.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * An interface for onclick items.
     */
    interface OnClickListener {
        fun onClick(weatherEntity: WeatherEntity)
    }

}