package com.example.stineapp

import android.content.Context
import android.content.Intent
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


class MainActivity : AppCompatActivity() {

    data class Event(val name: String, val date: String)
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
        val eventDateTextView: TextView = view.findViewById(R.id.textViewEventCountdown)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.eventNameTextView.text = eventsList[position].name
        holder.eventDateTextView.text = eventsList[position].date
    }

    override fun getItemCount() = eventsList.size
}
