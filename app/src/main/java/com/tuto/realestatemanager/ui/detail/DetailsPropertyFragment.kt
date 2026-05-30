package com.tuto.realestatemanager.ui.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.bumptech.glide.Glide
import com.tuto.realestatemanager.BuildConfig
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.FragmentDetailsPropertyBinding
import com.tuto.realestatemanager.ui.editproperty.EditPropertyActivity
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

        (requireActivity() as MenuHost).addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val adapter = PropertyDetailPhotoAdapter(
            object : OnPhotoClickListener {
                override fun onPhotoClick(photoUri: String) {
                    viewmodel.setUri(photoUri)
                }
            }
        )
        binding.mediaRecyclerview.adapter = adapter

        viewmodel.detailPropertyLiveData.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.contentFrame.visibility = View.GONE
                binding.placeholderImage.visibility = View.VISIBLE
                propertyId = 0
                return@observe
            }

            binding.contentFrame.visibility = View.VISIBLE
            binding.placeholderImage.visibility = View.GONE
            
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
                binding.status.setTextColor(
                    ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark)
                )
                binding.soldDate.text = it.saleDate
            } else {
                binding.status.text = "SOLD"
                binding.status.setTextColor(
                    ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
                )
                binding.soldDate.text = LocalDate.now().toString()
            }

            Glide
                .with(binding.mainPhoto)
                .load(it.photoUri)
                .centerInside()
                .into(binding.mainPhoto)

            viewmodel.isVisible(binding.poiAirport, it.poiAirport)
            viewmodel.isVisible(binding.poiBus, it.poiBus)
            viewmodel.isVisible(binding.poiPark, it.poiPark)
            viewmodel.isVisible(binding.poiSchool, it.poiSchool)
            viewmodel.isVisible(binding.poiResto, it.poiResto)
            viewmodel.isVisible(binding.poiTrain, it.poiTrain)

            adapter.submitList(it.photoList)

            val zoom = 15
            val size = "1200x1200"
            val apiKey = BuildConfig.GOOGLE_PLACES_KEY
            val address = "${it.address} ${it.city} ${it.zipcode} ${it.state} ${it.country}"

            val staticMap =
                "https://maps.googleapis.com/maps/api/staticmap" +
                        "?center=$address" +
                        "&zoom=$zoom" +
                        "&size=$size" +
                        "&markers=color:red%7C$address" +
                        "&key=$apiKey"

            if (it.hasInternet) {
                Glide.with(requireContext())
                    .load(staticMap)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
                    .error(R.drawable.staticmap_unvailabe)
                    .into(binding.staticMap)
            } else {
                Glide.with(requireContext()).clear(binding.staticMap)
                binding.staticMap.setImageResource(R.drawable.staticmap_unvailabe)
            }

        }

        viewmodel.navigateSingleLiveEvent.observe(viewLifecycleOwner) {
            when (it) {
                DetailViewAction.NavigateToEditActivity -> startActivity(
                    EditPropertyActivity.navigate(
                        requireContext(),
                        propertyId
                    )
                )
                else -> {}
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        val isInDetailContainer = view?.parent?.let {
            val parentId = (it as? View)?.id
            parentId == R.id.main_container_detail || parentId == R.id.detail_container
        } ?: false

        if (activity is DetailActivity || isInDetailContainer) {
            menuInflater.inflate(R.menu.fragment_detail_menu, menu)
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.edit_property -> {
                viewmodel.onNavigateToEditActivity()
                true
            }
            else -> false
        }
    }

}
