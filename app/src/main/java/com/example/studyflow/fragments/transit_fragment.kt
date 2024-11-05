package com.example.studyflow.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

//transit fragment will allow the user to see transit updates for simon fraser university

class transit_fragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //inflate layout for the fragment - transit_fragment.xml
        return inflater.inflate(com.example.studyflow.R.layout.transit_fragment, container, false)
    }
}