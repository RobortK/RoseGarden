package edu.rosehulman.caoz.rosegarden


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_list.view.*




private const val ARG_UID = "UID"
class ListFragment : Fragment() {
    private var uid: String? = null
    private var listener: OnSelectedListener? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val listView = inflater.inflate(R.layout.fragment_list, container, false)
        val adapter = ListAdapter(context, listener,uid!!)
        listView.fab.setOnClickListener {
            adapter.showAddDialog()
        }
        listView.recycler_view.adapter = adapter
        listView.recycler_view.layoutManager = LinearLayoutManager(context)
        listView.recycler_view.setHasFixedSize(true)

        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition

                    if (direction == ItemTouchHelper.RIGHT) {
                        adapter.remove(position)
                    }
                }
            }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(listView.recycler_view)

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
