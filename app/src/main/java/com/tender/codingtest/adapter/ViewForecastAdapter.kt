package com.tender.codingtest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.tender.codingtest.databinding.ItemViewForecastBinding
import com.tender.codingtest.databinding.ItemViewWeatherBinding
import com.tender.codingtest.entity.WeatherEntity
import com.tender.codingtest.model.Forecast
import com.tender.codingtest.model.WeatherResponse

class ViewForecastAdapter(val forecastArrayList: ArrayList<Forecast>) : RecyclerView.Adapter<ViewForecastAdapter.MyViewHolder>() {
    private var onClickListener : OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemViewForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return forecastArrayList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = forecastArrayList[position]

        holder.tvDate.text = model.dt_txt.replace(" 12:00:00", "")

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(model)
            }
        }
    }

    inner class MyViewHolder(binding: ItemViewForecastBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvDate = binding.tvDate
    }

    /**
     * A function for OnClickListener where the Interface is the expected parameter..
     */
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    /**
     * An interface for onclick items.
     */
    interface OnClickListener {
        fun onClick(forecast: Forecast)
    }
}