package com.example.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList


class WeatherAdapter (forecastList: ArrayList<ForecastList>): RecyclerView.Adapter<WeatherAdapter.MyViewHolder>(){

    var forecastList = forecastList

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var singleLine = itemView as ConstraintLayout
        var day = itemView.findViewById<TextView>(R.id.tvDayDaily)
        var date = itemView.findViewById<TextView>(R.id.tvDateDayily)
        var icon = itemView.findViewById<ImageView>(R.id.imgIconDay)
        var temp = itemView.findViewById<TextView>(R.id.tvTempDay)


        fun setData(listItemPosition: ForecastList, position: Int) {

            if(listItemPosition.date != null) {

                var tarix = getShortDate(listItemPosition.date)
                var list = tarix.split(" ")

                var date1 = list[1]+" "+list[2]+" "+list[3]
                var day1 = list[0].dropLast(1)
                date.text = date1
                day.text = day1
                temp.text = listItemPosition.temp.toString()

                when (listItemPosition.image) {
                    "01d" -> icon.setImageResource(R.drawable.sun)
                    "01n" -> {
                        icon.setImageResource(R.drawable.moon)
                    }
                    "02d" -> icon.setImageResource(R.drawable.sunny)
                    "02n" -> {
                        icon.setImageResource(R.drawable.cloud)
                    }
                    "03d" -> icon.setImageResource(R.drawable.cloud)
                    "03n" -> {
                        icon.setImageResource(R.drawable.cloud)
                    }
                    "04d" -> icon.setImageResource(R.drawable.broken)
                    "04n" -> {
                        icon.setImageResource(R.drawable.broken)
                    }
                    "09d" -> icon.setImageResource(R.drawable.shower)
                    "09n" -> {
                        icon.setImageResource(R.drawable.shower)
                    }
                    "10d" -> icon.setImageResource(R.drawable.rain)
                    "10n" -> {
                        icon.setImageResource(R.drawable.rainnight)
                    }
                    "13d" -> icon.setImageResource(R.drawable.snow)
                    "13n" -> {
                        icon.setImageResource(R.drawable.snow)
                    }
                    "50d" -> icon.setImageResource(R.drawable.fog)
                    "50n" -> {
                        icon.setImageResource(R.drawable.fog)

                    }
                    else -> icon.isVisible = false
                }
            }
            else{
                date.text = "error"
                temp.text = "error"

            }
        }

        fun getShortDate(ts:Long?):String{
            if(ts == null) return ""
            //Get instance of calendar
            val calendar = Calendar.getInstance(Locale.getDefault())
            //get current date from ts
            calendar.timeInMillis = ts * 1000L
            //return formatted date
            return android.text.format.DateFormat.format("EEEE, dd MMM yyyy", calendar).toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val tek_setir = inflater.inflate(R.layout.single_line, parent, false)
        return  MyViewHolder(tek_setir)
    }


    override fun getItemCount(): Int {
        return forecastList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var listItemPosition = forecastList.get(position)

        holder.setData(listItemPosition, position)

    }





}