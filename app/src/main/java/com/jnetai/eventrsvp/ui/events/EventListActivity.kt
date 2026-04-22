package com.jnetai.eventrsvp.ui.events

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jnetai.eventrsvp.EventRSVPApp
import com.jnetai.eventrsvp.R
import com.jnetai.eventrsvp.data.entity.Event
import com.jnetai.eventrsvp.ui.about.AboutActivity
import com.jnetai.eventrsvp.ui.dashboard.DashboardActivity
import com.jnetai.eventrsvp.ui.theme.ThemeManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class EventListActivity : AppCompatActivity() {

    private enum class FilterMode { ALL, UPCOMING, PAST }

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var spinner: Spinner
    private lateinit var adapter: EventAdapter
    private val app: EventRSVPApp by lazy { application as EventRSVPApp }
    private var currentFilter = FilterMode.ALL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeManager.applyDarkTheme()
        setContentView(R.layout.activity_event_list)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "EventRSVP"

        recyclerView = findViewById(R.id.recyclerEvents)
        emptyView = findViewById(R.id.textEmpty)
        spinner = findViewById(R.id.spinnerFilter)

        adapter = EventAdapter { event ->
            startActivity(Intent(this, EventDetailActivity::class.java).apply {
                putExtra("event_id", event.id)
            })
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        setupFilterSpinner()
        loadEvents()

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            startActivity(Intent(this, CreateEventActivity::class.java))
        }
    }

    private fun setupFilterSpinner() {
        val filters = listOf("All Events", "Upcoming", "Past")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filters)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentFilter = when (position) {
                    1 -> FilterMode.UPCOMING
                    2 -> FilterMode.PAST
                    else -> FilterMode.ALL
                }
                loadEvents()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadEvents() {
        val today = LocalDate.now().toString()
        lifecycleScope.launch {
            val flow = when (currentFilter) {
                FilterMode.ALL -> app.eventRepository.getAllEvents()
                FilterMode.UPCOMING -> app.eventRepository.getUpcomingEvents(today)
                FilterMode.PAST -> app.eventRepository.getPastEvents(today)
            }
            flow.collectLatest { events ->
                adapter.submitList(events)
                emptyView.visibility = if (events.isEmpty()) View.VISIBLE else View.GONE
                recyclerView.visibility = if (events.isEmpty()) View.GONE else View.VISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                startActivity(Intent(this, CreateEventActivity::class.java))
                true
            }
            R.id.action_dashboard -> {
                startActivity(Intent(this, DashboardActivity::class.java))
                true
            }
            R.id.action_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}