package com.tuto.realestatemanager.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuto.realestatemanager.R
import com.tuto.realestatemanager.databinding.FragmentPropertyListBinding
import com.tuto.realestatemanager.ui.createproperty.CreatePropertyActivity
import com.tuto.realestatemanager.ui.mortgagecalcultator.MortgageCalculatorActivity
import com.tuto.realestatemanager.ui.search.SearchPropertyActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PropertyListFragment : Fragment() {

    private var isConversionToDollars = false
    private var _binding: FragmentPropertyListBinding? = null
    private val binding: FragmentPropertyListBinding get() = _binding!!

    private var menu: Menu? = null

    private val viewModel by viewModels<PropertyListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPropertyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        requireActivity().addMenuProvider(
//            object : MenuProvider {
//
//                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                    menu.clear()
//                    menuInflater.inflate(R.menu.edit_property_menu, menu)
////                    this@PropertyListFragment.menu = menu
////                    viewModel.iconStatus.observe(viewLifecycleOwner){
////                        if (it) {
////                            menu.findItem(R.id.currency).setIcon(R.drawable.dollar)
////                        } else {
////                            menu.findItem(R.id.currency).setIcon(R.drawable.euro)
////                        }
////
////                    }
//
//
//                    updateMenuIcon(menu.findItem(R.id.currency))
//                }
//
//
//                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//                    when (menuItem.itemId) {
//
//                        R.id.bank_loan -> startActivity(
//                            Intent(
//                                requireContext(),
//                                MortgageCalculatorActivity::class.java
//                            )
//                        )
//                        R.id.search_property -> startActivity(
//                            Intent(
//                                requireContext(),
//                                SearchPropertyActivity::class.java
//                            )
//                        )
//                        R.id.currency -> {
//                            updateMenuIcon(menuItem)
//                            viewModel.converterPrice()
//                        }
//
//                    }
//                    return true
//                }
//            }
//        )

        val recyclerView: RecyclerView = binding.recyclerview
        val adapter = PropertyListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.propertyListLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.createProperty.setOnClickListener {
            viewModel.onDeleteTemporaryPhotoRepository()
            startActivity(Intent(context, CreatePropertyActivity::class.java))
        }

//        viewModel.iconStatus.observe(viewLifecycleOwner) { isConversionToDollars ->
//            // Utilisez menu.findItem pour obtenir l'élément de menu correspondant à l'icône
//            val currencyMenuItem = menu?.findItem(R.id.currency)
//            if (isConversionToDollars) {
//                currencyMenuItem?.setIcon(R.drawable.dollar)
//            } else {
//                currencyMenuItem?.setIcon(R.drawable.euro)
//            }
//        }

    }

//    private fun updateMenuIcon(item: MenuItem) {
//        if (view != null) {  //todo si je ne met pas ça j'ai une erreur Can't access the Fragment View's LifecycleOwner when getView() is null i.e., before onCreateView() or after onDestroyView()
//            viewModel.iconStatus.observe(viewLifecycleOwner) {
//                if (it) {
//                    item.setIcon(R.drawable.dollar)
//                } else {
//                    item.setIcon(R.drawable.euro)
//                }
//            }
//        }
//    }


//    private fun updateMenuIcon(item: MenuItem) {
//        if (isConversionToDollars) {
//            item.setIcon(R.drawable.dollar)
//        } else {
//            item.setIcon(R.drawable.euro)
//        }
//        isConversionToDollars = !isConversionToDollars
//
//    }


}