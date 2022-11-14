package com.tuto.realestatemanager.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.FragmentDetailsPropertyBinding
import com.tuto.realestatemanager.databinding.FragmentPropertyListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsPropertyFragment : Fragment() {

    private var _binding : FragmentDetailsPropertyBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailsPropertyBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.textDetail

    }


}