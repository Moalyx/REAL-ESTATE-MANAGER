package com.tuto.realestatemanager.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.tuto.realestatemanager.databinding.FragmentPropertyListBinding
import com.tuto.realestatemanager.ui.createproperty.CreatePropertyActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PropertyListFragment : Fragment()/*, MenuProvider*/ {

    private var _binding: FragmentPropertyListBinding? = null
    private val binding: FragmentPropertyListBinding get() = _binding!!

    private val viewModel by viewModels<PropertyListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPropertyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = binding.recyclerview
        val adapter = PropertyListAdapter()
        //recyclerView.layoutManager = GridLayoutManager(context,2)
        recyclerView.adapter = adapter
//        viewModel.propertyListLiveData.observe(viewLifecycleOwner) {
//            adapter?.submitList(it)
//        }
        viewModel.getPropertiesLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.createProperty.setOnClickListener {
            startActivity(Intent(context, CreatePropertyActivity::class.java))
        }

    }

//    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
////        menuInflater.inflate(R.menu.edit_property_menu, menu)
//    }
//
//    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//        when (menuItem.itemId) {
//            R.id.edit_property -> startActivity(
//                Intent(
//                    context,
//                    CreatePropertyActivity::class.java
//                )
//            )
//        }
//        return true
//    }


}