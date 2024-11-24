package com.example.studyflow.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.example.studyflow.R
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.RenderedQueryGeometry
import com.mapbox.maps.RenderedQueryOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapState
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.maps.extension.compose.style.MapStyle
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import kotlinx.coroutines.launch

// This fragment allows the user to see transit options
@OptIn(ExperimentalFoundationApi::class)
class TransitFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_transit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val composeView = view.findViewById<ComposeView>(R.id.compose_view)

        composeView.setContent {
            MaterialTheme(
                colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
            ) {
                TransitFragmentComposable()
            }
        }

        ViewCompat.requestApplyInsets(view)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TransitFragmentComposable() {
        val sheetState = rememberBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            defineValues = {
                SheetValue.Collapsed at height(100.dp)
                SheetValue.PartiallyExpanded at offset(percent = 60)
                SheetValue.Expanded at contentHeight
            }
        )
        val scaffoldState = rememberBottomSheetScaffoldState(sheetState)

        val coroutineScope = rememberCoroutineScope()
        val mapState = rememberMapState()
        var stop by remember {
            mutableStateOf<Stop?>(null)
        }
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
                if (stop != null) {
                    Text(stop!!.name, Modifier.padding(16.dp))
                } else {
                    Text("Tap on a transit stop on the map", Modifier.padding(16.dp))
                }
            },
            content = {
                MapboxMap(
                    Modifier.fillMaxSize(),
                    mapState = mapState,
                    mapViewportState = rememberMapViewportState {
                        setCameraOptions {
                            zoom(11.0)
                            center(Point.fromLngLat(-122.92, 49.28))
                            pitch(0.0)
                            bearing(0.0)
                        }
                    },
                    onMapClickListener = {
                        coroutineScope.launch {
                            stop = mapState.queryStopAt(it)
                        }
                        false
                    },
                    scaleBar = {},
                    style = {
                        MapStyle(style = "mapbox://styles/cyrusqng/cm38qzmej009t01q7gago6aoe")
                    }
                ) {
                    MapEffect() { mapView ->
                        mapView.mapboxMap.setBounds(
                            CameraBoundsOptions.Builder().bounds(
                                CoordinateBounds( // TransLink does not operate beyond these bounds
                                    Point.fromLngLat(-123.494, 48.95),
                                    Point.fromLngLat(-122.072, 49.49)
                                )
                            ).build()
                        )
                    }
                }
            }
        )
    }

    private suspend fun MapState.queryStopAt(point: Point): Stop? =
        queryRenderedFeatures(
            RenderedQueryGeometry(pixelForCoordinate(point)),
            RenderedQueryOptions(listOf("translink-stops"), null)
        ).value?.let { features ->
            features.firstNotNullOfOrNull {
                val code = it.queriedFeature.feature.properties()?.get("stop_code")?.asInt
                val name = it.queriedFeature.feature.properties()?.get("stop_name")?.asString
                if (code != null && name != null) Stop(code = code, name = name) else null
            }
        }

    companion object {
        enum class SheetValue { Collapsed, PartiallyExpanded, Expanded }
        data class Stop(val code: Int, val name: String)
    }
}
