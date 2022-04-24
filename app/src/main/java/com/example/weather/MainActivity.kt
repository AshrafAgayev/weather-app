package com.example.weather

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.*
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.StringRequest
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.navigation.NavigationView
import im.delight.android.location.SimpleLocation
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.security.Permission
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    lateinit var toggle: ActionBarDrawerToggle

    var lang = "en"
    var location: SimpleLocation? = null
    var latitude: String? = null
    var longitude: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        requestPermissions()

        location = SimpleLocation(this)
        latitude = String.format("%.6f", location?.latitude)
        longitude = String.format("%.6f", location?.longitude)
        loadDataOfCity(latitude, longitude)
        dailyForecast(latitude, longitude)


}
//END OF ONCREATE

fun hasLocatioPermissions() =
    EasyPermissions.hasPermissions(
        this,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )


fun requestPermissions() {
    if (hasLocatioPermissions()) {
        return
    }
    EasyPermissions.requestPermissions(
        this,
        "You need to accept location permission",
        10,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
}


override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    //Simple location library vasitesile latitude ve longitude tapiriq
    location = SimpleLocation(this)
    latitude = String.format("%.6f", location?.latitude)
    longitude = String.format("%.6f", location?.longitude)

    // Gunluk ve 7gunluk forecasti yukleyirik
    loadDataOfCity(latitude, longitude)
    dailyForecast(latitude, longitude)
}

override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
    if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
        AppSettingsDialog.Builder(this).build().show()
    } else {
        requestPermissions()
    }
}


override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
) {

    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)

}

private fun loadDataOfCity(latitude: String?, longitude: String?) {
    val url =
        "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&units=metric&appid=ef11e121e1385670fb44c37d6dd6d120"

    val queue = Volley.newRequestQueue(this)
    val havaJsonRequest = JsonObjectRequest(url,
        { response ->
            //JSON
            val main = response.getJSONObject("main")
            val temprature = main.getInt("temp")
            val feelslike = main.getString("feels_like")

            val city = response.getString("name")

            val weatherMain = response.getJSONArray("weather")
            val description = weatherMain.getJSONObject(0).getString("description")

            findViewById<TextView>(R.id.tvTemp).text = temprature.toString()
            findViewById<TextView>(R.id.tvFeelslike).text = feelslike.dropLast(3)
            findViewById<TextView>(R.id.tvDescription).text = description
            findViewById<TextView>(R.id.tvCity).text = city
            findViewById<TextView>(R.id.tvDate).text = bringDate()

            val iconCode = weatherMain.getJSONObject(0).getString("icon")
            val icon = findViewById<ImageView>(R.id.imgIcon)
            val background = R.drawable.backgroundnight
            val constraint = findViewById<ConstraintLayout>(R.id.constraint)


            when (iconCode) {
                "01d" -> icon.setImageResource(R.drawable.sun)
                "01n" -> {
                    icon.setImageResource(R.drawable.moon)
                    constraint.setBackgroundResource(background)
                }
                "02d" -> icon.setImageResource(R.drawable.sunny)
                "02n" -> {
                    icon.setImageResource(R.drawable.cloud)
                    constraint.setBackgroundResource(background)
                }
                "03d" -> icon.setImageResource(R.drawable.cloud)
                "03n" -> {
                    icon.setImageResource(R.drawable.cloud)
                    constraint.setBackgroundResource(background)
                }
                "04d" -> icon.setImageResource(R.drawable.broken)
                "04n" -> {
                    icon.setImageResource(R.drawable.broken)
                    constraint.setBackgroundResource(background)
                }
                "09d" -> icon.setImageResource(R.drawable.shower)
                "09n" -> {
                    icon.setImageResource(R.drawable.shower)
                    constraint.setBackgroundResource(background)
                }
                "10d" -> icon.setImageResource(R.drawable.rain)
                "10n" -> {
                    icon.setImageResource(R.drawable.rainnight)
                    constraint.setBackgroundResource(background)
                }
                "13d" -> icon.setImageResource(R.drawable.snow)
                "13n" -> {
                    icon.setImageResource(R.drawable.snow)
                    constraint.setBackgroundResource(background)
                }
                "50d" -> icon.setImageResource(R.drawable.fog)
                "50n" -> {
                    icon.setImageResource(R.drawable.fog)
                    constraint.setBackgroundResource(background)

                }
                else -> icon.isVisible = false

            }
        },
        { error ->
            Toast.makeText(this, "Weather data couldn't loaded", Toast.LENGTH_LONG).show()
        })

// Add the request to the RequestQueue.
    queue.add(havaJsonRequest)
}


// 7 Gunluk hava melumatini API call etmek
//
private fun dailyForecast(latitude: String?, longitude: String?) {
    val url =
        "https://api.openweathermap.org/data/2.5/onecall?lat=" + latitude + "&lon=" + longitude + "&units=metric&exclude=minutely,hourly&appid=ef11e121e1385670fb44c37d6dd6d120"


    val queue = Volley.newRequestQueue(this)
    val forecastRequest = JsonObjectRequest(url,
        {
            val lat = it.getString("lat")
            val lon = it.getString("lon")
            val list = it.getJSONArray("daily")
            var forecastList: ArrayList<ForecastList> = arrayListOf()

            for (i in 0..7) {
                        var day = list.getJSONObject(i)
                        var temp = day.getJSONObject("temp")
                var weather = day.getJSONArray("weather")
                var icon = weather.getJSONObject(0)
                var single =
                    ForecastList(day.getLong("dt"), icon.getString("icon"), temp.getInt("day"))
                forecastList.add(single)

            }

            //Gunluk havani gosteren recyclerviewun adapterini qoshmaq

            var recycler = findViewById<RecyclerView>(R.id.recyclerForecast)

            var forecastAdapter = WeatherAdapter(forecastList)

            recycler.adapter = forecastAdapter
            var linearLayoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            recycler.layoutManager = linearLayoutManager

        }, {
            Toast.makeText(this@MainActivity, "Couldnt loaded", Toast.LENGTH_SHORT).show()
        })

    queue.add(forecastRequest)

}

fun bringDate(): String {
    val calendar = Calendar.getInstance().time
    val format = SimpleDateFormat("EEEE, MMMM yyyy", Locale("en"))
    val date = format.format(calendar)
    return date

}

//fun loadData(city: String, lang: String) {
//
//    val url =
//        "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&lang=" + lang + "&appid=ef11e121e1385670fb44c37d6dd6d120"
//
//    val queue = Volley.newRequestQueue(this)
//    val havaJsonRequest = JsonObjectRequest(url,
//        { response ->
//            //JSON
//            val main = response.getJSONObject("main")
//            val temprature = main.getInt("temp")
//            val feelslike = main.getString("feels_like")
//
//            var city = response.getString("name")
//
//            val weatherMain = response.getJSONArray("weather")
//            val description = weatherMain.getJSONObject(0).getString("description")
//
//            findViewById<TextView>(R.id.tvTemp).text = temprature.toString()
//            findViewById<TextView>(R.id.tvFeelslike).text = feelslike.dropLast(3)
//            findViewById<TextView>(R.id.tvDescription).text = description
//            findViewById<TextView>(R.id.tvCity).text = city
//            findViewById<TextView>(R.id.tvDate).text = bringDate()
//
//            val iconCode = weatherMain.getJSONObject(0).getString("icon")
//            val icon = findViewById<ImageView>(R.id.imgIcon)
//            val background = R.drawable.backgroundnight
//            val constraint = findViewById<ConstraintLayout>(R.id.constraint)
//
//
//            when (iconCode) {
//                "01d" -> icon.setImageResource(R.drawable.sun)
//                "01n" -> {
//                    icon.setImageResource(R.drawable.moon)
//                    constraint.setBackgroundResource(background)
//                }
//                "02d" -> icon.setImageResource(R.drawable.sunny)
//                "02n" -> {
//                    icon.setImageResource(R.drawable.cloud)
//                    constraint.setBackgroundResource(background)
//                }
//                "03d" -> icon.setImageResource(R.drawable.cloud)
//                "03n" -> {
//                    icon.setImageResource(R.drawable.cloud)
//                    constraint.setBackgroundResource(background)
//                }
//                "04d" -> icon.setImageResource(R.drawable.broken)
//                "04n" -> {
//                    icon.setImageResource(R.drawable.broken)
//                    constraint.setBackgroundResource(background)
//                }
//                "09d" -> icon.setImageResource(R.drawable.shower)
//                "09n" -> {
//                    icon.setImageResource(R.drawable.shower)
//                    constraint.setBackgroundResource(background)
//                }
//                "10d" -> icon.setImageResource(R.drawable.rain)
//                "10n" -> {
//                    icon.setImageResource(R.drawable.rainnight)
//                    constraint.setBackgroundResource(background)
//                }
//                "13d" -> icon.setImageResource(R.drawable.snow)
//                "13n" -> {
//                    icon.setImageResource(R.drawable.snow)
//                    constraint.setBackgroundResource(background)
//                }
//                "50d" -> icon.setImageResource(R.drawable.fog)
//                "50n" -> {
//                    icon.setImageResource(R.drawable.fog)
//                    constraint.setBackgroundResource(background)
//
//                }
//                else -> icon.isVisible = false
//
//            }
//        },
//        { error ->
//            Toast.makeText(this, "Weather data couldn't loaded", Toast.LENGTH_LONG).show()
//        })
//
//// Add the request to the RequestQueue.
//    queue.add(havaJsonRequest)
//}


}