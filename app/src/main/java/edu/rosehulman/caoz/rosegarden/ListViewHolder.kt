package edu.rosehulman.caoz.rosegarden


import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.calendar_view.view.*
import java.text.DateFormatSymbols


class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)  {
    private val monthView = itemView.Weekday as TextView
    private  val dayView = itemView.Date as TextView
    private val recyclerView = itemView.task_recycler_view


    fun bind(date: Date, adapter: taskAdapter,context: Context?) {
        monthView.text =  DateFormatSymbols().weekdays[date.weekday]
        dayView.text = date.day.toString()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

    }
}
