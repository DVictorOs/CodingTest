package com.tender.codingtest.activity

import android.os.Bundle
import android.util.Log
import com.tender.codingtest.R
import com.tender.codingtest.constant.Constants
import com.tender.codingtest.databinding.ActivityForecastBinding
import com.tender.codingtest.model.Forecast
import java.math.RoundingMode
import java.text.NumberFormat

class ForecastActivity : BaseActivity() {

    private lateinit var mBinding: ActivityForecastBinding

    private lateinit var mLocation : String

    private lateinit var mForecast: Forecast

    private var mCelsius = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityForecastBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        if (intent.extras != null) {
            mLocation = intent.getStringExtra(Constants.LOCATION) as String
            mForecast = intent.getSerializableExtra(Constants.FORECAST) as Forecast
            Log.i("Forecast", mForecast.toString())
            setupUI()
        }
    }

    private fun setupUI () {

        mBinding.tvWeather.text = "Condition"
        mBinding.tvHumidity.text = "Humidity"
        mBinding.tvTemp.text = "Degrees"
        mBinding.tvWind.text = "Wind Speed"
        mBinding.tvLocation.text = "Location"

        // Here we update the main icon.
        when (mForecast.weather[0].icon) {
            "01d" -> mBinding.ivWeather.setImageResource(R.drawable.sunny)
            "02d" -> mBinding.ivWeather.setImageResource(R.drawable.cloud)
            "03d" -> mBinding.ivWeather.setImageResource(R.drawable.cloud)
            "04d" -> mBinding.ivWeather.setImageResource(R.drawable.cloud)
            "04n" -> mBinding.ivWeather.setImageResource(R.drawable.cloud)
            "10d" -> mBinding.ivWeather.setImageResource(R.drawable.rain)
            "11d" -> mBinding.ivWeather.setImageResource(R.drawable.storm)
            "13d" -> mBinding.ivWeather.setImageResource(R.drawable.snowflake)
            "01n" -> mBinding.ivWeather.setImageResource(R.drawable.cloud)
            "02n" -> mBinding.ivWeather.setImageResource(R.drawable.cloud)
            "03n" -> mBinding.ivWeather.setImageResource(R.drawable.cloud)
            "10n" -> mBinding.ivWeather.setImageResource(R.drawable.cloud)
            "11n" -> mBinding.ivWeather.setImageResource(R.drawable.rain)
            "13n" -> mBinding.ivWeather.setImageResource(R.drawable.snowflake)
        }

        // Forecast details.
        mBinding.tvWeatherDescription.text = mForecast.weather[0].description
        mBinding.tvHumidityDescription.text = "${mForecast.main.humidity} %"
        mBinding.tvTemperatureDescription.text = "${mForecast.main.temp} Celsius"

        // Convert wind speed in knots.
        val knots = convertWindSpeed(mForecast.wind.speed)
        mBinding.tvWindDescription.text = "$knots Knots"

        // Location and country.
        mBinding.tvLocationDescription.text = mLocation

        // Setup converter from Celsius to Fahrenheit and vice versa.
        mBinding.fabTemp.setOnClickListener {
            convertTemperatureUnit()
        }
    }

    private fun convertWindSpeed (meterPerSecond : Double) : String {
        val knots = (meterPerSecond / 1000) * 0.5399568034557235
        return roundDecimal(knots)
    }

    private fun convertTemperatureUnit () {
        var temp = mBinding.tvTemperatureDescription.text.toString()

        temp = if (temp.contains("Celsius")) {
            temp.replace(" Celsius", "")
        } else {
            temp.replace(" Fahrenheit", "")
        }

        var dblTemp = temp.toDouble()

        if (mCelsius) {
            dblTemp = (dblTemp * 1.8) + 32
        } else {
            dblTemp = (dblTemp - 32) * 0.55555555555
        }

        temp = roundDecimal(dblTemp)

        if (mCelsius) {
            temp = "$temp Fahrenheit"
            mCelsius = false
        } else {
            temp = "$temp Celsius"
            mCelsius = true
        }

        mBinding.tvTemperatureDescription.text = temp
    }

    private fun roundDecimal(double: Double) : String {
        val format: NumberFormat = NumberFormat.getInstance()
        format.roundingMode = RoundingMode.HALF_EVEN
        format.maximumFractionDigits = 2

        var string = format.format(double)
        string = string.replace(",", ".")
        return string
    }
}