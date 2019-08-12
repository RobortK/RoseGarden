package edu.rosehulman.caoz.rosegarden

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


import java.util.*
import kotlin.collections.ArrayList


class ListAdapter(var context: Context?, var listener: ListFragment.OnSelectedListener?, var uid: String, val cal: Calendar) : RecyclerView.Adapter<ListViewHolder>() {

    private  val  dayList = ArrayList<Date>()
    private  val   adapterList = ArrayList<taskAdapter>()


    init {

        val year  = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val totalDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            for(i in 1..totalDay){
            add(Date(i,month,year))
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.calendar_view, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {

        holder.bind(dayList[position],adapterList[position],context)
    }

    fun getTaskAdapter(): taskAdapter{
        //????
        return adapterList[cal.get(Calendar.DAY_OF_MONTH)-1]
    }
    override fun getItemCount() = dayList.size



    fun  add(date: Date){
        dayList.add(date)
        val taskAdapter = taskAdapter(context, listener,uid,date)
        adapterList.add(taskAdapter)
        notifyItemInserted(0)
    }


//    fun  edit(position: Int, title: String, duration: String){
//                taskList[position].title = title
//                taskList[position].duration = duration
//
//
//    }


//    fun  selectTeamAt(adapterPosition: Int){
//        val day = dayList[adapterPosition]
//       // listener?.onSelected(task)
//    }



}