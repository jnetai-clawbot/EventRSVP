package com.jnetai.eventrsvp.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jnetai.eventrsvp.data.entity.Guest

object JsonExporter {

    private val gson = Gson()

    fun exportGuests(guests: List<Guest>): String {
        val exportList = guests.map { guest ->
            mapOf(
                "name" to guest.name,
                "rsvpStatus" to guest.rsvpStatus.label,
                "dietaryRequirements" to guest.dietaryRequirements,
                "plusOnes" to guest.plusOnes,
                "notes" to guest.notes
            )
        }
        return gson.toJson(exportList)
    }
}