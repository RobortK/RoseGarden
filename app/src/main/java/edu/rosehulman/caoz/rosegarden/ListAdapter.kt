package edu.rosehulman.caoz.rosegarden

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.calendar_view.view.*


import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class ListAdapter(var context: Context?, var listener: ListFragment.OnSelectedListener?, var uid: String) : RecyclerView.Adapter<ListViewHolder>() {

    private  val  dayList = ArrayList<Date>()
    private  val   adapterList = ArrayList<taskAdapter>()


    init {

        val year  = LocalDateTime.now().year
        val month = LocalDateTime.now().month.toString()
        for(i in 1..30){
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

    fun getTaskAdapter(date: Int): taskAdapter{
        return adapterList[28]
    }
    override fun getItemCount() = dayList.size



    fun  add(date: Date){
        dayList.add(date)
        val taskAdapter = taskAdapter(context, listener,uid!!)
        adapterList.add(taskAdapter)
        notifyItemInserted(0)
    }


//    fun  edit(position: Int, title: String, time: String){
//                taskList[position].title = title
//                taskList[position].time = time
//
//
//    }


    fun  selectTeamAt(adapterPosition: Int){
        val day = dayList[adapterPosition]
       // listener?.onSelected(task)
    }



}