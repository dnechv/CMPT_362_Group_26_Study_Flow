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
            HWName.text = homework.homeworkName //setting the homework name
            HWdue.text = homework.homeworkDueTime //setting the homework term
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
}