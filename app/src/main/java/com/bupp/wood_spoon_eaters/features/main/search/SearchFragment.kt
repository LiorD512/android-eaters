package com.bupp.wood_spoon_eaters.features.main.search

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.auto_complete_text_watcher.AutoCompleteTextWatcher
import com.bupp.wood_spoon_eaters.custom_views.auto_complete_text_watcher.InputTextWatcher
import com.bupp.wood_spoon_eaters.custom_views.simpler_views.SimpleTextWatcher
import com.bupp.wood_spoon_eaters.databinding.FragmentSearchBinding
import com.bupp.wood_spoon_eaters.features.main.feed.adapters.FeedMainAdapter
import com.bupp.wood_spoon_eaters.model.Campaign
import com.bupp.wood_spoon_eaters.model.RestaurantInitParams
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment(R.layout.fragment_search), FeedMainAdapter.FeedMainAdapterListener {

    val viewModel by viewModel<SearchViewModel>()
    var binding: FragmentSearchBinding? = null
    var searchAdapter: FeedMainAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSearchBinding.bind(view.rootView)

        initUi()
        initObservers()
    }

    private fun initUi() {
        with(binding!!){
            searchAdapter = FeedMainAdapter(this@SearchFragment)
            searchFragList.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = searchAdapter
            }

            binding!!.searchFragInput.addTextChangedListener(object: SimpleTextWatcher(){
                override fun afterTextChanged(s: Editable) {
                    if (s.isEmpty()) {
                        val face = ResourcesCompat.getFont(requireContext(), R.font.lato_reg)
                        binding!!.searchFragInput.typeface = face
                    } else {
                        val face = ResourcesCompat.getFont(requireContext(), R.font.lato_bold)
                        binding!!.searchFragInput.typeface = face
                    }
                    super.afterTextChanged(s)
                }
            })

            binding!!.searchFragInput.addTextChangedListener(object: AutoCompleteTextWatcher(){
                override fun handleInputString(str: String) {
                    viewModel.searchInput(str)
                }
            })
        }


    }

    private fun initObservers() {
        viewModel.searchResultData.observe(viewLifecycleOwner, {
            it.feedData?.let { it1 -> searchAdapter?.setDataList(it1) }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onShareBannerClick(campaign: Campaign) {

    }

    override fun onRestaurantClick(restaurantInitParams: RestaurantInitParams) {
    }

    override fun onChangeAddressClick() {
    }

    override fun onDishSwiped() {
    }

    override fun onRefreshFeedClick() {
    }

    override fun onTagClick(tag: String) {
        binding!!.searchFragInput.setText(tag)
        binding!!.searchFragInput.setSelection(tag.length)
    }


}