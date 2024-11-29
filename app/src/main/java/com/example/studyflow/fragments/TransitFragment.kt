package com.example.studyflow.fragments

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.composables.core.HorizontalSeparator
import com.example.studyflow.R
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.RenderedQueryGeometry
import com.mapbox.maps.RenderedQueryOptions
import com.mapbox.maps.dsl.cameraOptions
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
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.periodUntil
import kotlinx.datetime.toLocalDateTime

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
        val requestQueue = Volley.newRequestQueue(context)
        var stop by remember {
            mutableStateOf<Stop?>(null)
        }
        var loading by remember {
            mutableStateOf(false)
        }
        var requestError by remember {
            mutableStateOf(false)
        }
        var departures by remember {
            mutableStateOf(emptyList<Departure>())
        }
        val sheetState = rememberBottomSheetState(
            initialValue = SheetValue.Collapsed,
            defineValues = {
                SheetValue.Collapsed at height(100.dp)
                SheetValue.PartiallyExpanded at offset(percent = 60)
                SheetValue.Expanded at contentHeight
            }
        )
        val scaffoldState = rememberBottomSheetScaffoldState(sheetState)

        val coroutineScope = rememberCoroutineScope()
        val mapState = rememberMapState()
        val mapViewportState = rememberMapViewportState {
            setCameraOptions {
                zoom(12.0)
                center(Point.fromLngLat(-122.92, 49.28))
                pitch(0.0)
                bearing(0.0)
            }
        }
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
                Column(
                    Modifier
                        .fillMaxHeight()
                ) {
                    if (stop != null) {
                        StopInfo(stop!!)
                        Spacer(Modifier.height(8.dp))
                        if (loading) {
                            Loading()
                        } else if (requestError) {
                            RequestError()
                        } else if (departures.isEmpty()) {
                            NoDepartures()
                        } else {
                            LazyColumn {
                                items(departures) { DepartureRow(it) }
                            }
                        }
                    } else {
                        Text(
                            stringResource(R.string.transit_action_hint),
                            Modifier.padding(20.dp, 8.dp)
                        )
                        Spacer(modifier = Modifier.weight(1.0f))
                    }
                }
            }
        ) {
            MapboxMap(
                Modifier.fillMaxSize(),
                mapState = mapState,
                mapViewportState = mapViewportState,
                onMapClickListener = {
                    requestError = false
                    coroutineScope.launch {
                        stop = mapState.queryStopAt(it)
                        departures = emptyList()
                        if (stop != null) {
                            mapViewportState.flyTo(
                                cameraOptions {
                                    if (mapViewportState.cameraState!!.zoom < 18.0) zoom(18.0)
                                    center(it)
                                }
                            )
                            loading = true
                            val request = JsonObjectRequest(
                                Request.Method.GET,
                                "https://transit.cqng.ca/api/info/${stop!!.id}/limit/10",
                                null,
                                { response ->
                                    requestError = false
                                    loading = false
                                    val departuresArray = response.getJSONArray("departures")
                                    departures = (0 until departuresArray.length()).map {
                                        val departure = departuresArray.getJSONObject(it)
                                        Departure(
                                            id = departure.getInt("id"),
                                            route = departure.getString("route"),
                                            destination = departure.getString("destination"),
                                            time = Instant.fromEpochMilliseconds(
                                                departure.getLong(
                                                    "time"
                                                )
                                            )
                                        )
                                    }

                                },
                                {
                                    requestError = true
                                    loading = false
                                })
                            requestQueue.add(request)
                        }
                    }
                    false
                },
                scaleBar = {},
                style = {
                    MapStyle(style = stringResource(R.string.transit_mapbox_style_uri))
                }
            ) {
                MapEffect { mapView ->
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
    }

    @Composable
    fun StopInfo(stop: Stop) {
        Column(Modifier.padding(20.dp, 4.dp)) {
            Text(stop.name, style = MaterialTheme.typography.titleLarge)
            if (stop.code != "") {
                Text(
                    stringResource(R.string.transit_stop_subtitle, stop.code),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }

    @Composable
    fun Loading() {
        Text(
            stringResource(R.string.transit_departures_loading),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 8.dp)
        )
    }

    @Composable
    fun NoDepartures() {
        Text(
            stringResource(R.string.transit_departures_empty),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 8.dp)
        )
    }

    @Composable
    fun RequestError() {
        Text(
            stringResource(R.string.transit_departures_error),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 8.dp)
        )
    }

    @Composable
    fun DepartureRow(departure: Departure) {
        val zone = TimeZone.of("America/Vancouver")
        val timeTillDeparture = Clock.System.now().periodUntil(departure.time, zone)
        val estimatedDepartureText = if (timeTillDeparture.hours > 0) {
            departure.time.toLocalDateTime(zone).format(LocalDateTime.Format {
                if (DateFormat.is24HourFormat(context)) {
                    hour()
                    char(':')
                    minute()
                } else {
                    amPmHour()
                    char(':')
                    minute()
                    char(' ')
                    amPmMarker("AM", "PM")
                }
            })
        } else if (timeTillDeparture.minutes == 0) {
            stringResource(R.string.transit_departure_now)
        } else {
            stringResource(R.string.transit_departure_minutes, timeTillDeparture.minutes)
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp, 16.dp)
        ) {
            Column {
                Text(departure.route, style = MaterialTheme.typography.headlineSmall)
                Text(departure.destination)
            }
            Spacer(modifier = Modifier.weight(1.0f))
            Text(estimatedDepartureText, Modifier.wrapContentHeight())
        }
        HorizontalSeparator(color = Color(0xFF9E9E9E))
    }

    private suspend fun MapState.queryStopAt(point: Point): Stop? =
        queryRenderedFeatures(
            RenderedQueryGeometry(pixelForCoordinate(point)),
            RenderedQueryOptions(listOf("translink-stops"), null)
        ).value?.let { features ->
            features.firstNotNullOfOrNull {
                val id = it.queriedFeature.feature.properties()?.get("stop_id")?.asInt ?: -1
                val code = it.queriedFeature.feature.properties()?.get("stop_code")?.asString
                val name = it.queriedFeature.feature.properties()?.get("stop_name")?.asString
                if (code != null && name != null) Stop(id = id, code = code, name = name) else null
            }
        }

    companion object {
        enum class SheetValue { Collapsed, PartiallyExpanded, Expanded }
        data class Stop(val id: Int, val code: String, val name: String)
        data class Departure(
            val id: Int,
            val route: String,
            val destination: String,
            val time: Instant
        )
    }
}
