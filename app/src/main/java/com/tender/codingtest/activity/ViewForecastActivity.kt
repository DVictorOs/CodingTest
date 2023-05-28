package com.tender.codingtest.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.tender.codingtest.adapter.ViewForecastAdapter
import com.tender.codingtest.constant.Constants
import com.tender.codingtest.databinding.ActivityViewForecastBinding
import com.tender.codingtest.entity.WeatherEntity
import com.tender.codingtest.model.Forecast
import com.tender.codingtest.model.WeatherResponse

class ViewForecastActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityViewForecastBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityViewForecastBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        if (intent.hasExtra(Constants.WEATHER_ENTITY)) {
            val weatherEntity = intent.getSerializableExtra(Constants.WEATHER_ENTITY) as WeatherEntity
            val weatherResponse = Gson().fromJson(weatherEntity.weatherResponse, WeatherResponse::class.java)

            val forecastList = sortForecastIntoFiveDays(weatherResponse.list)

            val adapter = ViewForecastAdapter(forecastList)

            mBinding.rvViewForecast.layoutManager = LinearLayoutManager(this)
            mBinding.rvViewForecast.setHasFixedSize(true)
            mBinding.rvViewForecast.adapter = adapter

            adapter.setOnClickListener(object : ViewForecastAdapter.OnClickListener{
                override fun onClick(forecast: Forecast) {
                    val intent = Intent(this@ViewForecastActivity, ForecastActivity::class.java)
                    intent.putExtra(Constants.LOCATION, weatherResponse.city.name)
                    intent.putExtra(Constants.FORECAST, forecast)
                    startActivity(intent)
                }
            })
        }
    }

    private fun sortForecastIntoFiveDays (forecastList : ArrayList<Forecast>) : ArrayList<Forecast> {
        val forecastListReturn : ArrayList<Forecast> = ArrayList()

        for (forecast in forecastList) {
            if (forecast.dt_txt.contains(MainActivity.TIME)) {
                forecastListReturn.add(forecast)
            }
        }
        return forecastListReturn
    }
}