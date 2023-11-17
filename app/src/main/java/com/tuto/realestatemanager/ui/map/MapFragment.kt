package com.tuto.realestatemanager.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getMapAsync(this)
        initGoogleMaps()
    }

    private fun initGoogleMaps() {
        val mapFragment: MapFragment = MapFragment.newInstance()
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        }

        map.uiSettings.isMyLocationButtonEnabled = true
        //map.mapType = GoogleMap.MAP_TYPE_SATELLITE


        viewModel.getMapViewState.observe(this) { mapViewState ->
            val point = LatLng(mapViewState.lat, mapViewState.lng)
            map.animateCamera(CameraUpdateFactory.zoomIn())
            val camera: CameraPosition =
                CameraPosition.Builder().target(point).zoom(12.0F).bearing(0F).tilt(30F).build()
            map.animateCamera(CameraUpdateFactory.newCameraPosition(camera))

            map.addCircle(
                CircleOptions()
                    .center(point)
                    .radius(5000.0)
                    .strokeColor(Color.BLUE)
                    .fillColor(0x110000BB)
            )

            for (markerPlace in mapViewState.markers) {
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                markerPlace.lat,
                                markerPlace.lng
                            )
                        )
                        .title(markerPlace.description)
                        .snippet(markerPlace.address)

                )
                if (marker != null) {
                    marker.tag = markerPlace.id
                }
            }
        }

        map.setOnMarkerClickListener { marker ->
            viewModel.setMarkerId(marker.tag.toString().toLong())
            //viewModel.onNavigationToDetailActivity()
            return@setOnMarkerClickListener true
        }

        viewModel.navigateSingleLiveEvent.observe(viewLifecycleOwner) {
            startActivity(Intent(requireContext(), DetailActivity::class.java))
            //DetailActivity.navigate(requireContext())
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.onConfigurationChanged(resources.getBoolean(R.bool.isTablet))
    }


}
