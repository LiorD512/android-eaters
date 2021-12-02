package com.bupp.wood_spoon_eaters.features.main.search

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.custom_views.auto_complete_text_watcher.AutoCompleteTextWatcher
import com.bupp.wood_spoon_eaters.custom_views.auto_complete_text_watcher.InputTextWatcher
import com.bupp.wood_spoon_eaters.custom_views.simpler_views.SimpleTextWatcher
import com.bupp.wood_spoon_eaters.databinding.FragmentSearchBinding
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.features.main.feed.adapters.FeedMainAdapter
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.Campaign
import com.bupp.wood_spoon_eaters.model.RestaurantInitParams
import com.bupp.wood_spoon_eaters.utils.AnimationUtil
import com.bupp.wood_spoon_eaters.utils.Utils
import com.bupp.wood_spoon_eaters.utils.closeKeyboard
import com.bupp.wood_spoon_eaters.views.abs.RecyclerHorizontalIndicatorDecoration
import com.monkeytech.brenda.common.item_decorators.SpacesItemDecoration
import kotlinx.coroutines.launch
import me.ibrahimsn.lib.util.clear
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment(R.layout.fragment_search), FeedMainAdapter.FeedMainAdapterListener {

    private val mainViewModel by sharedViewModel<MainViewModel>()
    val viewModel by viewModel<SearchViewModel>()
    var binding: FragmentSearchBinding? = null
    var searchAdapter: FeedMainAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSearchBinding.bind(view)

        initUi()
        initObservers()

    }

    private fun initUi() {
        with(binding!!){
            searchAdapter = FeedMainAdapter(this@SearchFragment)
            searchFragList.apply {
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(SpacesItemDecoration(Utils.toPx(14)))
                adapter = searchAdapter
            }

            searchFragInput.addTextChangedListener(object: SimpleTextWatcher(){
                override fun afterTextChanged(s: Editable) {
                    if (s.isEmpty()) {
                        val face = ResourcesCompat.getFont(requireContext(), R.font.lato_reg)
                        binding!!.searchFragInput.typeface = face
                        AnimationUtil().alphaOut(binding!!.searchFragClearInput)
                    } else {
                        val face = ResourcesCompat.getFont(requireContext(), R.font.lato_bold)
                        binding!!.searchFragInput.typeface = face
                        AnimationUtil().alphaIn(binding!!.searchFragClearInput)
                    }
                    super.afterTextChanged(s)
                }
            })

            searchFragInput.addTextChangedListener(object: AutoCompleteTextWatcher(){
                override fun handleInputString(str: String) {
                    if(str.isNotEmpty()){
                        if(str.startsWith(" ")){
                            handleInputString(str.substring(1))
                        }else{
                            viewModel.searchInput(str)
                        }
                    }else{
                        viewModel.postDefaultData()
                    }
                }
            })

            searchFragClearInput.setOnClickListener {
                searchFragInput.clear()
            }

            searchFragInput.setOnFocusChangeListener { view, b ->
                if(b){
                    viewModel.logEvent(Constants.EVENT_SEARCH_QUERY_CLICK)
                }
            }

            searchFragInput.setOnEditorActionListener { _, actionId, _ ->
                when(actionId){
                    IME_ACTION_DONE -> {
                        closeKeyboard()
                        return@setOnEditorActionListener true
                    }
                    else -> {
                        return@setOnEditorActionListener false
                    }
                }
            }
        }
    }

    private fun initObservers() {
        viewModel.searchResultData.observe(viewLifecycleOwner, {
            it.feedData?.let { it1 -> searchAdapter?.setDataList(it1) }
        })
        mainViewModel.refreshSearchData.observe(viewLifecycleOwner, {
            viewModel.getSearchTags()
            viewModel.getRecentOrders()
        })
//        viewModel.getFinalAddressParams().observe(viewLifecycleOwner, {
//            viewModel.getSearchTags()
//            viewModel.getRecentOrders()
//        })
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onRestaurantClick(restaurantInitParams: RestaurantInitParams) {
        val query = binding!!.searchFragInput.text.toString()
        restaurantInitParams.query = query
        mainViewModel.startRestaurantActivity(restaurantInitParams)
        viewModel.logRestaurantClick(restaurantInitParams)
    }

    override fun onTagClick(tag: String) {
        binding!!.searchFragInput.setText(tag)
        binding!!.searchFragInput.setSelection(tag.length)
        viewModel.logTagEvent(Constants.EVENT_SEARCH_TAG_CLICK, tag)
    }

    override fun onShareBannerClick(campaign: Campaign) {
        //do nothing
    }

    override fun onChangeAddressClick() {
        //do nothing
    }

    override fun onDishSwiped() {
        //do nothing
    }

    override fun onRefreshFeedClick() {
        val input = binding!!.searchFragInput.text.toString()
        if(input.isNullOrBlank()){
            viewModel.postDefaultData()
        }else{
            viewModel.searchInput(input)
        }
    }

    override fun onResume() {
        super.onResume()
//        viewModel.showDefaultSearchData()

    }



}