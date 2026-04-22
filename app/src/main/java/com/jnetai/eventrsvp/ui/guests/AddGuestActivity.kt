package com.jnetai.eventrsvp.ui.guests

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jnetai.eventrsvp.EventRSVPApp
import com.jnetai.eventrsvp.R
import com.jnetai.eventrsvp.data.entity.Guest
import com.jnetai.eventrsvp.data.entity.RsvpStatus
import com.jnetai.eventrsvp.ui.theme.ThemeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddGuestActivity : AppCompatActivity() {

    private val app: EventRSVPApp by lazy { application as EventRSVPApp }
    private var eventId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeManager.applyDarkTheme()
        setContentView(R.layout.activity_add_guest)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add Guest"

        eventId = intent.getLongExtra("event_id", -1)
        if (eventId == -1L) { finish(); return }

        val editName = findViewById<EditText>(R.id.editGuestName)
        val spinnerStatus = findViewById<Spinner>(R.id.spinnerRsvpStatus)
        val editDietary = findViewById<EditText>(R.id.editDietary)
        val editPlusOnes = findViewById<EditText>(R.id.editPlusOnes)
        val editNotes = findViewById<EditText>(R.id.editGuestNotes)
        val btnAdd = findViewById<Button>(R.id.btnAddGuest)

        val statusLabels = RsvpStatus.values().map { it.label }
        spinnerStatus.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusLabels)
        spinnerStatus.setSelection(RsvpStatus.NO_RESPONSE.ordinal)

        btnAdd.setOnClickListener {
            val name = editName.text.toString().trim()
            if (name.isBlank()) {
                editName.error = "Name required"
                return@setOnClickListener
            }

            val status = RsvpStatus.values()[spinnerStatus.selectedItemPosition]
            val guest = Guest(
                eventId = eventId,
                name = name,
                rsvpStatus = status,
                dietaryRequirements = editDietary.text.toString().trim(),
                plusOnes = editPlusOnes.text.toString().trim().toIntOrNull() ?: 0,
                notes = editNotes.text.toString().trim()
            )

            lifecycleScope.launch(Dispatchers.IO) {
                app.guestRepository.insert(guest)
                runOnUiThread {
                    Toast.makeText(this@AddGuestActivity, "Guest added!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}