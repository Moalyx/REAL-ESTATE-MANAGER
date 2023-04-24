package com.tuto.realestatemanager.ui.detail

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.*
import com.tuto.realestatemanager.BuildConfig
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.FragmentDetailsPropertyBinding
import com.tuto.realestatemanager.ui.editproperty.EditPropertyActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsPropertyFragment : Fragment(), OnMapReadyCallback, MenuProvider {

    private var _binding: FragmentDetailsPropertyBinding? = null
    private val binding get() = _binding!!
    private var propertyId: Long = 0

    private val viewmodel by viewModels<DetailPropertyViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDetailsPropertyBinding.inflate(inflater, container, false)
        initGoogleMaps()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setHasOptionsMenu(true)

        (requireActivity() as MenuHost).addMenuProvider(this)



        viewmodel.detailPropertyLiveData.observe(viewLifecycleOwner) { it ->
            propertyId = it.id
            binding.type.text = it.type
            binding.surface.text = it.surface.toString()
            binding.price.text = it.price.toString()
            binding.description.text = it.description
            binding.numberRoom.text = it.room.toString()
            binding.numberBathroom.text = it.bathroom.toString()
            binding.numberBedroom.text = it.bedroom.toString()
            binding.address.text = it.address
            binding.city.text = it.city
            binding.zipcode.text = it.zipcode.toString()
            binding.state.text = it.state
            binding.country.text = it.country
            viewmodel.isVisible(binding.poiAirport, it.poiAirport)
            viewmodel.isVisible(binding.poiBus, it.poiBus)
            viewmodel.isVisible(binding.poiPark, it.poiPark)
            viewmodel.isVisible(binding.poiSchool, it.poiSchool)
            viewmodel.isVisible(binding.poiResto, it.poiResto)
            viewmodel.isVisible(binding.poiTrain, it.poiTrain)


            val recyclerView: RecyclerView = binding.mediaRecyclerview
            val adapter = PropertyDetailPhotoAdapter()
            recyclerView.adapter = adapter
            viewmodel.detailPropertyLiveData.observe(viewLifecycleOwner) {
                adapter.submitList(it.photoList)
            }

            val zoom = 15
            val size = "1200x1200"
            val apiKey = BuildConfig.GOOGLE_PLACES_KEY

            val staticMap =
                "https://maps.googleapis.com/maps/api/staticmap?center=${it.address} ${it.city} ${it.zipcode} ${it.state} ${it.country}&markers=${it.address}&zoom=$zoom&size=$size&key=$apiKey"

            Glide.with(requireContext())
                .load(staticMap)
                .into(binding.staticMap)

        }


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
            map.isMyLocationEnabled = true
        }
    }


//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.edit_property_menu, menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.edit_property-> {
//                startActivity(CreatePropertyActivity.navigate(requireContext(), 1))
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_property_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.edit_property -> startActivity(
                EditPropertyActivity.navigate(
                    requireContext(),
                    propertyId
                )
            )
        }
        return true
    }

}