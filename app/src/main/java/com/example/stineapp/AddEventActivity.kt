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

    private lateinit var editTextDate: EditText
    private lateinit var editTextEventName: EditText
    private lateinit var buttonSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_event_page)

        editTextEventName = findViewById(R.id.editTextEventName)
        editTextDate = findViewById(R.id.editTextDate)
        buttonSave = findViewById(R.id.buttonSave)

        buttonSave.setOnClickListener {
            val name = editTextEventName.text.toString().trim()
            val date = editTextDate.text.toString().trim()

            if (name.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Both event name and date are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveEvent(MainActivity.Event(name, date))
            Toast.makeText(this, "Event saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun saveEvent(event: MainActivity.Event) {
        val sharedPreferences = getSharedPreferences("events", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()

        val existingEventsJson = sharedPreferences.getString("events_list", "[]")
        val eventType = object : TypeToken<ArrayList<MainActivity.Event>>() {}.type
        var events: ArrayList<MainActivity.Event> = gson.fromJson(existingEventsJson, eventType)

        events.add(event)
        val newEventsJson = gson.toJson(events)
        editor.putString("events_list", newEventsJson)
        editor.apply()
    }

    fun showDatePickerDialog(view: View) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = "${dayOfMonth}/${monthOfYear + 1}/${year}"
                editTextDate.setText(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
