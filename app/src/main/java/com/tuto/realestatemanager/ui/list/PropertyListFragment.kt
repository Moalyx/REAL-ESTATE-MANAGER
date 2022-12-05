package com.tuto.realestatemanager.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuto.realestatemanager.databinding.FragmentPropertyListBinding
import com.tuto.realestatemanager.ui.detail.DetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PropertyListFragment : Fragment(), MenuProvider {

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
        val adapter = this.context?.let { PropertyListAdapter(it) }
        //recyclerView.layoutManager = GridLayoutManager(context,2)
        recyclerView.adapter = adapter
        viewModel.propertyListLiveData.observe(viewLifecycleOwner) {
            adapter?.submitList(it)
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {



    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

        return false
    }
}