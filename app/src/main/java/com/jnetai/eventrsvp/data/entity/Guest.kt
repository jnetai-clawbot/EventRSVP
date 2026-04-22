package com.jnetai.eventrsvp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "guests",
    foreignKeys = [
        ForeignKey(
            entity = Event::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("eventId")]
)
data class Guest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val eventId: Long,
    val name: String,
    val rsvpStatus: RsvpStatus = RsvpStatus.NO_RESPONSE,
    val dietaryRequirements: String = "",
    val plusOnes: Int = 0,
    val notes: String = ""
)

enum class RsvpStatus(val label: String) {
    ATTENDING("Attending"),
    MAYBE("Maybe"),
    DECLINED("Declined"),
    NO_RESPONSE("No Response")
}