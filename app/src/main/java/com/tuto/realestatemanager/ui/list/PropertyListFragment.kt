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

        setHasOptionsMenu(true)

        requireActivity().addMenuProvider( //todo tester le menuprovider pour probleme de duplication0
            object : MenuProvider {

                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menu.clear()
                    menuInflater.inflate(R.menu.edit_property_menu, menu)
                }


                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
//                        R.id.edit_property -> startActivity(
//                            Intent(
//                                context,
//                                CreatePropertyActivity::class.java
//                            )
//                        )
                        R.id.bank_loan -> startActivity(
                            Intent(
                                requireContext(),
                                MortgageCalculatorActivity::class.java
                            )
                        )
                        R.id.search_property -> startActivity(
                            Intent(
                                requireContext(),
                                SearchPropertyActivity::class.java
                            )
                        )
                        R.id.currency -> {
                            updateMenuIcon(menuItem)
                            viewModel.converterPrice()
                        }

                    }
                    return true
                }

//                override fun onOptionsItemSelected(item: MenuItem): Boolean {
//                    when (item.itemId) {
//                        R.id.bank_loan -> startActivity(
//                            Intent(
//                                this,
//                                MortgageCalculatorActivity::class.java
//                            )
//                        )
//                        R.id.search_property -> startActivity(
//                            Intent(
//                                this,
//                                SearchPropertyActivity::class.java
//                            )
//                        )
//                        R.id.currency -> {
//                            updateMenuIcon(item)
//                            viewmodel.converterPrice()
//                        }
//                    }
//                    return true
//                }

            }

        )

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

    }

    private fun updateMenuIcon(item: MenuItem) {
        if (isConversionToDollars) {
            item.setIcon(R.drawable.dollar)
        } else {
            item.setIcon(R.drawable.euro)
        }
        isConversionToDollars = !isConversionToDollars
    }


}