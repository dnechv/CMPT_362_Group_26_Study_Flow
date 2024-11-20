package com.example.studyflow.adapters


//imports

//adapter for courses fragment -> will allow the user to add courses to their current term

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studyflow.R
import com.example.studyflow.database_cloud.Courses

class CoursesAdapter(private var courses: MutableList<Courses>) : RecyclerView.Adapter<CoursesAdapter.ViewHolder>() {

    //viewholder to hold views
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val courseName: TextView = itemView.findViewById(R.id.course_name_item_tableview)
        private val courseTerm: TextView = itemView.findViewById(R.id.course_term_item_tableview)

        //binding data to views
        fun bind(course: Courses) {
            courseName.text = course.courseName //setting the course name
            courseTerm.text = course.courseTerm //setting the course term
        }
    }

    //inflating the layout for each view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return ViewHolder(view)
    }


    //methods

    //get the number of items in the list
    override fun getItemCount(): Int = courses.size

    //binding data to views
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val course = courses[position]
        holder.bind(course)
    }

    //update the list of courses
    fun updateCourses(newCourses: List<Courses>) {
        courses.clear()
        courses.addAll(newCourses)
        notifyDataSetChanged()
    }

    // delete
    fun deleteCourse(position: Int) {
        if (position in courses.indices) {
            courses.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    // add course
    fun addCourse(course: Courses) {
        courses.add(course)
        notifyItemInserted(courses.size - 1)
    }



    //get position of courses -> used for swipe gesture
    fun getCourseAtPosition(position: Int): Courses {
        return courses[position]
    }

    //restore course -> used for swipe gesture
    fun restoreCourse(course: Courses, position: Int) {
        courses.add(position, course)
        notifyItemInserted(position)
    }
}