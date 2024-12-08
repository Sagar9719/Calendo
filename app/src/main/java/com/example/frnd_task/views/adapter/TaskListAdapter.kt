package com.example.frnd_task.views.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.frnd_task.R
import com.example.frnd_task.data.TaskDetail
import com.example.frnd_task.databinding.TaskDetailBinding

class TaskListAdapter(
    private val onTaskLongPressed: (TaskDetail) -> Unit
) : ListAdapter<TaskDetail, TaskListAdapter.TaskViewHolder>(TaskDiffCallback()) {
    private val colorList = listOf(
        R.color.color1,
        R.color.color2,
        R.color.color3,
        R.color.color4,
        R.color.color5,
        R.color.color6
    )

    private var expandedTaskPosition: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)

        holder.binding.tvTitle.text = task.taskModel?.title
        holder.binding.tvDesciption.text = task.taskModel?.description

        val randomColor = colorList.random()
        val drawable =
            ContextCompat.getDrawable(holder.itemView.context, R.drawable.task_background_shape)
        drawable?.mutate()?.let {
            it.setTint(ContextCompat.getColor(holder.itemView.context, randomColor))
            holder.binding.root.background = it
        }

        holder.binding.tvDesciption.visibility =
            if (expandedTaskPosition == position) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            val previousExpandedPosition = expandedTaskPosition
            expandedTaskPosition = if (expandedTaskPosition == position) null else position

            previousExpandedPosition?.let { notifyItemChanged(it) }
            notifyItemChanged(position)
        }

        holder.itemView.setOnLongClickListener {
            onTaskLongPressed(task)
            true
        }
    }

    inner class TaskViewHolder(val binding: TaskDetailBinding) :
        RecyclerView.ViewHolder(binding.root)

    class TaskDiffCallback : DiffUtil.ItemCallback<TaskDetail>() {
        override fun areItemsTheSame(oldItem: TaskDetail, newItem: TaskDetail): Boolean {
            return oldItem.taskId == newItem.taskId
        }

        override fun areContentsTheSame(oldItem: TaskDetail, newItem: TaskDetail): Boolean {
            return oldItem == newItem
        }
    }
}