package com.example.frnd_task.views.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.frnd_task.R
import com.example.frnd_task.data.TaskDetail
import com.example.frnd_task.databinding.TaskDetailBinding

class TaskViewAdapter(
    private val taskList: List<TaskDetail>,
    private val onTaskLongPressed: (TaskDetail) -> Unit
) :
    RecyclerView.Adapter<TaskViewAdapter.TaskViewHolder>() {
    private val colorList = listOf(
        R.color.color1,
        R.color.color2,
        R.color.color3,
        R.color.color4,
        R.color.color5,
        R.color.color6
    )

    var isTaskExpanded = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]

        holder.binding.tvTitle.text = task.taskModel?.title
        holder.binding.tvDesciption.text = task.taskModel?.description

        val randomColor = colorList.random()
        val drawable =
            ContextCompat.getDrawable(holder.itemView.context, R.drawable.task_background_shape)
        drawable?.mutate()?.let {
            it.setTint(ContextCompat.getColor(holder.itemView.context, randomColor))
            holder.binding.root.background = it
        }

        holder.binding.tvDesciption.visibility = if (isTaskExpanded) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            isTaskExpanded = !isTaskExpanded
            notifyItemChanged(position)
        }

        holder.itemView.setOnLongClickListener {
            onTaskLongPressed(task)
            true
        }
    }

    override fun getItemCount(): Int = taskList.size

    inner class TaskViewHolder(val binding: TaskDetailBinding) :
        RecyclerView.ViewHolder(binding.root)
}
