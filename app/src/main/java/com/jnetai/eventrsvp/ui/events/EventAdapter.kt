package com.jnetai.eventrsvp.ui.events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jnetai.eventrsvp.R
import com.jnetai.eventrsvp.data.entity.Event

class EventAdapter(
    private val onClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    private var items: List<Event> = emptyList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.textEventName)
        val date: TextView = view.findViewById(R.id.textEventDate)
        val time: TextView = view.findViewById(R.id.textEventTime)
        val location: TextView = view.findViewById(R.id.textEventLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = items[position]
        holder.name.text = event.name
        holder.date.text = formatDate(event.date)
        holder.time.text = formatTime(event.time)
        holder.location.text = event.location.ifBlank { "No location" }
        holder.location.visibility = if (event.location.isNotBlank()) View.VISIBLE else View.GONE
        holder.itemView.setOnClickListener { onClick(event) }
    }

    override fun getItemCount() = items.size

    fun submitList(newItems: List<Event>) {
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

    private fun formatDate(iso: String): String {
        return try {
            val p = iso.split("-")
            "${p[2]}/${p[1]}/${p[0]}"
        } catch (e: Exception) { iso }
    }

    private fun formatTime(iso: String): String {
        return try {
            val p = iso.split(":")
            val h = p[0].toInt()
            val amPm = if (h >= 12) "PM" else "AM"
            val dh = if (h == 0) 12 else if (h > 12) h - 12 else h
            "$dh:${p[1]} $amPm"
        } catch (e: Exception) { iso }
    }
}