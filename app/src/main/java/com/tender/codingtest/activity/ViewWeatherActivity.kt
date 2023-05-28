package com.tender.codingtest.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tender.codingtest.adapter.ViewWeatherAdapter
import com.tender.codingtest.constant.Constants
import com.tender.codingtest.dao.WeatherDao
import com.tender.codingtest.database.WeatherApp
import com.tender.codingtest.databinding.ActivityViewWeatherBinding
import com.tender.codingtest.entity.WeatherEntity
import com.tender.codingtest.utils.SwipeToDeleteCallback
import kotlinx.coroutines.launch

class ViewWeatherActivity : BaseActivity() {

    private lateinit var mBinding: ActivityViewWeatherBinding

    private lateinit var mWeatherDao: WeatherDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityViewWeatherBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mWeatherDao = (application as WeatherApp).db.weatherDao()

        val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                lifecycleScope.launch {
                    val adapter = mBinding.rvViewWeather.adapter as ViewWeatherAdapter
                    val weatherEntity = adapter.weatherEntityArrayList[viewHolder.adapterPosition]
                    mWeatherDao.delete(weatherEntity)
                    adapter.removeAt(viewHolder.adapterPosition)
                }
            }
        }

        val deleteTouchHelper = ItemTouchHelper(deleteSwipeHandler)

        deleteTouchHelper.attachToRecyclerView(mBinding.rvViewWeather)

        fetchAllWeather()
    }

    private fun fetchAllWeather() {
        lifecycleScope.launch {

            mWeatherDao.fetchAllWeather().collect{
                weatherEntityList ->
                val weatherEntityArrayList = ArrayList(weatherEntityList)

                mBinding.rvViewWeather.layoutManager = LinearLayoutManager(this@ViewWeatherActivity)
                mBinding.rvViewWeather.setHasFixedSize(true)

                val adapter = ViewWeatherAdapter(weatherEntityArrayList)

                mBinding.rvViewWeather.adapter = adapter

                adapter.setOnClickListener(object: ViewWeatherAdapter.OnClickListener{
                    override fun onClick(weatherEntity: WeatherEntity) {
                        val intent = Intent(this@ViewWeatherActivity, ViewForecastActivity::class.java)
                        intent.putExtra(Constants.WEATHER_ENTITY, weatherEntity)
                        startActivity(intent)
                    }
                })
            }
        }
    }
}