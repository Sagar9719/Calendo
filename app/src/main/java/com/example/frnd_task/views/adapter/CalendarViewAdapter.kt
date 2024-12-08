package com.example.frnd_task.views.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.frnd_task.R
import com.example.frnd_task.databinding.ItemCalendarDayBinding

class CalendarViewAdapter(
    private val days: List<String>,
    private val onDateClick: (String, Int) -> Unit
) :
    RecyclerView.Adapter<CalendarViewAdapter.CalendarViewHolder>() {
    private var selectedDate: String? = "1"

    inner class CalendarViewHolder(private val binding: ItemCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(date: String, position: Int) {
            itemView.setOnClickListener {
                if (selectedDate != date) {
                    selectedDate = date
                    notifyDataSetChanged()
                    onDateClick(date, position)
                }
            }
            if (date == selectedDate) {
                itemView.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.color1
                    )
                )
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT)
            }
            if (date.isNullOrEmpty()) {
                itemView.visibility = View.GONE
            } else {
                itemView.visibility = View.VISIBLE
                binding.tvDay.text = date
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding =
            ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(days[position], position)
    }

    override fun getItemCount(): Int {
        return days.size
    }

}