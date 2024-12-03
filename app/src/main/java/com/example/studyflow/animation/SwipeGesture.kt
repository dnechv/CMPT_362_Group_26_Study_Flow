package com.example.studyflow.animation



//adds swipe functionality to the recycler view - courses list
//swipe left to delete
//swipe right to edit


//imports

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper // swipe gesture
import androidx.recyclerview.widget.RecyclerView
import com.example.studyflow.adapters.CoursesAdapter
import com.example.studyflow.database_cloud.Courses
import com.google.android.material.snackbar.Snackbar


//the following class defines the logic for swiping left and right on a homework item
//swiping left deletes the homework item using recycler view

class SwipeGesture (


    //variables
    private val coursesAdapter: CoursesAdapter,


    //callback for delete
    private val onDeleteCallback: (Courses, Int) -> Unit,


    //callback for edit
    private val onEditCallback: (Courses, Int) -> Unit



) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    //drap and drop not supported but this method must be impemented
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {

        //ALWAYS RETURNS FALSE
        return false
    }

    //swipe left
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {


        //get the position of the course
        val position = viewHolder.adapterPosition



        when (direction){


        ItemTouchHelper.LEFT -> {


            //SWIPE LEFT TO DELETE THE COURSE

            //get deleted course
            val deletedCourse = coursesAdapter.getCourseAtPosition(position)


            //notify the fragment about the course that was deleted
            onDeleteCallback(deletedCourse, position)

            //update the database - delete
            coursesAdapter.deleteCourse(position)


            ///undo option - shake
            Snackbar.make(viewHolder.itemView, "Course deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo") {
                    coursesAdapter.restoreCourse(deletedCourse, position)
                }

                .show()
        }


            //swipe right edit course
            ItemTouchHelper.RIGHT -> {
                //SWIPE RIGHT TO EDIT THE COURSE

                //get the course
                val courseToEdit = coursesAdapter.getCourseAtPosition(position)


                //notify the fragment about the course that was edited
                onEditCallback(courseToEdit, position)

                //update the database - edit
                coursesAdapter.notifyItemChanged(position)
            }


        }


        }







    //draw the background - when swipped
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

        //itemView variable
        val itemView = viewHolder.itemView

        //background color
        val background = ColorDrawable(Color.RED)

        //draw background
        background.setBounds(


            //left swipe -> dx becomes negative adjusting the background
            viewHolder.itemView.right + dX.toInt(),

            viewHolder.itemView.top,

            viewHolder.itemView.right,

            viewHolder.itemView.bottom
        )

        background.draw(c)


        //add text Swipe Left to Delete

        if (dX<0){

            //define text
            val text = "Swipe Left to Delete"

            //paint of the text
            val paint = android.graphics.Paint().apply {

                color = android.graphics.Color.WHITE
                textSize = 50f
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true

            }


            //draw the text - > positioned in the centre


            //padding relative to the items edge -> readability of the text
            val textPadding = 450f


            //horizontal positioning of the text
            val textX = itemView.right + dX + textPadding


            //vertical position of the text
            val textY = itemView.top + (itemView.height / 2f) - (paint.descent() + paint.ascent()) / 2

            // draw the text only within bounds
            if (textX < itemView.right) {

                //

            }
                c.drawText(text, textX, textY, paint)


            //swipe rIGHT edit
            } else if (dX > 0) {


            //blue on swipe right
            background.color = Color.BLUE


            //set bounds
            background.setBounds(

                itemView.left,
                itemView.top,

                //right swipe -> dx becomes positive adjusting the background
                itemView.left + dX.toInt(),


                itemView.bottom

            )
            background.draw(c)

            // EDIT TEXT
            val paint = android.graphics.Paint().apply {



                color = Color.WHITE
                textSize = 50f
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true



            }
            c.drawText(


                "Swipe Right to Edit",


                //position of the text -> dynamically placing the text for better UI as dx changes
                itemView.left + dX / 2,


                //vertical position of the text
                itemView.top + itemView.height / 2f - (paint.descent() + paint.ascent()) / 2,
                paint

            )
        }


    }
}
