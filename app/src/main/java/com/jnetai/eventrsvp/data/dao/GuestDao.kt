package com.jnetai.eventrsvp.data.dao

import androidx.room.*
import com.jnetai.eventrsvp.data.entity.Guest
import com.jnetai.eventrsvp.data.entity.RsvpStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface GuestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(guest: Guest): Long

    @Update
    suspend fun update(guest: Guest)

    @Delete
    suspend fun delete(guest: Guest)

    @Query("SELECT * FROM guests WHERE eventId = :eventId ORDER BY name ASC")
    fun getGuestsForEvent(eventId: Long): Flow<List<Guest>>

    @Query("SELECT COUNT(*) FROM guests WHERE eventId = :eventId AND rsvpStatus = :status")
    suspend fun countByStatus(eventId: Long, status: RsvpStatus): Int

    @Query("SELECT SUM(plusOnes) FROM guests WHERE eventId = :eventId AND rsvpStatus != :declined")
    suspend fun totalPlusOnes(eventId: Long, declined: RsvpStatus = RsvpStatus.DECLINED): Int?

    @Query("SELECT * FROM guests WHERE eventId = :eventId")
    suspend fun getGuestsForEventList(eventId: Long): List<Guest>
}