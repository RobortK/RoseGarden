package edu.rosehulman.caoz.rosegarden


import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_list.view.*
import java.text.DateFormatSymbols
import java.text.FieldPosition
import java.time.LocalDateTime
import java.util.*


private const val ARG_UID = "UID"
private const val ARG_YEAR = "YEAR"
private const val ARG_MONTH = "MONTH"
private const val ARG_DAY = "DAY"

class ListFragment : Fragment() {
    private var uid: String? = null
    private var listener: OnSelectedListener? = null
    private  var cal: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val date = cal.get(Calendar.DAY_OF_MONTH)
        val listView = inflater.inflate(R.layout.fragment_list, container, false)
        val adapter = ListAdapter(context, listener,uid!!,cal )
        listView.fab.setOnClickListener {
            adapter.getTaskAdapter().showAddDialog()
        }
        listView.month_title.text = DateFormatSymbols().getMonths()[cal.get(Calendar.MONTH)]+" "+ cal.get(Calendar.YEAR).toString()
        listView.month_title.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(context!!,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        })
        listView.prev_button.setOnClickListener {
            listener!!.changeMonth(cal,false)
        }
        listView.next_button.setOnClickListener {
            listener!!.changeMonth(cal,true)
        }


        listView.recycler_view.adapter = adapter
        listView.recycler_view.layoutManager = LinearLayoutManager(context,OrientationHelper.HORIZONTAL,false)
        listView.recycler_view.setHasFixedSize(true)
        listView.recycler_view.scrollToPosition(date-1)


        return listView
    }

    val dateSetListener = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                               dayOfMonth: Int) {
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            listener!!.chooseDate(cal)
        }
    }

//    fun onButtonPressed(task: Task) {
//        listener?.onSelected(task)
//    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSelectedListener) {
            listener = context
            arguments?.let {
                uid = it.getString(ARG_UID)
                cal.set(it.getInt(ARG_YEAR),it.getInt(ARG_MONTH),it.getInt(ARG_DAY))
            }

        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    companion object {
        @JvmStatic
        fun newInstance(uid: String,calender:Calendar) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_UID, uid)
                    putInt(ARG_YEAR,calender.get(Calendar.YEAR))
                    putInt(ARG_MONTH,calender.get(Calendar.MONTH))
                    putInt(ARG_DAY,calender.get(Calendar.DAY_OF_MONTH))

                }
            }
    }



    interface OnSelectedListener {
        fun onSelected(task: Task, position: Int, adapter: taskAdapter)
        fun changeMonth(calendar: Calendar,isNext: Boolean)
        fun chooseDate(calendar: Calendar)
        fun resetList()

    }

}
