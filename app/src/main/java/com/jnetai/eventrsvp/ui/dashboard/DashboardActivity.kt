package com.jnetai.eventrsvp.ui.dashboard

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jnetai.eventrsvp.EventRSVPApp
import com.jnetai.eventrsvp.R
import com.jnetai.eventrsvp.data.entity.RsvpStatus
import com.jnetai.eventrsvp.ui.theme.ThemeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardActivity : AppCompatActivity() {

    private val app: EventRSVPApp by lazy { application as EventRSVPApp }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeManager.applyDarkTheme()
        setContentView(R.layout.activity_dashboard)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Dashboard"

        loadStats()
    }

    private fun loadStats() {
        lifecycleScope.launch {
            app.eventRepository.getAllEvents().collectLatest { events ->
                val totalEvents = events.size
                val today = java.time.LocalDate.now().toString()
                val upcoming = events.count { it.date >= today }

                findViewById<TextView>(R.id.textTotalEvents).text = "📋 Total Events: $totalEvents"
                findViewById<TextView>(R.id.textUpcomingEvents).text = "🔜 Upcoming: $upcoming"
                findViewById<TextView>(R.id.textPastEvents).text = "📅 Past: ${totalEvents - upcoming}"

                // Aggregate guest stats across all events
                var totalGuests = 0
                var totalAttending = 0
                var totalMaybe = 0
                var totalDeclined = 0
                var totalNoResp = 0
                var totalPlusOnes = 0

                for (event in events) {
                    val guests = withContext(Dispatchers.IO) {
                        app.guestRepository.getGuestsForEventList(event.id)
                    }
                    totalGuests += guests.size
                    totalAttending += guests.count { it.rsvpStatus == RsvpStatus.ATTENDING }
                    totalMaybe += guests.count { it.rsvpStatus == RsvpStatus.MAYBE }
                    totalDeclined += guests.count { it.rsvpStatus == RsvpStatus.DECLINED }
                    totalNoResp += guests.count { it.rsvpStatus == RsvpStatus.NO_RESPONSE }
                    totalPlusOnes += guests.sumOf { it.plusOnes }
                }

                findViewById<TextView>(R.id.textTotalGuests).text = "👥 Total Guests: $totalGuests"
                findViewById<TextView>(R.id.textAttending).text = "✅ Attending: $totalAttending"
                findViewById<TextView>(R.id.textMaybe).text = "🤔 Maybe: $totalMaybe"
                findViewById<TextView>(R.id.textDeclined).text = "❌ Declined: $totalDeclined"
                findViewById<TextView>(R.id.textNoResponse).text = "❓ No Response: $totalNoResp"
                findViewById<TextView>(R.id.textPlusOnes).text = "➕ Plus-ones: $totalPlusOnes"
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}