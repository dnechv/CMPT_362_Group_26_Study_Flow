package com.example.studyflow.animation



//adds swipe functionality to the recycler view - courses list


//imports

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper // swipe gesture
import androidx.recyclerview.widget.RecyclerView
import com.example.studyflow.adapters.CoursesAdapter
import com.google.android.material.snackbar.Snackbar

class SwipeGesture (

    private val coursesAdapter: CoursesAdapter



) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    //swipe left
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    //swipe left
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition


        //get deleted course
        val deletedCourse = coursesAdapter.getCourseAtPosition(position)

        coursesAdapter.deleteCourse(position)


        ///undo option
        Snackbar.make(viewHolder.itemView, "Course deleted", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                coursesAdapter.restoreCourse(deletedCourse, position)
            }
            .show()
    }

    //draw the background
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val background = ColorDrawable(Color.RED)
        background.setBounds(
            viewHolder.itemView.right + dX.toInt(),
            viewHolder.itemView.top,
            viewHolder.itemView.right,
            viewHolder.itemView.bottom
        )
        background.draw(c)
    }
}
