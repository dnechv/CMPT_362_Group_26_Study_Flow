package com.example.studyflow.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mapbox.maps.MapView
import com.example.studyflow.R
import com.example.studyflow.view_models.TransitViewModel
import com.mapbox.bindgen.Expected
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.QueriedRenderedFeature
import com.mapbox.maps.RenderedQueryGeometry
import com.mapbox.maps.RenderedQueryOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener



//mapbox import

// This fragment allows the user to see transit options
class TransitFragment : Fragment(), OnMapClickListener {
    private val viewModel: TransitViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_transit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)





        val mapView = view.findViewById<MapView>(R.id.map_view)

        mapView.mapboxMap.apply {
            setBounds(
                CameraBoundsOptions.Builder().bounds(
                    CoordinateBounds( // TransLink does not operate beyond these bounds
                        Point.fromLngLat(-123.422, 48.95),
                        Point.fromLngLat(-122.072, 49.49)
                    )
                ).build()
            )
            addOnMapClickListener(this@TransitFragment)
        }

        ViewCompat.requestApplyInsets(view)
    }

    override fun onMapClick(point: Point): Boolean {
        val mapView = view?.findViewById<MapView>(R.id.map_view) ?: return false
        val name = requireView().findViewById<TextView>(R.id.name)
        val subtitle = requireView().findViewById<TextView>(R.id.subtitle)
        mapView.mapboxMap.apply {
            queryRenderedFeatures(
                RenderedQueryGeometry(pixelForCoordinate(point)),
                RenderedQueryOptions(listOf("translink-stops"), null)
            ) {
                onFeatureClick(it, {
                    name.text = ""
                    subtitle.text = ""
                }) { feature ->
                    flyTo(CameraOptions.Builder().zoom(18.0).center(point).build())
                    val stationName = feature.properties()?.get("stop_name")?.asString ?: ""
                    val stationId = feature.properties()?.get("stop_code")?.asInt
                    name.text = stationName
                        .replace(Regex("^Westbound"), "WB")
                        .replace(Regex("^Eastbound"), "EB")
                        .replace(Regex("@"), "at")
                    subtitle.text = stationId?.toString() ?: ""
                }
            }
        }
        return true
    }

    private fun onFeatureClick(
        expected: Expected<String, List<QueriedRenderedFeature>>,
        noFeatureCallback: () -> Unit,
        featureCallback: (Feature) -> Unit
    ) {
        if (expected.isValue && expected.value?.size!! > 0) {
            featureCallback.invoke(expected.value!![0].queriedFeature.feature)
        } else {
            noFeatureCallback.invoke()
        }
    }
}