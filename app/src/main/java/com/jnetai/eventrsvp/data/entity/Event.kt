package com.jnetai.eventrsvp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val date: String,          // ISO: yyyy-MM-dd
    val time: String,          // ISO: HH:mm
    val location: String = "",
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)