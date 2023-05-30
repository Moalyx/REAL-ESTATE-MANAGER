package com.tuto.realestatemanager.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.*
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tuto.realestatemanager.ui.detail.DetailsPropertyFragment
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
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        }


        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
        //map.mapType = GoogleMap.MAP_TYPE_SATELLITE

        viewModel.getMapViewState.observe(this) { mapViewState ->
            val point = LatLng(mapViewState.lat, mapViewState.lng)
            map.animateCamera(CameraUpdateFactory.zoomIn())
            val camera: CameraPosition =
                CameraPosition.Builder().target(point).zoom(12.0F).bearing(0F).tilt(30F).build()
            map.animateCamera(CameraUpdateFactory.newCameraPosition(camera))


            for (markerPlace in mapViewState.marker) {
//                    val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver , Uri.parse(
//                        property.photos[0].photoUri))
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
//                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                )
                if (marker != null) {
                    marker.tag = markerPlace.id
                }
            }

            map.setOnMarkerClickListener { it ->
                startActivity(
                    DetailsPropertyFragment.navigate(
                        requireContext(),
                        it.tag.toString().toLong()
                    )
                )
//                    viewModel.navigateSingleLiveEvent.observe(this) { //todo verifier pourquoi cela ne fonctionne pas
//                        when (it) {
//                            MapViewAction.NavigateToDetailActivity -> startActivity(
//                                Intent(
//                                    requireContext(),
//                                    DetailActivity::class.java
//                                )
//                            )
//                        }
//                    }
//                    startActivity(Intent(this.requireContext(), DetailActivity::class.java))
                return@setOnMarkerClickListener true
            }

        }
    }
}
