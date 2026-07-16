package com.tuto.realestatemanager.ui.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.FragmentPropertyListBinding
import com.tuto.realestatemanager.ui.createproperty.CreatePropertyActivity
import com.tuto.realestatemanager.ui.detail.DetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PropertyListFragment : Fragment() {

    private var _binding: FragmentPropertyListBinding? = null

    private val binding: FragmentPropertyListBinding
        get() = _binding!!

    private val viewModel by viewModels<PropertyListViewModel>()

    private val createPropertyLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                _binding?.root?.let { root ->
                    Snackbar.make(
                        root,
                        "Property successfully added",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPropertyListBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PropertyListAdapter()

        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager =
            LinearLayoutManager(requireContext())

        val isTablet =
            resources.getBoolean(R.bool.isTablet)

        viewModel.onConfigurationChanged(isTablet)

        binding.createProperty.visibility =
            if (isTablet) {
                View.GONE
            } else {
                View.VISIBLE
            }

        binding.createProperty.setOnClickListener {
            onCreatePropertyClicked()
        }

        observeViewModel(adapter)
    }

    fun onCreatePropertyClicked() {
        viewModel.onDeleteTemporaryPhotoRepository()
        viewModel.onNavigateToCreateActivity()
    }

    private fun observeViewModel(
        adapter: PropertyListAdapter
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                launch {
                    viewModel.viewAction.collect { action ->
                        when (action) {
                            ListViewAction.NavigateToCreateActvity -> {
                                createPropertyLauncher.launch(
                                    Intent(
                                        requireContext(),
                                        CreatePropertyActivity::class.java
                                    )
                                )
                            }

                            ListViewAction.NavigateToDetailActivity -> {
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        DetailActivity::class.java
                                    )
                                )
                            }

                            ListViewAction.ShowNoInternetWarning -> {
                                Toast.makeText(
                                    requireContext(),
                                    "No internet some property may not appear on the map",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                launch {
                    viewModel.propertyListStateFlow.collect { properties ->
                        adapter.submitList(properties)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}