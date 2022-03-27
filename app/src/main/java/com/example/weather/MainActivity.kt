package com.example.weather

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.constraintlayout.widget.ConstraintLayout
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
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    var city = "baku"
    var lang = "en"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
           window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
       }

//        var drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
//        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
//        drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()

     //   supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        findViewById<NavigationView>(R.id.navView).setNavigationItemSelectedListener {
//            when (it.itemId) {
//                R.id.item1 -> Toast.makeText(applicationContext, "klamlsf", Toast.LENGTH_SHORT).show()
//                else -> {}
//            }
//            true
//        }



//        val url =
//            "https://api.openweathermap.org/data/2.5/weather?lat=40.409264&lon=49.867092&units=metric&lang=az&appid=ef11e121e1385670fb44c37d6dd6d120"
//


        //Dil secmek spinnerini initialize etmek
        //
//        var spnLanguage = findViewById<Spinner>(R.id.language)
//
//        val languages = arrayOf("AZ", "EN", "TR")
//
//        var adapter = ArrayAdapter.createFromResource(
//            this,
//            R.array.langs,
//            android.R.layout.simple_spinner_item
//        )
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//
//        spnLanguage.adapter = adapter
//
//        spnLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                var lang = p0?.getItemAtPosition(p2).toString()
//                loadData(city, lang)
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//
//            }
//        }

        loadData(city, lang)
        dailyForecast()

    }

    //END OF ONCREATE




    // 7 Gunluk hava melumatini API call etmek
    //
    private fun dailyForecast(){
        val url = "https://api.openweathermap.org/data/2.5/onecall?lat=40.409264&lon=49.867092&units=metric&exclude=minutely,hourly&appid=ef11e121e1385670fb44c37d6dd6d120"


        val queue = Volley.newRequestQueue(this)
        val forecastRequest = JsonObjectRequest(url,
            {
            val lat = it.getString("lat")
                val lon =it.getString("lon")
                val list = it.getJSONArray("daily")
                var forecastList: ArrayList<ForecastList> = arrayListOf()

                for(i in 0..7){
                    var day = list.getJSONObject(i)
                    var temp = day.getJSONObject("temp")
                    var weather = day.getJSONArray("weather")
                    var icon = weather.getJSONObject(0)
                    var single = ForecastList(day.getLong("dt"),icon.getString("icon"), temp.getInt("day"))
                    forecastList.add(single)

                }

                //Gunluk havani gosteren recyclerviewun adapterini qoshmaq

                var recycler=  findViewById<RecyclerView>(R.id.recyclerForecast)

                var forecastAdapter =WeatherAdapter(forecastList)

                recycler.adapter = forecastAdapter
                var linearLayoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                recycler.layoutManager = linearLayoutManager

        },{

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

    fun loadData(city: String, lang: String) {

        val url =
            "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&lang=" + lang + "&appid=ef11e121e1385670fb44c37d6dd6d120"

        val queue = Volley.newRequestQueue(this)
        val havaJsonRequest = JsonObjectRequest(url,
            { response ->
                //JSON
                val main = response.getJSONObject("main")
                val temprature = main.getInt("temp")
                val feelslike = main.getString("feels_like")

                var city = response.getString("name")

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

    fun hourlyData() {
        val url =
            "https://pro.openweathermap.org/data/2.5/forecast/hourly?q=baku&appid=ef11e121e1385670fb44c37d6dd6d120"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }


}