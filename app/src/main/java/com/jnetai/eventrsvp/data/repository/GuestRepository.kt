package com.jnetai.eventrsvp.data.repository

import com.jnetai.eventrsvp.data.dao.GuestDao
import com.jnetai.eventrsvp.data.entity.Guest
import com.jnetai.eventrsvp.data.entity.RsvpStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GuestRepository(private val guestDao: GuestDao) {

    fun getGuestsForEvent(eventId: Long): Flow<List<Guest>> = guestDao.getGuestsForEvent(eventId)

    suspend fun countByStatus(eventId: Long, status: RsvpStatus): Int = withContext(Dispatchers.IO) {
        guestDao.countByStatus(eventId, status)
    }

    suspend fun totalPlusOnes(eventId: Long): Int = withContext(Dispatchers.IO) {
        guestDao.totalPlusOnes(eventId) ?: 0
    }

    suspend fun insert(guest: Guest): Long = withContext(Dispatchers.IO) {
        guestDao.insert(guest)
    }

    suspend fun update(guest: Guest) = withContext(Dispatchers.IO) {
        guestDao.update(guest)
    }

    suspend fun delete(guest: Guest) = withContext(Dispatchers.IO) {
        guestDao.delete(guest)
    }

    suspend fun getGuestsForEventList(eventId: Long): List<Guest> = withContext(Dispatchers.IO) {
        guestDao.getGuestsForEventList(eventId)
    }
}