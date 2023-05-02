package com.tuto.realestatemanager.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.tuto.realestatemanager.data.repository.property.PropertyRepository
import com.tuto.realestatemanager.databinding.FragmentDetailsPropertyBinding
import com.tuto.realestatemanager.ui.list.PropertyListViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MapFragment(
)  : SupportMapFragment(), OnMapReadyCallback {

    private val viewModel by viewModels<MapViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getMapAsync(this)
        initGoogleMaps()
    }

    private fun initGoogleMaps() {
        val mapFragment: MapFragment = MapFragment.newInstance()
        mapFragment.getMapAsync(this);
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
            //map.isMyLocationEnabled = true
            //propertyRepository.getAllPropertiesWithPhotosEntity()

//            val point = LatLng(48.866667,2.333333)
//            map.animateCamera(CameraUpdateFactory.zoomIn())
//            val camera : CameraPosition = CameraPosition.Builder().target(point).zoom(14.0F).bearing(0F).tilt(30F).build()
//            map.animateCamera(CameraUpdateFactory.newCameraPosition(camera))
//
//            map.addMarker(MarkerOptions().position(point))

            viewModel.mapViewStateList.observe(viewLifecycleOwner){

                for (property in it){
                   val marker = map.addMarker(MarkerOptions()
                       .position(LatLng(property.propertyEntity.lat, property.propertyEntity.lng))
                       .title(property.propertyEntity.description)
                       .snippet(property.propertyEntity.address)
                   )
                    if(marker != null){
                        marker.tag = property.propertyEntity.id
                    }

                }
            }
        }
    }
}