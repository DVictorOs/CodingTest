package com.tender.codingtest.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.CancelableCallback
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.tender.codingtest.R
import com.tender.codingtest.adapter.ForecastAdapter
import com.tender.codingtest.constant.Constants
import com.tender.codingtest.dao.WeatherDao
import com.tender.codingtest.database.WeatherApp
import com.tender.codingtest.databinding.ActivityMainBinding
import com.tender.codingtest.databinding.DialogWeatherBinding
import com.tender.codingtest.entity.WeatherEntity
import com.tender.codingtest.model.Forecast
import com.tender.codingtest.model.WeatherResponse
import com.tender.codingtest.network.WeatherService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : BaseActivity(), OnMapReadyCallback, OnMapClickListener, OnMarkerClickListener, OnInfoWindowClickListener, NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mBinding : ActivityMainBinding

    private lateinit var mGoogleMap: GoogleMap

    private var mMarker: Marker? = null

    private lateinit var mWeatherDao: WeatherDao

    private lateinit var mWeatherResponse: WeatherResponse

    companion object {
        const val ZOOM = 9f
        const val TIME = " 12:00:00"
    }

    private val autocompleteLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(result.data!!)
            moveCamera(place.latLng)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mWeatherDao = (application as WeatherApp).db.weatherDao()

        setupActionBar()
        setupUI()

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupActionBar() {
        setSupportActionBar(mBinding.appBarMain.toolbarMainActivity)

        mBinding.appBarMain.toolbarMainActivity.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_action_nav_menu)

        mBinding.appBarMain.toolbarMainActivity.setNavigationOnClickListener {
            toggleDrawer()
        }

        mBinding.navView.setNavigationItemSelectedListener(this)
    }

    private fun toggleDrawer() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            mBinding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setupUI () {

        mBinding.appBarMain.contentMain.pbMain.visibility = View.GONE

        mBinding.appBarMain.contentMain.btnSearch.setOnClickListener {

            if (!Places.isInitialized()) {
                Places.initialize(this, Constants.MAP_API_KEY, Locale.getDefault())
            }

            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .setCountries(listOf("ZA"))
                .build(this)
            autocompleteLauncher.launch(intent)

        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.mGoogleMap = googleMap

        this.mGoogleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(this, com.tender.codingtest.R.raw.style_json)
        )

        this.mGoogleMap.setOnMapClickListener(this)
        this.mGoogleMap.setOnInfoWindowClickListener(this)
        this.mGoogleMap.setOnMarkerClickListener(this)
    }

    override fun onMapClick(latLng: LatLng) {

        lifecycleScope.launch {
            val addresses: MutableList<Address>
            val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1) as MutableList<Address>
            if (addresses.size > 0) {
                val address = addresses[0].getAddressLine(0)
                moveCamera(latLng, address)
            } else {
                moveCamera(latLng)
            }
        }
    }

    private fun moveCamera(latLng: LatLng, address : String = "") {
        mGoogleMap.animateCamera(
            CameraUpdateFactory.newLatLng(latLng), object : CancelableCallback {
                override fun onCancel() {}
                override fun onFinish() {
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM))

                    mMarker?.remove()
                    val markerOptions = MarkerOptions()
                    markerOptions.position(latLng)
                    markerOptions.icon(bitmapDescriptorFromVector(this@MainActivity, R.drawable.ic_marker))
                    if (address.isNotEmpty()) {
                        markerOptions.title(address)
                    }
                    mMarker = mGoogleMap.addMarker(markerOptions)!!
                    if (address.isNotEmpty()) {
                        mMarker!!.showInfoWindow()
                    }
                }
            })
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    override fun onInfoWindowClick(marker: Marker) {
        onMarkerClick(marker)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        lifecycleScope.launch {
            val latitude = marker.position.latitude
            val longitude = marker.position.longitude
            mBinding.appBarMain.contentMain.pbMain.visibility = View.VISIBLE
            getLocationWeatherDetails(latitude, longitude)
        }
        return true
    }

    private fun getLocationWeatherDetails(latitude : Double, longitude : Double) =
        if (Constants.isNetworkAvailable(this)) {
            val retrofit : Retrofit = Retrofit.Builder()
                .baseUrl(Constants.WEATHER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service : WeatherService = retrofit
                .create<WeatherService>(WeatherService::class.java)

            val listCall : Call<WeatherResponse> = service.getWeather(latitude, longitude, Constants.METRIC_UNIT, Constants.WEATHER_API_KEY)

            listCall.enqueue(object : Callback<WeatherResponse>{
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        mWeatherResponse = response.body() as WeatherResponse
                        Log.i("Weather Response", "${mWeatherResponse.city.coord}")

                        showDialogWeatherForecast(mWeatherResponse)
                    } else {
                        when(response.code()) {
                            400 -> { Log.e("Error 400", "Bad Connection")}
                            404 -> { Log.e("Error 404", "Not Found")}
                            else -> { Log.e("Error", "Generic Error") }
                        }
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.e("Error", t.message.toString())
                }
            })
        } else {
            showErrorSnackBar("No internet connection")
        }

    private fun showDialogWeatherForecast (weatherResponse: WeatherResponse) {
        val dialogWeatherForecast = Dialog(this, R.style.MyDialogTheme)
        val dialogBinding = DialogWeatherBinding.inflate(layoutInflater)
        dialogWeatherForecast.setContentView(dialogBinding.root)
        dialogBinding.tvCityName.text = "${weatherResponse.city.name}, ${weatherResponse.city.country}"

        dialogBinding.btnSave.setOnClickListener {
            addRecord(mWeatherDao)
            dialogWeatherForecast.dismiss()
        }

        var forecastList = weatherResponse.list
        forecastList = sortForecastIntoFiveDays(forecastList)
        dialogBinding.rvWeather.layoutManager = LinearLayoutManager(this)
        dialogBinding.rvWeather.setHasFixedSize(true)
        val forecastAdapter = ForecastAdapter(forecastList)

        dialogBinding.rvWeather.adapter = forecastAdapter

        forecastAdapter.setOnClickListener(object: ForecastAdapter.OnClickListener{
            override fun onClick(forecast: Forecast) {
                val intent = Intent(this@MainActivity, ForecastActivity::class.java)
                intent.putExtra(Constants.LOCATION, "${weatherResponse.city.name}, ${weatherResponse.city.country}")
                intent.putExtra(Constants.FORECAST, forecast)
                startActivity(intent)
            }
        })

        mBinding.appBarMain.contentMain.pbMain.visibility = View.GONE
        dialogWeatherForecast.show()
    }

    private fun addRecord(weatherDao: WeatherDao) {
        lifecycleScope.launch {
            weatherDao.insert(WeatherEntity(weatherResponse = Gson().toJson(mWeatherResponse)))
            showNotificationSnackBar("Weather forecast saved")
        }
    }

    private fun sortForecastIntoFiveDays (forecastList : ArrayList<Forecast>) : ArrayList<Forecast> {
        val forecastListReturn : ArrayList<Forecast> = ArrayList()

        for (forecast in forecastList) {
            if (forecast.dt_txt.contains(TIME)) {
                forecastListReturn.add(forecast)
            }
        }
        return forecastListReturn
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_view_weather -> {
                startActivity(Intent(this, ViewWeatherActivity::class.java))
            }
        }
        mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

}