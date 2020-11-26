package com.saarthak.proteams.ui.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saarthak.proteams.R
import com.saarthak.proteams.TaskListActivity
import com.saarthak.proteams.model.Task
import kotlinx.android.synthetic.main.item_task.view.*
import java.util.*
import kotlin.collections.ArrayList

open class TaskListAdapter(private val context: Context, private val tasks: ArrayList<Task>) :
    RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {

    private var dragFrom = -1
    private var dragTo = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)

        val layoutParam = LinearLayout.LayoutParams(
            (parent.width * .8).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParam.setMargins((15.toDp()).toPx(), 0, (40.toDp()).toPx(), 0)

        v.layoutParams = layoutParam
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]

        holder.apply {
            if (position == tasks.size - 1) {
                addList_tv.visibility = View.VISIBLE
                taskItem_ll.visibility = View.GONE
            } else {
                addList_tv.visibility = View.GONE
                taskItem_ll.visibility = View.VISIBLE
            }

            taskTitle_tv.text = task.title
            addList_tv.setOnClickListener {
                addList_tv.visibility = View.GONE
                addList_cv.visibility = View.VISIBLE
            }

            cancelTaskListName_B.setOnClickListener {
                taskListName_et.text?.clear()
                addList_tv.visibility = View.VISIBLE
                addList_cv.visibility = View.GONE
            }

            okTaskListName_B.setOnClickListener {
                val name = taskListName_et.text.toString()
                if (name.isNotEmpty()) {
                    if (context is TaskListActivity) context.createTaskList(name)
                } else Toast.makeText(context, "List name can't be empty !!", Toast.LENGTH_SHORT)
                    .show()
            }

            editTitle_B.setOnClickListener {
                editTaskListName_et.setText(task.title)
                taskTitle_ll.visibility = View.GONE
                editTaskList_cv.visibility = View.VISIBLE
            }

            deleteTitle_B.setOnClickListener {
                showAlertDialog(position, task.title)
            }

            cancelEditTaskListName_B.setOnClickListener {
                editTaskListName_et.text?.clear()
                taskTitle_ll.visibility = View.VISIBLE
                editTaskList_cv.visibility = View.GONE
            }

            okEditTaskListName_B.setOnClickListener {
                val name = editTaskListName_et.text.toString()
                if (name.isNotEmpty()) {
                    if (context is TaskListActivity) context.updateTaskList(position, name, task)
                } else Toast.makeText(context, "List name can't be empty !!", Toast.LENGTH_SHORT)
                    .show()
            }

            addCard_B.setOnClickListener {
                addCard_B.visibility = View.GONE
                cardName_cv.visibility = View.VISIBLE
            }

            cancelCardName_B.setOnClickListener {
                cardName_et.text?.clear()
                addCard_B.visibility = View.VISIBLE
                cardName_cv.visibility = View.GONE
            }

            okCardName_B.setOnClickListener {
                val name = cardName_et.text.toString()
                if (name.isNotEmpty()) {
                    if (context is TaskListActivity) context.addCardToTasks(position, name)
                } else Toast.makeText(context, "Card name can't be empty !!", Toast.LENGTH_SHORT)
                    .show()
            }

            cardList_rv.layoutManager = LinearLayoutManager(context)
            cardList_rv.setHasFixedSize(true)

            val adapter = CardListAdapter(context, task.cards)
            cardList_rv.adapter = adapter
            adapter.setOnClickListener(object : CardListAdapter.OnClickListener {
                override fun onClick(pos: Int) {
                    if (context is TaskListActivity) context.cardDetails(position, pos)
                }
            })

            val dividerItemDecor = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            cardList_rv.addItemDecoration(dividerItemDecor)

            val helper = ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val s = viewHolder.adapterPosition
                    val e = target.adapterPosition

                    if (dragFrom == -1) dragFrom = s
                    if (dragTo == -1) dragTo = e

                    Collections.swap(tasks[position].cards, s, e)
                    adapter.notifyItemMoved(s, e)

                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)

                    if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
                        (context as TaskListActivity).updateCardPos(position, tasks[position].cards)
                    }

                    dragFrom = -1
                    dragTo = -1
                }
            })

            helper.attachToRecyclerView(cardList_rv)
        }
    }

    override fun getItemCount() = tasks.size

    private fun Int.toDp() = (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx() = (this * Resources.getSystem().displayMetrics.density).toInt()

    private fun showAlertDialog(pos: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        builder.apply {
            setTitle("Alert")
            setMessage("Are you sure you want to delete $title ?")
            setIcon(R.drawable.ic_warning)
            setNegativeButton("No") { inter, _ ->
                inter.dismiss()
            }
        }
        builder.setPositiveButton("Yes") { inter, _ ->
            inter.dismiss()
            if (context is TaskListActivity) (context).deleteTaskList(pos)
        }

        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val addList_tv = v.addList_tv
        val taskItem_ll = v.taskItem_ll
        val taskTitle_ll = v.taskTitle_ll
        val taskTitle_tv = v.taskTitle_tv
        val addList_cv = v.addList_cv
        val cancelTaskListName_B = v.cancelTaskListName_B
        val taskListName_et = v.taskListName_et
        val okTaskListName_B = v.okTaskListName_B
        val editTitle_B = v.editTitle_B
        val deleteTitle_B = v.deleteTitle_B
        val editTaskList_cv = v.editTaskList_cv
        val cancelEditTaskListName_B = v.cancelEditTaskListName_B
        val editTaskListName_et = v.editTaskListName_et
        val okEditTaskListName_B = v.okEditTaskListName_B
        val cardList_rv = v.cardList_rv
        val cardName_cv = v.cardName_cv
        val cancelCardName_B = v.cancelCardName_B
        val cardName_et = v.cardName_et
        val okCardName_B = v.okCardName_B
        val addCard_B = v.addCard_B
    }
}