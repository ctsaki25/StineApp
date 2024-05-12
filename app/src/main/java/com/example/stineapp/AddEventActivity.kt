package com.example.stineapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import java.util.ArrayList;
import java.util.HashMap;


class AddEventActivity : AppCompatActivity() {

    private lateinit var editTextDate: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_event_page)

        val buttonSave = findViewById<Button>(R.id.buttonSave)
        editTextDate = findViewById(R.id.editTextDate)

        buttonSave.setOnClickListener {
            val eventNameEditText = findViewById<EditText>(R.id.editTextEventName)

            val eventName = eventNameEditText.text.toString().trim()

            if (eventName.isEmpty()) {
                Toast.makeText(this, "You must enter an event name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Exit the listener
            }

            if (editTextDate.text.toString().isEmpty()) {
                Toast.makeText(this, "You must pick a date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Exit the listener
            }

            Toast.makeText(this, "Event saved", Toast.LENGTH_SHORT).show()
            finish()
        }

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
