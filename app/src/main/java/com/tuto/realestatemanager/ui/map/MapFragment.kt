package com.tuto.realestatemanager.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.graphics.scale
import androidx.fragment.app.viewModels
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


@AndroidEntryPoint
class MapFragment : SupportMapFragment(), OnMapReadyCallback {

    private val viewModel by viewModels<MapViewModel>()

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    private val bitmapNotSold by lazy {
        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.property_not_sold)
        val scaledBitmap = originalBitmap.scale(64, 64, false)
        BitmapDescriptorFactory.fromBitmap(scaledBitmap)
    }

    private val bitmapSold by lazy {
        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.icon_sold_text)
        val scaledBitmap = originalBitmap.scale(64, 64, false)
        BitmapDescriptorFactory.fromBitmap(scaledBitmap)
    }

    private val bitmapNotSoldSelected by lazy {
        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.property_not_sold)
        val scaledBitmap = originalBitmap.scale(120, 120, false)
        BitmapDescriptorFactory.fromBitmap(scaledBitmap)
    }

    private val bitmapSoldSelected by lazy {
        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.icon_sold_text)
        val scaledBitmap = originalBitmap.scale(120, 120, false)
        BitmapDescriptorFactory.fromBitmap(scaledBitmap)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getMapAsync(this)

    }

@Deprecated("Deprecated in Java")
override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
        getMapAsync { map ->
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
                map.isMyLocationEnabled = true
                map.uiSettings.isMyLocationButtonEnabled = true
            } else {
                map.uiSettings.isMyLocationButtonEnabled = false
            }
        }
    }
}

    override fun onMapReady(map: GoogleMap) {

        map.uiSettings.isMyLocationButtonEnabled = false

        viewModel.getMapViewState.observe(viewLifecycleOwner) { mapViewState ->

            map.clear()

            val point = LatLng(mapViewState.lat, mapViewState.lng)

            val camera: CameraPosition =
                CameraPosition.Builder()
                    .target(point)
                    .zoom(12.0F)
                    .bearing(0F)
                    .tilt(30F)
                    .build()

            map.animateCamera(CameraUpdateFactory.newCameraPosition(camera))

            map.addCircle(
                CircleOptions()
                    .center(point)
                    .radius(5000.0)
                    .strokeColor(Color.BLUE)
                    .fillColor(0x11000099)
            )

            for (markerPlace in mapViewState.markers) {
                if (markerPlace.lat != null && markerPlace.lng != null) {
                    val marker = map.addMarker(
                        MarkerOptions()
                            .position(LatLng(markerPlace.lat, markerPlace.lng))
                            .title(markerPlace.description)
                            .snippet(markerPlace.address)
                            .icon(
                                when {
                                    markerPlace.id == mapViewState.selectedMarkerId && markerPlace.isSold -> bitmapSoldSelected
                                    markerPlace.id == mapViewState.selectedMarkerId -> bitmapNotSoldSelected
                                    markerPlace.isSold -> bitmapSold
                                    else -> bitmapNotSold
                                }
                            )
                            .anchor(0.5f, 1f)
                    )

                    marker?.tag = markerPlace.id
                }
            }
        }

        map.setOnMarkerClickListener { marker ->
            marker.tag?.toString()?.toLongOrNull()?.let { id ->
                viewModel.setMarkerId(id)
            }
            true
        }

        viewModel.navigateSingleLiveEvent.observe(viewLifecycleOwner) {
            startActivity(Intent(requireContext(), DetailActivity::class.java))
        }

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
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onConfigurationChanged(resources.getBoolean(R.bool.isTablet))
    }


}
