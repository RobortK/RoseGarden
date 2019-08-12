package edu.rosehulman.caoz.rosegarden

import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.task_view.view.*


class TaskViewHolder(itemView: View, adapter: taskAdapter): RecyclerView.ViewHolder(itemView)  {
    private val taskTitleView = itemView.task_title_view as TextView
    private  val taskTimeView = itemView.task_time_view as TextView
    val  cardView: CardView =itemView.card_view
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

        taskTimeView.text =   "${if (task!!.hour == 0)task!!.minute.toString()+" Mins"  
        else task!!.hour.toString()+ "Hours "+task!!.minute.toString()+"Mins"}"
        if(task.isDone){
            cardView.setCardBackgroundColor(Color.GREEN)
        }else{
            cardView.setBackgroundColor(Color.GRAY)
        }
        cardView.background.alpha= 80

    }
}
