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
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stineapp.R
import com.example.stineapp.R.id.textViewEventCountdown
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


data class Event(val name: String, val date: String) {
    fun getTimeRemainingOrPassed(): String {
        val now = LocalDate.now()
        val eventDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("d/M/yyyy"))
        val daysBetween = ChronoUnit.DAYS.between(now, eventDate)
        return when {
            daysBetween > 0 -> "$daysBetween days remaining"
            daysBetween < 0 -> "${-daysBetween} days passed"
            else -> "Today"
        }
    }
}

class EventsAdapter(private val context: Context, private var eventsList: ArrayList<Event>, private val eventListener: EventListener) : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    interface EventListener {
        fun onEdit(event: Event, position: Int)
        fun onDelete(position: Int)
    }

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
        }

        holder.itemView.setOnLongClickListener {
            showEditDeleteDialog(event, position)
            true
        }
    }

    override fun getItemCount() = eventsList.size

    private fun showEditDeleteDialog(event: Event, position: Int) {
        val options = arrayOf("Edit", "Delete")
        AlertDialog.Builder(context).setItems(options) { _, which ->
            when (which) {
                0 -> eventListener.onEdit(event, position)
                1 -> eventListener.onDelete(position)
            }
        }.show()
    }
}

class MainActivity : AppCompatActivity(), EventsAdapter.EventListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var eventsAdapter: EventsAdapter
    private lateinit var eventsList: ArrayList<Event>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViewEvents)
        recyclerView.layoutManager = LinearLayoutManager(this)
        loadEvents()
    }

    override fun onResume() {
        super.onResume()
        loadEvents()
    }

    private fun loadEvents() {
        val sharedPreferences = getSharedPreferences("events", Context.MODE_PRIVATE)
        val gson = Gson()
        val eventsJson = sharedPreferences.getString("events_list", "[]")
        val eventType = object : TypeToken<ArrayList<Event>>() {}.type
        eventsList = gson.fromJson(eventsJson, eventType)

        eventsAdapter = EventsAdapter(this, eventsList, this)
        recyclerView.adapter = eventsAdapter
    }

    override fun onEdit(event: Event, position: Int) {
        val intent = Intent(this, AddEventActivity::class.java).apply {
            putExtra("edit_event", true)
            putExtra("event_name", event.name)
            putExtra("event_date", event.date)
            putExtra("event_position", position)
        }
        startActivity(intent)
    }

    override fun onDelete(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this event?")
            .setPositiveButton("Delete") { _, _ ->
                eventsList.removeAt(position)
                saveEvents()
                eventsAdapter.notifyDataSetChanged()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveEvents() {
        val sharedPreferences = getSharedPreferences("events", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val newEventsJson = gson.toJson(eventsList)
        editor.putString("events_list", newEventsJson)
        editor.apply()
    }

    fun onAddEventButtonClick(view: View) {
        val intent = Intent(this, AddEventActivity::class.java)
        startActivity(intent)
    }

}