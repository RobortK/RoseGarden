package edu.rosehulman.caoz.rosegarden

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.dialog_add.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class taskAdapter (var context: Context?, var listener: ListFragment.OnSelectedListener?, var uid: String, var date:String) : RecyclerView.Adapter<TaskViewHolder>() {

    private  val  taskList = ArrayList<Task>()
    private var cal = Calendar.getInstance()
    private var date_button: Button? = null
    private var startTime_button : Button? = null
    private val taskRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.USERS_COLLECTION)
        .document(uid)
        .collection(Constants.TASKS_COLLECTION)



    init {
        taskRef
            .orderBy(Task.LAST_TOUCHED_KEY, Query.Direction.ASCENDING)

            .addSnapshotListener { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
                if(exception != null){
                    Log.e(Constants.TAG,"Listen error ${exception}")
                    return@addSnapshotListener
                }
                for (docChange in snapshot!!.documentChanges) {
                    val task = Task.fromSnapShot(docChange.document)
                    if (task.date == date) {
                        when (docChange.type) {
                            DocumentChange.Type.ADDED -> {
                                //taskList.add(task.pos, task)
                                taskList.add(0, task)

                                notifyItemInserted(0)
                            }
                            DocumentChange.Type.REMOVED -> {
                                val pos = taskList.indexOfFirst { task.id == it.id }
                                taskList.removeAt(pos)
                                notifyItemRemoved(pos)

                            }
                            DocumentChange.Type.MODIFIED -> {
                                val pos = taskList.indexOfFirst { task.id == it.id }
                                taskList[pos] = task
                                notifyItemChanged(pos)
                            }


                        }
                    }
                }
            }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.task_view, parent, false)
        return TaskViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(taskList[position])
    }
    override fun getItemCount() = taskList.size




    fun showAddDialog(position: Int = -1){
        val builder = AlertDialog.Builder(context)
        //Set options
        builder.setTitle(
            if(position >= 0){
                R.string.dialog_title_edit
            }else{
                R.string.dialog_title_add
            })

        //Content is message, view or a list of items
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add, null, false)
        builder.setView(view)
        date_button = view.date_button
        startTime_button = view.startTime_button
        date_button!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(context!!,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        })
        updateDateInView()

        startTime_button!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {

                 TimePickerDialog(context!!,
                     timeSetListener,
                     cal.get(Calendar.HOUR),
                     cal.get(Calendar.MINUTE),
                     false)
                .show()
            }
        })
        updateTimeInView()




        if(position >= 0){
            view.title_edit_text.setText(taskList[position].title)
            //view.time_edit_text.setText(taskList[position].duration)
            view.hour_input.setText(taskList[position].hour.toString())
            view.minute_input.setText(taskList[position].minute.toString())

        }
        else{
            view.hour_input.setText("0")
            view.minute_input.setText("30")
        }
        builder.setPositiveButton(android.R.string.ok){ _,_ ->
            val title = view.title_edit_text.text.toString()
            var time = view.time_edit_text.text.toString()

            val year  =  cal.get(Calendar.YEAR)
            val month =cal.get(Calendar.MONTH)
            val day =    cal.get(Calendar.DAY_OF_MONTH)
            val hour = cal.get(Calendar.HOUR)
            val min = cal.get(Calendar.MINUTE)
            val duration_hour: Int
            val duration_min: Int
            if (view.hour_input.text.toString()=="") {
                duration_hour=0
            }
            else{
                duration_hour =view.hour_input.text.toString().toInt()
            }
            if (view.minute_input.text.toString()=="") {
                duration_min=0
            }
            else{
                duration_min =view.minute_input.text.toString().toInt()
            }




            if(position>=0){
                edit(position, title, duration_hour,duration_min)
            }else {
                add(Task(title, Date(day,month,year).toString(),"$hour : $min",duration_hour,duration_min))
            }
        }
        builder.setNegativeButton(android.R.string.cancel,null)
        builder.create().show()
    }



    fun  add(task: Task){
        taskRef.add(task)
    }


    fun  remove(position: Int){
        taskRef.document(taskList[position].id).delete()
    }

    fun  edit(position: Int, title: String, hour: Int, min:Int){
        taskList[position].title = title
        taskList[position].hour = hour
        taskList[position].minute = min
        taskRef.document(taskList[position].id).set(taskList[position])

    }



    fun  selectTeamAt(adapterPosition: Int){
        val task = taskList[adapterPosition]
        listener?.onSelected(task)
    }


    // create an OnDateSetListener
    val dateSetListener = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                               dayOfMonth: Int) {
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
    }

    val timeSetListener = object : TimePickerDialog.OnTimeSetListener {
        override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
            cal.set(Calendar.HOUR, p1)
            cal.set(Calendar.MINUTE, p2)

            updateTimeInView()
        }
    }


    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        date_button!!.text = "Date: " + sdf.format(cal.getTime())
    }

    private fun updateTimeInView() {
        val myFormat = "HH:mm" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        startTime_button!!.text = "Start Time: "+ sdf.format(cal.getTime())
    }

}