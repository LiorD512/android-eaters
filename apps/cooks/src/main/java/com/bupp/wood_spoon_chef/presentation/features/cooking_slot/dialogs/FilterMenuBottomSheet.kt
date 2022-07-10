package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.data.remote.model.DishCategory
import com.bupp.wood_spoon_chef.databinding.BottomSheetFilterMenuBinding
import com.bupp.wood_spoon_chef.presentation.custom_views.HeaderView
import com.bupp.wood_spoon_chef.utils.extensions.show
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FilterMenuBottomSheet : TopCorneredBottomSheet(), HeaderView.HeaderViewListener,
    DishCategoryAdapter.DishCategoryListener {

    private var binding: BottomSheetFilterMenuBinding? = null
    private val viewModel by viewModel<FilterMenuViewModel>()
    private lateinit var dishCategoryAdapter: DishCategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_filter_menu, container, false)
        binding = BottomSheetFilterMenuBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFullScreenDialog()
        initUi()
        setupList()
        observeViewModelState()
        observeViewModelEvents()
    }

    private fun initUi() {
        binding?.apply {
            filterMenuBsHeaderView.setHeaderViewListener(this@FilterMenuBottomSheet)
            filterMenuBsClearAll.setOnClickListener { dishCategoryAdapter.clearSelection() }
            filterMenuBsApplyBtn.setOnClickListener { viewModel.onApplyClick() }
        }
    }

    private fun observeViewModelState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        updateInputsWithState(state)
                    }
                }
            }
        }
    }

    private fun observeViewModelEvents(){
        viewLifecycleOwner.lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    viewModel.events.collect{event ->
                        when(event){
                            is FilterMenuEvent.SelectedCategory -> showClearAll()
                            is FilterMenuEvent.PassSelectedCategory -> onApplyClick(
                                event.dishCategory
                            )
                        }
                    }
                }
            }
        }
    }

    private fun onApplyClick(selectedCategory: DishCategory?){
        setFragmentResult(DISH_CATEGORY_KEY, bundleOf(DISH_CATEGORY_VALUE to selectedCategory))
        dismiss()
    }

    private fun updateInputsWithState(state: FilterMenuState) {
        binding.apply {
            updateList(state.dishCategories)
        }
    }

    private fun setupList() {
        binding?.apply {
            filterMenuBsCategoriesRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                dishCategoryAdapter = DishCategoryAdapter(this@FilterMenuBottomSheet)
                adapter = dishCategoryAdapter
            }
        }
    }

    private fun updateList(data: List<DishCategory>?) {
        dishCategoryAdapter.submitList(data)
    }

    override fun clearClassVariables() {
        binding = null
    }

    override fun onHeaderBackClick() {
        dismiss()
    }

    override fun onCategorySelected(dishCategory: DishCategory?, oldItemPosition: Int?) {
        oldItemPosition?.let {
            dishCategoryAdapter.notifyItemChanged(oldItemPosition)
        }
        viewModel.onCategorySelectedClick(dishCategory)
    }

    private fun showClearAll(){
        binding?.apply {
            filterMenuBsClearAll.show(true)
        }
    }

    companion object {
        const val DISH_CATEGORY_KEY = "dishCategoryKey"
        const val DISH_CATEGORY_VALUE = "dishCategoryValue"

        fun show(fragment: Fragment, listener: ((DishCategory?) -> Unit)) {
            FilterMenuBottomSheet().show(
                fragment.childFragmentManager,
                FilterMenuBottomSheet::class.simpleName
            )
            fragment.setDishCategoryResultListener(listener)
        }
    }
}

private fun Fragment.setDishCategoryResultListener(listener: ((DishCategory?) -> Unit)) {
    childFragmentManager.setFragmentResultListener(
        FilterMenuBottomSheet.DISH_CATEGORY_KEY,
        this
    ) { _, bundle ->
        val result = bundle.getParcelable<DishCategory>(FilterMenuBottomSheet.DISH_CATEGORY_VALUE)
        listener.invoke(result)
    }
}