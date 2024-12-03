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


    //adapter for homework
    private val HWAdapter: HomeworkAdapter,


    //callback for delete
    private val onDeleteCallback: (Homework, Int) -> Unit,


    //callback for edit
    private val onEditCallback: (Homework, Int) -> Unit



) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    //DRAG AND DROP not supported to false
    override fun onMove(

        //recycler view
        recyclerView: RecyclerView,

        //view holder of the recycler view -> courses
        viewHolder: RecyclerView.ViewHolder,


        //target ->
        target: RecyclerView.ViewHolder
    ): Boolean {


        //ALWAYS RETURNS FALSE
        return false
    }






    //swipe left gesture
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {


        //get the position of the course
        val position = viewHolder.adapterPosition



        //check the position
        when (direction){


            //left
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


        c: Canvas, //canvas
        recyclerView: RecyclerView, //recycler view
        viewHolder: RecyclerView.ViewHolder,//view holder of the recycler view -> courses
        dX: Float, //swipe on x
        dY: Float, //swipe on y
        actionState: Int, // interaction type swipe or etc
        isCurrentlyActive: Boolean


    ) {

        //call the super class
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        //itemView variable
        val itemView = viewHolder.itemView

        //background color
        val background = ColorDrawable(Color.RED)

        //draw background -> defines the area of canvas
        background.setBounds(

            //left swipe -> dx becomes negative adjusting the background
            viewHolder.itemView.right + dX.toInt(),

            //top, right, bottom bounds
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

            // calculate the text position -> dynamically placing the text for better UI

            //padding relative to the items edge -> readability of the text
            val textPadding = 450f


            //horizontal positioning of the text
            val textX = itemView.right + dX + textPadding

            //vertical position of the text
            val textY = itemView.top + (itemView.height / 2f) - (paint.descent() + paint.ascent()) / 2

            // draw the text only within bounds
            if (textX < itemView.right) {


            }
            c.drawText(text, textX, textY, paint)


            //swipe rIGHT edit
        } else if (dX > 0) {


            //blue background
            background.color = Color.BLUE

            //draw the background
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
