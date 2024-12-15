package com.example.easydo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.easydo.R
import com.example.easydo.model.Todo

/**
 * This class is the bridge between the To-do data layer and the [RecyclerView] UI.
 */
class TodoListAdapter(
    private val dataSet: Array<Todo>,
    private val todoItemListener: TodoItemListener
) : RecyclerView.Adapter<TodoListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView
        val descTextView: TextView
        val completedCheckbox: CheckBox
        val parentView: ConstraintLayout

        init {
            titleTextView = view.findViewById(R.id.titleTextView)
            descTextView = view.findViewById(R.id.descTextView)
            completedCheckbox = view.findViewById(R.id.completedCheckBox)
            parentView = view.findViewById(R.id.parentView)

            // Add click listener to parent view (CardView)
            parentView.setOnClickListener {
                todoItemListener.onClick(dataSet[adapterPosition])
            }

            // Add long click to parent view (CardView)
            parentView.isLongClickable = true
            parentView.setOnLongClickListener {
                todoItemListener.onLongClick(dataSet[adapterPosition])
                false // It is important to return false so that the system receives the event and shows the context menu
            }

            // Add check listener to checkbox
            completedCheckbox.setOnCheckedChangeListener { _, checked ->
                todoItemListener.onCheckChanged(dataSet[adapterPosition], checked)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.todo_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val todo = dataSet[position]

        viewHolder.titleTextView.text = todo.title
        viewHolder.descTextView.text = todo.description
        viewHolder.completedCheckbox.isChecked = todo.completed
    }

    override fun getItemCount() = dataSet.size

}

/**
 * This interface creates a channel between the adapter and the hosting Activity/Fragment,
 * so that user interaction events can be propagated upwards.
 */
interface TodoItemListener {
    fun onCheckChanged(todo: Todo, checked: Boolean)
    fun onLongClick(todo: Todo)
    fun onClick(todo: Todo)
}