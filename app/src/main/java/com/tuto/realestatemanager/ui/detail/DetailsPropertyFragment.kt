package com.tuto.realestatemanager.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tuto.realestatemanager.databinding.FragmentDetailsPropertyBinding
import com.tuto.realestatemanager.ui.list.PropertyListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsPropertyFragment : Fragment() {

    private var _binding : FragmentDetailsPropertyBinding? = null
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


        viewmodel.detailPropertyLiveData.observe(viewLifecycleOwner) { it ->
            binding.type.text = it.type
            binding.surface?.text = it.surface.toString()
            binding.price.text = it.price.toString()

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


}