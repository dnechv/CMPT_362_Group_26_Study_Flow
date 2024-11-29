package com.example.studyflow.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studyflow.R
import com.example.studyflow.adapters.CoursesAdapter.ViewHolder
import com.example.studyflow.database_cloud.Courses
import com.example.studyflow.database_cloud.Homework

class HomeworkAdapter(private var homeworks: MutableList<Homework>) : RecyclerView.Adapter<HomeworkAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val HWName: TextView = itemView.findViewById(R.id.HW_name_item_tableview)
        private val HWdue: TextView = itemView.findViewById(R.id.HW_due_item_tableview)
        private val HWcourse: TextView = itemView.findViewById(R.id.HW_course_item_tableview)

        //binding data to views
        fun bind(homework: Homework) {
            val dueDateTime = "${homework.homeworkDueDate} ${homework.homeworkDueTime}"
            HWName.text = homework.homeworkName //setting the homework name
            HWdue.text = "${homework.homeworkDueDate} ${homework.homeworkDueTime}" //setting the homework due
            HWcourse.text = homework.courseName  //setting the homework course name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_homework, parent, false)
        return com.example.studyflow.adapters.HomeworkAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int = homeworks.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val HW = homeworks[position]
        holder.bind(HW)
    }

    fun updateHW(newHW: List<Homework>) {
        homeworks.clear()
        homeworks.addAll(newHW)
        notifyDataSetChanged()
    }

    fun addHomeWork(HW: Homework) {
        homeworks.add(HW)
        notifyItemInserted(homeworks.size - 1)
    }

    //get position of courses -> used for swipe gesture
    fun getHWAtPosition(position: Int): Homework {
        return homeworks[position]
    }

    // delete
    fun deleteHW(position: Int) {
        if (position in homeworks.indices) {
            homeworks.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    //restore course -> used for swipe gesture
    fun restoreHW(hw: Homework, position: Int) {
        homeworks.add(position, hw)
        notifyItemInserted(position)
    }

    fun updateHWAtPosition(updatedHW: Homework, position: Int) {
        if (position in homeworks.indices) {
            homeworks[position] = updatedHW
            notifyItemChanged(position)
        }
    }
}