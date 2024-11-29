package com.example.studyflow.animation

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.studyflow.adapters.HomeworkAdapter
import com.example.studyflow.database_cloud.Homework
import com.google.android.material.snackbar.Snackbar

class HomeWorkSwipeGesture (


    //variables
    private val HWAdapter: HomeworkAdapter,


    //callback for delete
    private val onDeleteCallback: (Homework, Int) -> Unit,


    //callback for edit
    private val onEditCallback: (Homework, Int) -> Unit



) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    //swipe left - delete course
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
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
                val deletedHW = HWAdapter.getHWAtPosition(position)


                //notify the fragment about the course that was deleted
                onDeleteCallback(deletedHW, position)

                //update the database - delete
                HWAdapter.deleteHW(position)


                ///undo option - shake
                Snackbar.make(viewHolder.itemView, "Homework deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        HWAdapter.restoreHW(deletedHW, position)
                    }
                    .show()
            }


            //swipe right edit course
            ItemTouchHelper.RIGHT -> {
                //SWIPE RIGHT TO EDIT THE COURSE

                //get the course
                val courseToEdit = HWAdapter.getHWAtPosition(position)


                //notify the fragment about the course that was edited
                onEditCallback(courseToEdit, position)

                //update the database - edit
                HWAdapter.notifyItemChanged(position)
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

            // calculate the text position

            val textPadding = 450f

            val textX = itemView.right + dX + textPadding

            val textY = itemView.top + (itemView.height / 2f) - (paint.descent() + paint.ascent()) / 2

            // draw the text
            if (textX < itemView.right) {

            }
            c.drawText(text, textX, textY, paint)


            //swipe rIGHT edit
        } else if (dX > 0) {



            background.color = Color.BLUE //set the background color for swipe right

            background.setBounds(
                itemView.left,
                itemView.top,
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

                itemView.left + dX / 2,

                itemView.top + itemView.height / 2f - (paint.descent() + paint.ascent()) / 2,
                paint

            )
        }


    }
}
