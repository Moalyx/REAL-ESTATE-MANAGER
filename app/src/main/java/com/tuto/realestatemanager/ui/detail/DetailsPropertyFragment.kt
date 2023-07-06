package com.tuto.realestatemanager.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tuto.realestatemanager.BuildConfig
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.FragmentDetailsPropertyBinding
import com.tuto.realestatemanager.ui.editproperty.EditPropertyActivity
import com.tuto.realestatemanager.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class DetailsPropertyFragment : Fragment(), MenuProvider {

    private var _binding: FragmentDetailsPropertyBinding? = null
    private val binding get() = _binding!!
    private var propertyId: Long = 0

    private val viewmodel by viewModels<DetailPropertyViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentDetailsPropertyBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setHasOptionsMenu(true)

//        requireActivity().addMenuProvider(
//            object : MenuProvider {
//
//                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                    menuInflater.inflate(R.menu.fragment_detail_menu, menu)
//                }
//
//
//                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//                    when (menuItem.itemId) {
//                        R.id.edit_property -> startActivity(
//                            Intent(
//                                context,
//                                CreatePropertyActivity::class.java
//                            )
//                        )
//                    }
//                    return true
//                }
//            }
//
//        )

        (requireActivity() as MenuHost).addMenuProvider(this)

        viewmodel.detailPropertyLiveData.observe(viewLifecycleOwner) { it ->
            propertyId = it.id
            binding.type.text = it.type
            binding.surface.text = it.surface.toString()
            binding.price.text = it.price
            binding.description.text = it.description
            binding.numberRoom.text = it.room.toString()
            binding.numberBathroom.text = it.bathroom.toString()
            binding.numberBedroom.text = it.bedroom.toString()
            binding.address.text = it.address
            binding.city.text = it.city
            binding.zipcode.text = it.zipcode.toString()
            binding.state.text = it.state
            binding.country.text = it.country
            binding.onSaleDate.text = it.saleSince
            binding.agent.text = it.agent
            if (!it.isSold) {
                binding.status.text = "Available for sale"
                binding.soldDate.text = it.saleDate
            } else {
                binding.status.text = "SOLD"
                binding.soldDate.text = LocalDate.now().toString()
            }
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
            val address = "${it.address} ${it.city} ${it.zipcode} ${it.state} ${it.country}"

            val staticMap =
                "https://maps.googleapis.com/maps/api/staticmap?center=$address&markers=${it.address}&zoom=$zoom&size=$size&key=$apiKey"

            Glide.with(requireContext())
                .load(staticMap)
                .into(binding.staticMap)

        }



        viewmodel.navigateSingleLiveEvent.observe(viewLifecycleOwner) {
            when (it) {
                DetailViewAction.NavigateToEditActivity -> startActivity(
                    EditPropertyActivity.navigate(
                        requireContext(),
                        propertyId
                    )
                )
                //requireActivity().finish()
//                DetailViewAction.NavigateToMainActivity -> startActivity(
//                    Intent(
//                        requireContext(),
//                        MainActivity::class.java
//                    )
//                )
                else -> {}
            }
        }

        //viewmodel.onNavigatetoMainActivity()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.fragment_detail_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.edit_property -> viewmodel.onNavigateToEditActivity()
        }
        return true
    }

}