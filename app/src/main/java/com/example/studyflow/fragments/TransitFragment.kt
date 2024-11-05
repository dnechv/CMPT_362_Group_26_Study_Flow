package com.example.studyflow.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.mapbox.maps.MapView
import com.example.studyflow.R
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.plugin.scalebar.scalebar

// This fragment allows the user to see transit options
class TransitFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_transit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapView = view.findViewById<MapView>(R.id.map_view)
        mapView.scalebar.enabled = false
        mapView.mapboxMap.setBounds(CameraBoundsOptions.Builder().bounds(CoordinateBounds(
            Point.fromLngLat(-123.422, 48.95),
            Point.fromLngLat(-122.072, 49.49)
        )).build())

        ViewCompat.requestApplyInsets(view)
    }
}