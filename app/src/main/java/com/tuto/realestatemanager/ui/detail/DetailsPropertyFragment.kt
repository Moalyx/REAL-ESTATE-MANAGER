package com.tuto.realestatemanager.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.FragmentDetailsPropertyBinding
import com.tuto.realestatemanager.ui.createupdateproperty.CreatePropertyActivity
import com.tuto.realestatemanager.ui.list.PropertyListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsPropertyFragment : Fragment(), MenuProvider {

    private var _binding: FragmentDetailsPropertyBinding? = null
    private val binding get() = _binding!!

    private val viewmodel by viewModels<DetailPropertyViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailsPropertyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       // setHasOptionsMenu(true)

        (requireActivity() as MenuHost).addMenuProvider(this )


        viewmodel.detailPropertyLiveData.observe(viewLifecycleOwner) { it ->
            binding.type.text = it.type
            binding.surface?.text = it.surface.toString()
            binding.price.text = it.price.toString()
            binding.description.text = it.description
            binding.numberRoom.text = it.room.toString()
            binding.numberBathroom.text = it.bathroom.toString()
            binding.numberBedroom.text = it.bedroom.toString()
            binding.country.text = it.county
            if (it.poiBus) {
                binding.poiBus.isVisible = true
            }
            if (it.poiTrain) {
                binding.poiTrain.isVisible = true
            }
            if (it.poiSchool) {
                binding.poiSchool.isVisible = true
            }
            if (it.poiAirport) {
                binding.poiAirport.isVisible = true
            }
            if (it.poiPark) {
                binding.poiPark.isVisible = true
            }
            if (it.poiResto) {
                binding.poiResto.isVisible = true
            }


            val recyclerView: RecyclerView = binding.mediaRecyclerview
            val adapter = PropertyDetailPhotoAdapter()
            //recyclerView.layoutManager = GridLayoutManager(context,2)
            recyclerView.adapter = adapter
            viewmodel.detailPropertyLiveData.observe(viewLifecycleOwner) {
                adapter.submitList(it.photoList)


//            Glide.with(requireContext())
//                .load(it.photo)
//                .into(binding.propertyPhotoDetail)

//            binding.propertyPhotoDetail?.let { it1 ->
//                Glide.with(requireContext())
//                    .load(it.photo)
//                    .into(it1)
            }

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
            R.id.edit_property -> startActivity(CreatePropertyActivity.navigate(requireContext(), 1))
        }
        return true
    }


}