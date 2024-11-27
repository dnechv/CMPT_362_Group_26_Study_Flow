package com.example.studyflow.adapters

// imports

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studyflow.R
import com.example.studyflow.database_cloud.Courses

class CoursesAdapter(

    // list of courses
    private var courses: MutableList<Courses>,

    // on click listener for each course
    private val onItemClickListener: (Courses) -> Unit

) : RecyclerView.Adapter<CoursesAdapter.ViewHolder>() {

    // collection of views
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val courseName: TextView = itemView.findViewById(R.id.course_name_item_tableview)
        private val courseTerm: TextView = itemView.findViewById(R.id.course_term_item_tableview)
        private val courseDates: TextView = itemView.findViewById(R.id.course_dates_item_tableview)
        private val courseDays: TextView = itemView.findViewById(R.id.course_days_item_tableview)
        private val courseTime: TextView = itemView.findViewById(R.id.course_time_item_tableview)

        // binding data to views
        fun bind(course: Courses, onItemClickListener: (Courses) -> Unit) {
            // name
            courseName.text = course.courseName

            // term
            courseTerm.text = course.courseTerm

            // dates
            courseDates.text = "From: ${course.courseStartDate} to ${course.courseEndDate}"

            // time
            val startTime = course.courseStartTime ?: "N/A"
            val endTime = course.courseEndTime ?: "N/A"
            courseTime.text = "Time: $startTime - $endTime"

            // recurring days
            courseDays.text = "Days: ${course.courseDays.joinToString(", ")}"



            // on click listener
            itemView.setOnClickListener {

                // pass the course to the onItemClickListener
                onItemClickListener(course)
            }
        }
    }

    // inflating the layout for each view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return ViewHolder(view)
    }

    // get the number of items in the list
    override fun getItemCount(): Int = courses.size

    // binding data to views
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val course = courses[position]
        holder.bind(course, onItemClickListener) // Pass the onItemClickListener to bind
    }

    // update the list of courses
    fun updateCourses(newCourses: List<Courses>) {
        courses.clear()
        courses.addAll(newCourses)
        notifyDataSetChanged()
    }

    // delete course
    fun deleteCourse(position: Int) {
        if (position in courses.indices) {
            courses.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    // update a specific course at a position
    fun updateCourseAtPosition(updatedCourse: Courses, position: Int) {
        if (position in courses.indices) {
            courses[position] = updatedCourse
            notifyItemChanged(position)
        }
    }

    // add course
    fun addCourse(course: Courses) {
        courses.add(course)
        notifyItemInserted(courses.size - 1)
    }

    // get position of a course -> used for swipe gesture
    fun getCourseAtPosition(position: Int): Courses {
        return courses[position]
    }

    // restore course -> used for swipe gesture
    fun restoreCourse(course: Courses, position: Int) {
        courses.add(position, course)
        notifyItemInserted(position)
    }

    //returns courses
    fun getCourses(): List<Courses> {
        return courses
    }
}