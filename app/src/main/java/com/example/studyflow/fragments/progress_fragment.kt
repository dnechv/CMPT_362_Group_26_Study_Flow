package com.example.studyflow.fragments


//Progress Fragment will display the user's progress in the current term as a chart


//imports
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.studyflow.R

class progress_fragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment -> progress_fragment.xml
        return inflater.inflate(R.layout.progress_fragment, container, false)
    }
}