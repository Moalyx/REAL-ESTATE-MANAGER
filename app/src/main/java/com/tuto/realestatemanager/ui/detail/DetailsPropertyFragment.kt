package com.tuto.realestatemanager.ui.detail

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.tuto.realestatemanager.BuildConfig
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.FragmentDetailsPropertyBinding
import com.tuto.realestatemanager.ui.editproperty.EditPropertyActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

@AndroidEntryPoint
class DetailsPropertyFragment : Fragment(), MenuProvider {

    private var _binding: FragmentDetailsPropertyBinding? = null
    private val binding: FragmentDetailsPropertyBinding
        get() = _binding!!

    private var propertyId: Long = 0L

    private val viewModel by viewModels<DetailPropertyViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsPropertyBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MenuHost).addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.STARTED
        )

        val adapter = PropertyDetailPhotoAdapter(
            object : OnPhotoClickListener {
                override fun onPhotoClick(photoUri: String) {
                    viewModel.setUri(photoUri)
                }
            }
        )

        binding.mediaRecyclerview.adapter = adapter

        observeViewModel(adapter)
    }

    private fun observeViewModel(
        adapter: PropertyDetailPhotoAdapter
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                launch {
                    viewModel.viewAction.collect { action ->
                        when (action) {
                            DetailViewAction.NavigateToEditActivity -> {
                                startActivity(
                                    EditPropertyActivity.navigate(
                                        requireContext(),
                                        propertyId
                                    )
                                )
                            }

                            DetailViewAction.NavigateToMainActivity -> Unit
                        }
                    }
                }

                launch {
                    viewModel.detailPropertyStateFlow.collect { state ->
                        if (state == null) {
                            displayEmptyState()
                        } else {
                            displayProperty(
                                state = state,
                                adapter = adapter
                            )
                        }
                    }
                }
            }
        }
    }

    private fun displayEmptyState() {
        binding.contentFrame.visibility = View.GONE
        binding.placeholderImage.visibility = View.VISIBLE
        propertyId = 0L
    }

    @SuppressLint("SetTextI18n")
    private fun displayProperty(
        state: PropertyDetailViewState,
        adapter: PropertyDetailPhotoAdapter
    ) {
        binding.contentFrame.visibility = View.VISIBLE
        binding.placeholderImage.visibility = View.GONE

        propertyId = state.id
        binding.type.text = state.type
        binding.surface.text = state.surface.toString()
        binding.price.text = state.price
        binding.description.text = state.description
        binding.numberRoom.text = state.room.toString()
        binding.numberBathroom.text = state.bathroom.toString()
        binding.numberBedroom.text = state.bedroom.toString()
        binding.address.text = state.address
        binding.city.text = state.city
        binding.zipcode.text = state.zipcode.toString()
        binding.state.text = state.state
        binding.country.text = state.country
        binding.onSaleDate.text = state.saleSince
        binding.agent.text = state.agent

        if (state.isSold) {
            binding.status.text = "SOLD"
            binding.status.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.holo_red_dark
                )
            )
        } else {
            binding.status.text = "Available for sale"
            binding.status.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.holo_green_dark
                )
            )
        }

        binding.soldDate.text = state.saleDate

        Glide.with(binding.mainPhoto)
            .load(state.photoUri)
            .centerInside()
            .into(binding.mainPhoto)

        binding.poiAirport.isVisible = state.poiAirport
        binding.poiBus.isVisible = state.poiBus
        binding.poiPark.isVisible = state.poiPark
        binding.poiSchool.isVisible = state.poiSchool
        binding.poiResto.isVisible = state.poiResto
        binding.poiTrain.isVisible = state.poiTrain

        adapter.submitList(state.photoList)

        displayStaticMap(state)
    }

    private fun displayStaticMap(state: PropertyDetailViewState) {
        val zoom = 15
        val size = "1200x1200"
        val apiKey = BuildConfig.GOOGLE_PLACES_KEY

        val address =
            "${state.address} ${state.city} ${state.zipcode} " +
                    "${state.state} ${state.country}"

        val encodedAddress = Uri.encode(address)

        val staticMap =
            "https://maps.googleapis.com/maps/api/staticmap" +
                    "?center=$encodedAddress" +
                    "&zoom=$zoom" +
                    "&size=$size" +
                    "&markers=color:red%7C$encodedAddress" +
                    "&key=$apiKey"

        val mapCacheKey = address
            .lowercase(Locale.ROOT)
            .replace("[^a-z0-9]".toRegex(), "_")

        val localStaticMapFile = File(
            requireContext().filesDir,
            "static_map_${state.id}_$mapCacheKey.png"
        )

        when {
            localStaticMapFile.exists() -> {
                Glide.with(requireContext())
                    .load(localStaticMapFile)
                    .into(binding.staticMap)
            }

            state.hasInternet -> {
                downloadAndCacheStaticMap(
                    staticMapUrl = staticMap,
                    localStaticMapFile = localStaticMapFile
                )
            }

            else -> {
                binding.staticMap.setImageResource(
                    R.drawable.staticmap_unvailabe
                )
            }
        }
    }

    private fun downloadAndCacheStaticMap(
        staticMapUrl: String,
        localStaticMapFile: File
    ) {
        Glide.with(requireContext())
            .asBitmap()
            .load(staticMapUrl)
            .into(
                object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        _binding?.staticMap?.setImageBitmap(resource)

                        FileOutputStream(localStaticMapFile).use { outputStream ->
                            resource.compress(
                                Bitmap.CompressFormat.PNG,
                                100,
                                outputStream
                            )
                        }
                    }

                    override fun onLoadCleared(
                        placeholder: Drawable?
                    ) = Unit
                }
            )
    }

    override fun onCreateMenu(
        menu: Menu,
        menuInflater: MenuInflater
    ) {
        val isInDetailContainer = view?.parent?.let { parent ->
            val parentId = (parent as? View)?.id

            parentId == R.id.main_container_detail ||
                    parentId == R.id.detail_container
        } ?: false

        if (activity is DetailActivity || isInDetailContainer) {
            menuInflater.inflate(
                R.menu.fragment_detail_menu,
                menu
            )
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.edit_property -> {
                if (propertyId != 0L) {
                    viewModel.onNavigateToEditActivity()
                }

                true
            }

            else -> false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}