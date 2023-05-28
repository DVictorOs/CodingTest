package com.tender.codingtest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tender.codingtest.databinding.ItemForecastBinding
import com.tender.codingtest.model.Forecast

class ForecastAdapter(private val forecastArrayList: ArrayList<Forecast>) : RecyclerView.Adapter<ForecastAdapter.MyViewHolder>() {

    private var onClickListener : OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return forecastArrayList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = forecastArrayList[position]

        holder.tvDateTime.text = model.dt_txt.replace(" 12:00:00", "")

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(model)
            }
        }
    }

    inner class MyViewHolder(binding: ItemForecastBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvDateTime = binding.tvDateTime
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