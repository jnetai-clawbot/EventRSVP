package com.jnetai.eventrsvp.ui.events

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jnetai.eventrsvp.EventRSVPApp
import com.jnetai.eventrsvp.R
import com.jnetai.eventrsvp.data.entity.Event
import com.jnetai.eventrsvp.notification.ReminderScheduler
import com.jnetai.eventrsvp.ui.theme.ThemeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class CreateEventActivity : AppCompatActivity() {

    private val app: EventRSVPApp by lazy { application as EventRSVPApp }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeManager.applyDarkTheme()
        setContentView(R.layout.activity_create_event)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Create Event"

        val editName = findViewById<EditText>(R.id.editEventName)
        val editDate = findViewById<EditText>(R.id.editEventDate)
        val editTime = findViewById<EditText>(R.id.editEventTime)
        val editLocation = findViewById<EditText>(R.id.editEventLocation)
        val editDescription = findViewById<EditText>(R.id.editEventDescription)
        val btnCreate = findViewById<Button>(R.id.btnCreateEvent)

        // Set defaults
        editDate.setText(LocalDate.now().toString())
        editTime.setText(LocalTime.now().plusHours(1).toString().substring(0, 5))

        btnCreate.setOnClickListener {
            val name = editName.text.toString().trim()
            val date = editDate.text.toString().trim()
            val time = editTime.text.toString().trim()

            if (name.isBlank()) {
                editName.error = "Name required"
                return@setOnClickListener
            }
            if (date.isBlank()) {
                editDate.error = "Date required"
                return@setOnClickListener
            }
            if (time.isBlank()) {
                editTime.error = "Time required"
                return@setOnClickListener
            }

            val event = Event(
                name = name,
                date = date,
                time = time,
                location = editLocation.text.toString().trim(),
                description = editDescription.text.toString().trim()
            )

            lifecycleScope.launch(Dispatchers.IO) {
                val id = app.eventRepository.insert(event)
                val savedEvent = event.copy(id = id)
                ReminderScheduler.scheduleReminder(this@CreateEventActivity, savedEvent)
                runOnUiThread {
                    Toast.makeText(this@CreateEventActivity, "Event created!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}