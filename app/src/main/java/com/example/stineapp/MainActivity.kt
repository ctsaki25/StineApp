package com.example.stineapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stineapp.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class MainActivity : AppCompatActivity() {

    data class Event(val name: String, val date: String) {
        fun getTimeRemainingOrPassed(): String {
            try {
                val now = LocalDate.now()
                val eventDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("d/M/yyyy"))

                val daysBetween = ChronoUnit.DAYS.between(now, eventDate)
                return when {
                    daysBetween > 0 -> "$daysBetween days remaining"
                    daysBetween < 0 -> "${-daysBetween} days passed"
                    else -> "Today"
                }
            } catch (e: Exception) {
                return "Invalid date format"
            }
        }
    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventsAdapter: EventsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViewEvents)
        recyclerView.layoutManager = LinearLayoutManager(this)
        loadEvents()
    }

    private fun loadEvents() {
        val sharedPreferences = getSharedPreferences("events", Context.MODE_PRIVATE)
        val gson = Gson()
        val eventsJson = sharedPreferences.getString("events_list", "[]")
        val eventType = object : TypeToken<ArrayList<Event>>() {}.type
        val eventsList: ArrayList<Event> = gson.fromJson(eventsJson, eventType)

        eventsAdapter = EventsAdapter(eventsList)
        recyclerView.adapter = eventsAdapter
    }
    override fun onResume() {
        super.onResume()
        loadEvents()
    }

//    fun loadEvents() {
//        val sharedPreferences = getSharedPreferences("events", Context.MODE_PRIVATE)
//        val gson = Gson()
//        val eventsJson = sharedPreferences.getString("events_list", "[]")
//        val eventType = object : TypeToken<ArrayList<Event>>() {}.type
//        val eventsList: ArrayList<Event> = gson.fromJson(eventsJson, eventType)
//
//        recyclerView = findViewById(R.id.recyclerViewEvents)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        eventsAdapter = EventsAdapter(eventsList)
//        recyclerView.adapter = eventsAdapter
//    }

    fun onAddEventButtonClick(view: View) {
        val intent = Intent(this, AddEventActivity::class.java)
        startActivity(intent)
    }
}
class EventsAdapter(private val eventsList: ArrayList<MainActivity.Event>) : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val eventNameTextView: TextView = view.findViewById(R.id.textViewEventName)
        val eventTimeTextView: TextView = view.findViewById(R.id.textViewEventCountdown)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = eventsList[position]
        holder.eventNameTextView.text = event.name
        holder.eventTimeTextView.text = event.getTimeRemainingOrPassed()
        if (event.getTimeRemainingOrPassed().contains("passed")) {
            holder.eventTimeTextView.setTextColor(Color.RED)
        } else {
            holder.eventTimeTextView.setTextColor(Color.BLACK) // or any other color
        }
    }

    override fun getItemCount() = eventsList.size
}

