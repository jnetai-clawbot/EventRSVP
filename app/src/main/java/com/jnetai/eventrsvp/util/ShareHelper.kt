package com.jnetai.eventrsvp.util

import com.jnetai.eventrsvp.data.entity.Event

object ShareHelper {

    fun buildInviteText(event: Event, guestCount: Int): String {
        return buildString {
            appendLine("📅 You're Invited!")
            appendLine()
            appendLine("🎉 ${event.name}")
            appendLine("📅 Date: ${formatDate(event.date)}")
            appendLine("🕐 Time: ${formatTime(event.time)}")
            if (event.location.isNotBlank()) appendLine("📍 Location: ${event.location}")
            if (event.description.isNotBlank()) appendLine("📝 ${event.description}")
            appendLine()
            appendLine("👥 $guestCount guests invited")
            appendLine()
            appendLine("— Sent via EventRSVP")
        }
    }

    private fun formatDate(isoDate: String): String {
        return try {
            val parts = isoDate.split("-")
            "${parts[2]}/${parts[1]}/${parts[0]}"
        } catch (e: Exception) { isoDate }
    }

    private fun formatTime(isoTime: String): String {
        return try {
            val parts = isoTime.split(":")
            val hour = parts[0].toInt()
            val minute = parts[1]
            val amPm = if (hour >= 12) "PM" else "AM"
            val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
            "$displayHour:$minute $amPm"
        } catch (e: Exception) { isoTime }
    }
}