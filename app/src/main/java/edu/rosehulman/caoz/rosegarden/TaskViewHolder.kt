package edu.rosehulman.caoz.rosegarden

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.task_view.view.*


class TaskViewHolder(itemView: View, adapter: taskAdapter): RecyclerView.ViewHolder(itemView)  {
    private val taskTitleView = itemView.task_title_view as TextView
    private  val taskTimeView = itemView.task_time_view as TextView
    init {
        itemView.setOnClickListener{
                adapter.selectTeamAt(adapterPosition)

        }
        itemView.setOnLongClickListener{
            adapter.showAddDialog(adapterPosition)
            true
        }
    }

    fun bind(task: Task) {
        taskTitleView.text = task.title
        taskTimeView.text = task.time
    }
}
