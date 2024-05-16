package com.example.stineapp

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar
import java.util.ArrayList;
import java.util.HashMap;


class AddEventActivity : AppCompatActivity() {

    private lateinit var editTextEventName: EditText
    private lateinit var editTextDate: EditText
    private var eventPosition: Int? = null

    fun showDatePickerDialog(view: View) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            val selectedDate = String.format("%d/%d/%d", selectedDayOfMonth, selectedMonth + 1, selectedYear)
            editTextDate.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_event_page)

        editTextEventName = findViewById(R.id.editTextEventName)
        editTextDate = findViewById(R.id.editTextDate)

        intent?.let {
            if (it.getBooleanExtra("edit_event", false)) {
                editTextEventName.setText(it.getStringExtra("event_name"))
                editTextDate.setText(it.getStringExtra("event_date"))
                eventPosition = it.getIntExtra("event_position", -1)
            }
        }

        findViewById<Button>(R.id.buttonSave).setOnClickListener {
            saveEvent()
        }
    }

    private fun saveEvent() {
        val name = editTextEventName.text.toString().trim()
        val date = editTextDate.text.toString().trim()

        if (name.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Both event name and date are required", Toast.LENGTH_SHORT).show()
            return
        }

        val event = Event(name, date)
        val sharedPreferences = getSharedPreferences("events", Context.MODE_PRIVATE)
        val gson = Gson()
        val eventsJson = sharedPreferences.getString("events_list", "[]")
        val eventType = object : TypeToken<ArrayList<Event>>() {}.type
        val eventsList: ArrayList<Event> = gson.fromJson(eventsJson, eventType)

        eventPosition?.let {
            if (it >= 0) {
                eventsList[it] = event // Replace the existing event
            } else {
                eventsList.add(event) // New event
            }
        } ?: eventsList.add(event)

        val newEventsJson = gson.toJson(eventsList)
        sharedPreferences.edit().putString("events_list", newEventsJson).apply()

        finish()
    }
}