package edu.rosehulman.caoz.rosegarden

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.dialog_add.view.*

class ListAdapter(var context: Context?, var listener: ListFragment.OnSelectedListener?,var uid: String) : RecyclerView.Adapter<TaskViewHolder>() {

    private  val  taskList = ArrayList<Task>()
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
                for (docChange in snapshot!!.documentChanges){
                    val task = Task.fromSnapShot(docChange.document)

                        when (docChange.type) {
                            DocumentChange.Type.ADDED -> {
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.task_view, parent, false)
        return TaskViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(taskList[position])
    }
    override fun getItemCount() = taskList.size

//    fun getPicForOwner(uid: String){
//        val path = FieldPath.of("ui")
//    }



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
        if(position >= 0){
            view.title_edit_text.setText(taskList[position].title)
            view.time_edit_text.setText(taskList[position].time)
        }
        builder.setPositiveButton(android.R.string.ok){ _,_ ->
            val title = view.title_edit_text.text.toString()
            var time = view.time_edit_text.text.toString()
            if(time==""){
                time = "30 Mins"
            }
            if(position>=0){
                edit(position, title, time)
            }else {
                add(Task(title, time))
                //updateQuote(MovieQuote(quote,movie))
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

    fun  edit(position: Int, title: String, time: String){
                taskList[position].title = title
                taskList[position].time = time
                taskRef.document(taskList[position].id).set(taskList[position])

    }



    fun  selectTeamAt(adapterPosition: Int){
        val task = taskList[adapterPosition]
        listener?.onSelected(task)
    }



}