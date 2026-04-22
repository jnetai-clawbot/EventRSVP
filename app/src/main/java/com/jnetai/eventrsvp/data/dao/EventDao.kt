package com.jnetai.eventrsvp.data.dao

import androidx.room.*
import com.jnetai.eventrsvp.data.entity.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event): Long

    @Update
    suspend fun update(event: Event)

    @Delete
    suspend fun delete(event: Event)

    @Query("SELECT * FROM events ORDER BY date ASC, time ASC")
    fun getAllEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE date >= :today ORDER BY date ASC, time ASC")
    fun getUpcomingEvents(today: String): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE date < :today ORDER BY date DESC")
    fun getPastEvents(today: String): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: Long): Event?

    @Query("SELECT * FROM events WHERE id = :id")
    fun getEventByIdFlow(id: Long): Flow<Event?>
}