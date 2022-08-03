package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.cooking_slot_menu

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
import com.bupp.wood_spoon_chef.databinding.BottomSheetFilterMenuBinding
import com.bupp.wood_spoon_chef.presentation.custom_views.HeaderView
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.FilterAdapterSectionModel
import com.bupp.wood_spoon_chef.utils.extensions.show
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FilterMenuBottomSheet(
    private val selectedSections: List<String>?
) : TopCorneredBottomSheet(), HeaderView.HeaderViewListener,
    FilterMenuAdapter.FilterMenuAdapterListener {

    private var binding: BottomSheetFilterMenuBinding? = null
    private val viewModel by viewModel<FilterMenuViewModel>()
    private lateinit var filterMenuAdapter: FilterMenuAdapter

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
        setSelectedSections()
        observeViewModelState()
        observeViewModelEvents()
    }

    private fun initUi() {
        binding?.apply {
            filterMenuBsHeaderView.setHeaderViewListener(this@FilterMenuBottomSheet)
            filterMenuBsClearAll.setOnClickListener { viewModel.onClearAllClick() }
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

    private fun observeViewModelEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.events.collect { event ->
                        when (event) {
                            is FilterMenuEvent.ApplyClicked -> onApplyClick(
                                event.selectedSections
                            )
                        }
                    }
                }
            }
        }
    }

    private fun onApplyClick(selectedSections: List<FilterAdapterSectionModel>?) {
        setFragmentResult(
            SELECTED_SECTIONS_KEY, bundleOf(
                SELECTED_SECTIONS_VALUE to selectedSections
            )
        )
        dismiss()
    }

    private fun updateInputsWithState(state: FilterMenuState) {
        binding.apply {
            updateList(state.sectionList)
            showClearAll(state.showClearAll)
        }
    }

    private fun setupList() {
        binding?.apply {
            filterMenuBsCategoriesRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                filterMenuAdapter = FilterMenuAdapter(this@FilterMenuBottomSheet)
                adapter = filterMenuAdapter
            }
        }
    }

    private fun updateList(data: List<FilterAdapterSectionModel>?) {
        filterMenuAdapter.submitList(data)
    }

    override fun clearClassVariables() {
        binding = null
    }

    override fun onHeaderBackClick() {
        dismiss()
    }

    override fun onSectionSelected(sectionName: String, isSelected: Boolean) {
        viewModel.onSectionSelected(sectionName, isSelected)
    }

    private fun setSelectedSections() {
        viewModel.setSelectedSections(selectedSections)
    }

    private fun showClearAll(show: Boolean) {
        binding?.apply {
            filterMenuBsClearAll.show(show)
        }
    }

    companion object {
        const val SELECTED_SECTIONS_KEY = "selectedSectionsKey"
        const val SELECTED_SECTIONS_VALUE = "selectedSectionsValue"

        fun show(
            fragment: Fragment,
            selectedSections: List<String>?,
            listener: ((List<FilterAdapterSectionModel>) -> Unit)
        ) {
            FilterMenuBottomSheet(selectedSections).show(
                fragment.childFragmentManager,
                FilterMenuBottomSheet::class.simpleName
            )
            fragment.setDishCategoryResultListener(listener)
        }
    }
}

private fun Fragment.setDishCategoryResultListener(listener: ((List<FilterAdapterSectionModel>) -> Unit)) {
    childFragmentManager.setFragmentResultListener(
        FilterMenuBottomSheet.SELECTED_SECTIONS_KEY,
        this
    ) { _, bundle ->
        val result =
            bundle.get(FilterMenuBottomSheet.SELECTED_SECTIONS_VALUE) as List<FilterAdapterSectionModel>
        listener.invoke(result)
    }
}