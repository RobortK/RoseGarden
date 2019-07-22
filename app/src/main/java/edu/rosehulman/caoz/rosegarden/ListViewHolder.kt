package edu.rosehulman.caoz.rosegarden


import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.calendar_view.view.*



class ListViewHolder(itemView: View, adapter: ListAdapter): RecyclerView.ViewHolder(itemView)  {
    private val monthView = itemView.Month as TextView
    private  val dayView = itemView.Date as TextView


    fun bind(date: Date) {
        monthView.text = date.month
        dayView.text = date.day.toString()
    }
}
