package edu.rosehulman.caoz.rosegarden


import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_list.view.*
import java.time.LocalDateTime
import java.util.*


private const val ARG_UID = "UID"
class ListFragment : Fragment() {
    private var uid: String? = null
    private var listener: OnSelectedListener? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val listView = inflater.inflate(R.layout.fragment_list, container, false)
        val adapter = ListAdapter(context, listener,uid!!)
        listView.fab.setOnClickListener {
            adapter.getTaskAdapter().showAddDialog()
        }
        listView.recycler_view.adapter = adapter
        listView.recycler_view.layoutManager = LinearLayoutManager(context,OrientationHelper.HORIZONTAL,false)
        listView.recycler_view.setHasFixedSize(true)
        listView.recycler_view.scrollToPosition(date-1)


        return listView
    }


    fun onButtonPressed(task: Task) {
        listener?.onSelected(task)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSelectedListener) {
            listener = context
            arguments?.let {
                uid = it.getString(ARG_UID)
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
        fun newInstance(uid: String) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_UID, uid)
                }
            }
    }



    interface OnSelectedListener {
        fun onSelected(task: Task)
    }

}
