package com.jnetai.eventrsvp.data.repository

import com.jnetai.eventrsvp.data.dao.EventDao
import com.jnetai.eventrsvp.data.entity.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class EventRepository(private val eventDao: EventDao) {

    fun getAllEvents(): Flow<List<Event>> = eventDao.getAllEvents()

    fun getUpcomingEvents(today: String): Flow<List<Event>> = eventDao.getUpcomingEvents(today)

    fun getPastEvents(today: String): Flow<List<Event>> = eventDao.getPastEvents(today)

    suspend fun getEventById(id: Long): Event? = withContext(Dispatchers.IO) {
        eventDao.getEventById(id)
    }

    fun getEventByIdFlow(id: Long): Flow<Event?> = eventDao.getEventByIdFlow(id)

    suspend fun insert(event: Event): Long = withContext(Dispatchers.IO) {
        eventDao.insert(event)
    }

    suspend fun update(event: Event) = withContext(Dispatchers.IO) {
        eventDao.update(event)
    }

    suspend fun delete(event: Event) = withContext(Dispatchers.IO) {
        eventDao.delete(event)
    }
}