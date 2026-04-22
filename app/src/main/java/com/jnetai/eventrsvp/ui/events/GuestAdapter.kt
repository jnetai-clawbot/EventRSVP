package com.jnetai.eventrsvp.ui.events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jnetai.eventrsvp.R
import com.jnetai.eventrsvp.data.entity.Guest
import com.jnetai.eventrsvp.data.entity.RsvpStatus

class GuestAdapter(
    private val onStatusChange: (Guest, Int) -> Unit,
    private val onDelete: (Guest) -> Unit
) : RecyclerView.Adapter<GuestAdapter.ViewHolder>() {

    private var items: List<Guest> = emptyList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.textGuestName)
        val status: TextView = view.findViewById(R.id.textGuestStatus)
        val dietary: TextView = view.findViewById(R.id.textGuestDietary)
        val plusOnes: TextView = view.findViewById(R.id.textGuestPlusOnes)
        val btnStatus: ImageButton = view.findViewById(R.id.btnChangeStatus)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteGuest)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_guest, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val guest = items[position]
        holder.name.text = guest.name
        holder.status.text = statusEmoji(guest.rsvpStatus) + " " + guest.rsvpStatus.label
        holder.dietary.text = if (guest.dietaryRequirements.isNotBlank()) "🍽 ${guest.dietaryRequirements}" else ""
        holder.dietary.visibility = if (guest.dietaryRequirements.isNotBlank()) View.VISIBLE else View.GONE
        holder.plusOnes.text = if (guest.plusOnes > 0) "+${guest.plusOnes}" else ""
        holder.plusOnes.visibility = if (guest.plusOnes > 0) View.VISIBLE else View.GONE
        holder.btnStatus.setOnClickListener { onStatusChange(guest, position) }
        holder.btnDelete.setOnClickListener { onDelete(guest) }
    }

    override fun getItemCount() = items.size

    fun submitList(newItems: List<Guest>) {
        val oldItems = items
        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = oldItems.size
            override fun getNewListSize() = newItems.size
            override fun areItemsTheSame(oldPos: Int, newPos: Int) = oldItems[oldPos].id == newItems[newPos].id
            override fun areContentsTheSame(oldPos: Int, newPos: Int) = oldItems[oldPos] == newItems[newPos]
        })
        items = newItems
        result.dispatchUpdatesTo(this)
    }

    private fun statusEmoji(status: RsvpStatus): String = when (status) {
        RsvpStatus.ATTENDING -> "✅"
        RsvpStatus.MAYBE -> "🤔"
        RsvpStatus.DECLINED -> "❌"
        RsvpStatus.NO_RESPONSE -> "❓"
    }
}