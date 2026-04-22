package com.jnetai.eventrsvp.ui.events

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jnetai.eventrsvp.EventRSVPApp
import com.jnetai.eventrsvp.R
import com.jnetai.eventrsvp.data.entity.Event
import com.jnetai.eventrsvp.data.entity.Guest
import com.jnetai.eventrsvp.notification.ReminderScheduler
import com.jnetai.eventrsvp.ui.guests.AddGuestActivity
import com.jnetai.eventrsvp.ui.theme.ThemeManager
import com.jnetai.eventrsvp.util.JsonExporter
import com.jnetai.eventrsvp.util.ShareHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventDetailActivity : AppCompatActivity() {

    private val app: EventRSVPApp by lazy { application as EventRSVPApp }
    private var eventId: Long = -1
    private var currentEvent: Event? = null
    private lateinit var guestAdapter: GuestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeManager.applyDarkTheme()
        setContentView(R.layout.activity_event_detail)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        eventId = intent.getLongExtra("event_id", -1)
        if (eventId == -1L) { finish(); return }

        guestAdapter = GuestAdapter(
            onStatusChange = { guest, position -> showStatusDialog(guest, position) },
            onDelete = { guest -> deleteGuest(guest) }
        )

        val recycler = findViewById<RecyclerView>(R.id.recyclerGuests)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = guestAdapter

        loadEvent()
        loadGuests()
    }

    private fun loadEvent() {
        lifecycleScope.launch {
            val event = withContext(Dispatchers.IO) { app.eventRepository.getEventById(eventId) }
            if (event == null) { finish(); return@launch }
            currentEvent = event
            supportActionBar?.title = event.name
            findViewById<TextView>(R.id.textDetailDate).text = "📅 ${formatDate(event.date)}"
            findViewById<TextView>(R.id.textDetailTime).text = "🕐 ${formatTime(event.time)}"
            findViewById<TextView>(R.id.textDetailLocation).text = if (event.location.isNotBlank()) "📍 ${event.location}" else ""
            findViewById<TextView>(R.id.textDetailDescription).text = if (event.description.isNotBlank()) event.description else ""
        }
    }

    private fun loadGuests() {
        lifecycleScope.launch {
            app.guestRepository.getGuestsForEvent(eventId).collectLatest { guests ->
                guestAdapter.submitList(guests)
                updateGuestSummary(guests)
            }
        }
    }

    private fun updateGuestSummary(guests: List<Guest>) {
        val attending = guests.count { it.rsvpStatus.name == "ATTENDING" }
        val maybe = guests.count { it.rsvpStatus.name == "MAYBE" }
        val declined = guests.count { it.rsvpStatus.name == "DECLINED" }
        val noResp = guests.count { it.rsvpStatus.name == "NO_RESPONSE" }
        val plusOnes = guests.sumOf { it.plusOnes }

        val summary = "👥 ${guests.size} guests  ·  ✅ $attending  ·  🤔 $maybe  ·  ❌ $declined  ·  ❓ $noResp  ·  +$plusOnes plus-ones"
        findViewById<TextView>(R.id.textGuestSummary).text = summary
    }

    private fun showStatusDialog(guest: Guest, position: Int) {
        val statuses = arrayOf("Attending", "Maybe", "Declined", "No Response")
        val currentIndex = when (guest.rsvpStatus.name) {
            "ATTENDING" -> 0; "MAYBE" -> 1; "DECLINED" -> 2; else -> 3
        }
        AlertDialog.Builder(this)
            .setTitle("RSVP: ${guest.name}")
            .setSingleChoiceItems(statuses, currentIndex) { dialog, which ->
                val newStatus = when (which) {
                    0 -> com.jnetai.eventrsvp.data.entity.RsvpStatus.ATTENDING
                    1 -> com.jnetai.eventrsvp.data.entity.RsvpStatus.MAYBE
                    2 -> com.jnetai.eventrsvp.data.entity.RsvpStatus.DECLINED
                    else -> com.jnetai.eventrsvp.data.entity.RsvpStatus.NO_RESPONSE
                }
                lifecycleScope.launch(Dispatchers.IO) {
                    app.guestRepository.update(guest.copy(rsvpStatus = newStatus))
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteGuest(guest: Guest) {
        AlertDialog.Builder(this)
            .setTitle("Remove ${guest.name}?")
            .setPositiveButton("Remove") { _, _ ->
                lifecycleScope.launch(Dispatchers.IO) {
                    app.guestRepository.delete(guest)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_event_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_guest -> {
                startActivity(Intent(this, AddGuestActivity::class.java).apply {
                    putExtra("event_id", eventId)
                })
                true
            }
            R.id.action_share -> {
                shareEvent()
                true
            }
            R.id.action_export -> {
                exportGuests()
                true
            }
            R.id.action_delete_event -> {
                confirmDeleteEvent()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun shareEvent() {
        lifecycleScope.launch {
            val event = currentEvent ?: return@launch
            val count = withContext(Dispatchers.IO) {
                app.guestRepository.getGuestsForEventList(eventId).size
            }
            val text = ShareHelper.buildInviteText(event, count)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            startActivity(Intent.createChooser(intent, "Share Event"))
        }
    }

    private fun exportGuests() {
        lifecycleScope.launch {
            val guests = withContext(Dispatchers.IO) {
                app.guestRepository.getGuestsForEventList(eventId)
            }
            val json = JsonExporter.exportGuests(guests)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_TEXT, json)
            }
            startActivity(Intent.createChooser(intent, "Export Guest List"))
        }
    }

    private fun confirmDeleteEvent() {
        AlertDialog.Builder(this)
            .setTitle("Delete this event?")
            .setMessage("This will remove the event and all its guests.")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch(Dispatchers.IO) {
                    currentEvent?.let {
                        ReminderScheduler.cancelReminder(this@EventDetailActivity, it.id)
                        app.eventRepository.delete(it)
                    }
                    runOnUiThread { finish() }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }

    private fun formatDate(iso: String): String {
        return try { val p = iso.split("-"); "${p[2]}/${p[1]}/${p[0]}" } catch (e: Exception) { iso }
    }

    private fun formatTime(iso: String): String {
        return try {
            val p = iso.split(":")
            val h = p[0].toInt()
            val ap = if (h >= 12) "PM" else "AM"
            val dh = if (h == 0) 12 else if (h > 12) h - 12 else h
            "$dh:${p[1]} $ap"
        } catch (e: Exception) { iso }
    }
}