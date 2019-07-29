package edu.rosehulman.caoz.rosegarden


import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_task.view.*


private const val ARG_TASK = "Task"



class TaskFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var task: Task? = null

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            task = it.getParcelable<Task>(ARG_TASK)

        }



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_task, container, false)
        view.task_name.text = task!!.title
        view.time_remain.text = task!!.time
        view.due_time.text = task!!.time

        Thread(Runnable {
            var pStatus = 0
            while (pStatus < 100) {
                pStatus += 1
                handler.post {
                    // TODO Auto-generated method stub
                   view.progressBar.progress = pStatus
                    view.percentage.text = pStatus.toString() + "%"
                }
                try {
                    // Sleep for 200 milliseconds.
                    // Just to display the progress slowly
                    Thread.sleep(16) //thread will take approx 3 seconds to finish
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }).start()




        return view
    }


    companion object {

        @JvmStatic
        fun newInstance(task: Task) =
            TaskFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_TASK,task)
                }
            }
    }
}
