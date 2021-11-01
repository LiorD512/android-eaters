package com.bupp.wood_spoon_eaters.features.main.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.auto_complete_text_watcher.AutoCompleteTextWatcher
import com.bupp.wood_spoon_eaters.custom_views.auto_complete_text_watcher.InputTextWatcher
import com.bupp.wood_spoon_eaters.custom_views.simpler_views.SimpleTextWatcher
import com.bupp.wood_spoon_eaters.databinding.FragmentSearchBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment(R.layout.fragment_search), SearchAdapter.OrdersHistoryAdapterListener {

    val viewModel by viewModel<SearchViewModel>()
    var binding: FragmentSearchBinding? = null
    var searchAdapter: SearchAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSearchBinding.bind(view.rootView)

        initUi()
        initObservers()
    }

    private fun initUi() {
        with(binding!!){
            searchAdapter = SearchAdapter(requireContext(), this@SearchFragment)
            searchFragList.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = searchAdapter
            }

            binding!!.searchFragInput.addTextChangedListener(object: AutoCompleteTextWatcher(){
                override fun handleInputString(str: String) {
                    viewModel.searchInput(str)
                }
            })
        }


    }

    private fun initObservers() {
        viewModel.searchLiveData.observe(viewLifecycleOwner, {
            searchAdapter?.submitList(it)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onTagClick(tag: String) {
        binding!!.searchFragInput.setText(tag)
    }


}