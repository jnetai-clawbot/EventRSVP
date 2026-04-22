package com.jnetai.eventrsvp

import android.app.Application
import com.jnetai.eventrsvp.data.AppDatabase
import com.jnetai.eventrsvp.data.repository.EventRepository
import com.jnetai.eventrsvp.data.repository.GuestRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class EventRSVPApp : Application() {

    val database by lazy { AppDatabase.getInstance(this) }
    val eventRepository by lazy { EventRepository(database.eventDao()) }
    val guestRepository by lazy { GuestRepository(database.guestDao()) }
    val applicationScope = CoroutineScope(SupervisorJob())
}