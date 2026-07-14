package com.tuto.realestatemanager.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.graphics.scale
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.ui.detail.DetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapFragment : SupportMapFragment(), OnMapReadyCallback {

    private val viewModel by viewModels<MapViewModel>()

    private var googleMap: GoogleMap? = null

    private var isInitialCameraPositionApplied = false

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fineLocationGranted =
                permissions[
                    Manifest.permission.ACCESS_FINE_LOCATION
                ] ?: false

            val coarseLocationGranted =
                permissions[
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ] ?: false

            googleMap?.let { map ->
                if (
                    fineLocationGranted ||
                    coarseLocationGranted
                ) {
                    if (
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        @SuppressLint("MissingPermission")
                        map.isMyLocationEnabled = true

                        map.uiSettings
                            .isMyLocationButtonEnabled = true
                    }
                } else {
                    map.uiSettings
                        .isMyLocationButtonEnabled = false
                }
            }
        }

    private val bitmapNotSold by lazy {
        val originalBitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.property_not_sold
        )

        val scaledBitmap = originalBitmap.scale(
            64,
            64,
            false
        )

        BitmapDescriptorFactory.fromBitmap(scaledBitmap)
    }

    private val bitmapSold by lazy {
        val originalBitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.icon_sold_text
        )

        val scaledBitmap = originalBitmap.scale(
            64,
            64,
            false
        )

        BitmapDescriptorFactory.fromBitmap(scaledBitmap)
    }

    private val bitmapNotSoldSelected by lazy {
        val originalBitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.property_not_sold
        )

        val scaledBitmap = originalBitmap.scale(
            120,
            120,
            false
        )

        BitmapDescriptorFactory.fromBitmap(scaledBitmap)
    }

    private val bitmapSoldSelected by lazy {
        val originalBitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.icon_sold_text
        )

        val scaledBitmap = originalBitmap.scale(
            120,
            120,
            false
        )

        BitmapDescriptorFactory.fromBitmap(scaledBitmap)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        map.uiSettings.isMyLocationButtonEnabled = false

        configureCameraPersistence(map)
        configureMarkerClick(map)
        configureLocationPermission(map)
        observeViewModel(map)
    }

    private fun configureCameraPersistence(map: GoogleMap) {
        map.setOnCameraIdleListener {
            if (isInitialCameraPositionApplied) {
                viewModel.saveCameraPosition(
                    map.cameraPosition
                )
            }
        }
    }

    private fun observeViewModel(map: GoogleMap) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                launch {
                    viewModel.viewAction.collect { action ->
                        when (action) {
                            MapViewAction.NavigateToDetailActivity -> {
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        DetailActivity::class.java
                                    )
                                )
                            }
                        }
                    }
                }

                launch {
                    viewModel.mapViewState.collect { mapViewState ->
                        mapViewState?.let { state ->
                            displayMapState(
                                map = map,
                                mapViewState = state
                            )
                        }
                    }
                }
            }
        }
    }

    private fun displayMapState(
        map: GoogleMap,
        mapViewState: MapViewState
    ) {
        map.clear()

        val userPosition = LatLng(
            mapViewState.lat,
            mapViewState.lng
        )

        if (!isInitialCameraPositionApplied) {
            isInitialCameraPositionApplied = true

            val savedCameraPosition =
                viewModel.cameraPosition.value

            if (savedCameraPosition != null) {
                map.moveCamera(
                    CameraUpdateFactory.newCameraPosition(
                        savedCameraPosition
                    )
                )
            } else {
                val initialCameraPosition =
                    CameraPosition.Builder()
                        .target(userPosition)
                        .zoom(12.0F)
                        .bearing(0F)
                        .tilt(30F)
                        .build()

                map.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                        initialCameraPosition
                    )
                )
            }
        }

        map.addCircle(
            CircleOptions()
                .center(userPosition)
                .radius(5000.0)
                .strokeColor(Color.BLUE)
                .fillColor(0x11000099)
        )

        for (markerPlace in mapViewState.markers) {
            val markerLat = markerPlace.lat
            val markerLng = markerPlace.lng

            if (markerLat != null && markerLng != null) {
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                markerLat,
                                markerLng
                            )
                        )
                        .title(markerPlace.description)
                        .snippet(markerPlace.address)
                        .icon(
                            when {
                                markerPlace.id ==
                                        mapViewState.selectedMarkerId &&
                                        markerPlace.isSold -> {
                                    bitmapSoldSelected
                                }

                                markerPlace.id ==
                                        mapViewState.selectedMarkerId -> {
                                    bitmapNotSoldSelected
                                }

                                markerPlace.isSold -> {
                                    bitmapSold
                                }

                                else -> {
                                    bitmapNotSold
                                }
                            }
                        )
                        .anchor(0.5f, 1f)
                )

                marker?.tag = markerPlace.id
            }
        }
    }

    private fun configureMarkerClick(map: GoogleMap) {
        map.setOnMarkerClickListener { marker ->
            marker.tag
                ?.toString()
                ?.toLongOrNull()
                ?.let(viewModel::setMarkerId)

            true
        }
    }

    private fun configureLocationPermission(map: GoogleMap) {
        if (
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            @SuppressLint("MissingPermission")
            map.isMyLocationEnabled = true

            map.uiSettings.isMyLocationButtonEnabled = true
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.onConfigurationChanged(
            resources.getBoolean(R.bool.isTablet)
        )
    }
}