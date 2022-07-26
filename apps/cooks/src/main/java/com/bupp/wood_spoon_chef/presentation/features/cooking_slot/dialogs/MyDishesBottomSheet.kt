package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.databinding.BottomSheetMyDishesBinding
import com.bupp.wood_spoon_chef.presentation.custom_views.HeaderView
import com.bupp.wood_spoon_chef.presentation.custom_views.SimpleTextWatcher
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.FilterAdapterSectionModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.MyDishesPickerAdapterModel
import com.bupp.wood_spoon_chef.utils.extensions.show
import com.bupp.wood_spoon_chef.utils.extensions.showErrorToast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.ibrahimsn.lib.util.clear
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyDishesBottomSheet(private val selectedDishesIds: List<Long>) : TopCorneredBottomSheet(),
    HeaderView.HeaderViewListener, MyDishesSectionAdapter.MyDishesSectionAdapterListener {

    private var binding: BottomSheetMyDishesBinding? = null
    private val viewModel by viewModel<MyDishesViewModel>()
    private lateinit var myDishesSectionAdapter: MyDishesSectionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_my_dishes, container, false)
        binding = BottomSheetMyDishesBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDialogAdjustPan()
        setFullScreenDialog()
        setSelectedDishes()
        initUi()
        setupList()
        observeViewModelState()
        observeViewModelEvents()
    }

    override fun clearClassVariables() {
        binding = null
    }

    private fun initUi() {
        binding?.apply {
            myDishesBsHeaderView.setHeaderViewListener(this@MyDishesBottomSheet)
            myDishesBsSearchInput.addTextChangedListener(object : SimpleTextWatcher() {
                override fun afterTextChanged(s: Editable) {
                    super.afterTextChanged(s)
                    viewModel.filterList(s.toString())
                    setSearchImageResource(s.toString())
                }
            })
            myDishesBsFilterView.setOnClickListener {
                viewModel.onFilterClick()
            }
            mydDishesBsAddBtn.setOnClickListener {
                viewModel.onAddClick()
            }

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
                viewModel.events.collect { event ->
                    when (event) {
                        is MyDishesEvent.ShowFilterMenu -> openFilterMenuBottomSheet(event.selectedSections)
                        is MyDishesEvent.ShowEmptyState -> showEmptyResultState(event.show)
                        is MyDishesEvent.Error -> {
                            binding?.apply {
                                showErrorToast(
                                    event.message ?: getString(R.string.generic_error),
                                    myDishesBsMainLayout, Toast.LENGTH_SHORT
                                )
                            }
                        }
                        is MyDishesEvent.AddDishes -> {
                            setFragmentResult(SELECTED_DISHES_KEY, bundleOf(SELECTED_DISHES_VALUE to event.selectedDishes))
                            dismiss()
                        }
                    }
                }
            }
        }
    }

    private fun setSelectedDishes(){
     viewModel.setSelectedDishesIds(selectedDishesIds)
    }

    private fun setupList() {
        binding?.apply {
            myDishesBsDishesRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                myDishesSectionAdapter = MyDishesSectionAdapter(this@MyDishesBottomSheet)
                adapter = myDishesSectionAdapter
                itemAnimator = object: DefaultItemAnimator() {
                    override fun animateChange(
                        oldHolder: RecyclerView.ViewHolder,
                        newHolder: RecyclerView.ViewHolder,
                        preInfo: ItemHolderInfo,
                        postInfo: ItemHolderInfo
                    ): Boolean {
                        if(preInfo.top == postInfo.top) {
                            dispatchChangeFinished(oldHolder, true)
                            dispatchChangeFinished(newHolder, true)
                            return true
                        }
                        return super.animateChange(oldHolder, newHolder, preInfo, postInfo)
                    }
                }
            }
        }
    }

    private fun updateList(data: List<MyDishesPickerAdapterModel>) {
        myDishesSectionAdapter.submitSections(data)
    }

    private fun showEmptyResultState(show: Boolean) {
        binding?.apply {
            myDishesBsNoResultState.show(show)
        }
    }

    private fun updateInputsWithState(state: MyDishesState) {
        binding.apply {
            state.sectionedList?.let {
                updateList(it)
            }
            setFilterImageResource(state.isListFiltered)
        }
    }

    private fun openFilterMenuBottomSheet(selectedSections: List<String>?) {
        FilterMenuBottomSheet.show(this, selectedSections) {
            viewModel.setIsListFiltered(it.isNotEmpty())
            viewModel.filterBySectionName(it)
            viewModel.updateSelectedSections(it.map { section -> section.sectionName})
        }
    }

    private fun setFilterImageResource(isListFiltered: Boolean?) {
        binding?.apply {
            if (isListFiltered == false) {
                myDishBsFilterImg.setImageResource(R.drawable.ic_filter_normal)
            } else {
                myDishBsFilterImg.setImageResource(R.drawable.ic_filter_on)
            }
        }
    }

    private fun setSearchImageResource(searchInput: String?) {
        binding?.apply {
            if (searchInput.isNullOrBlank()) {
                myDishesBsSearchImg.setImageResource(R.drawable.ic_search)
                myDishesBsSearchImg.setOnClickListener(null)
            } else {
                myDishesBsSearchImg.setImageResource(R.drawable.ic_icons_close_search)
                myDishesBsSearchImg.setOnClickListener { myDishesBsSearchInput.clear() }
            }
        }
    }


    override fun onDishSelected(isChecked: Boolean, dishId: Long) {
        viewModel.onDishSelected(isChecked, dishId)
    }

    override fun onHeaderBackClick() {
        dismiss()
    }

    companion object {
        const val SELECTED_DISHES_KEY = "selectedDishesKey"
        const val SELECTED_DISHES_VALUE = "selectedDishesValue"

        fun show(
            fragment: Fragment,
            selectedDishesIds: List<Long>,
            listener: ((List<Long>) -> Unit)
        ) {
            MyDishesBottomSheet(selectedDishesIds).show(
                fragment.childFragmentManager,
                MyDishesBottomSheet::class.simpleName
            )
            fragment.setSelectedDishesResultListener(listener)
        }
    }
}

private fun Fragment.setSelectedDishesResultListener(listener: ((List<Long>) -> Unit)) {
    childFragmentManager.setFragmentResultListener(
        MyDishesBottomSheet.SELECTED_DISHES_KEY,
        this
    ) { _, bundle ->
        val result = bundle.get(MyDishesBottomSheet.SELECTED_DISHES_VALUE) as List<Long>
        listener.invoke(result)
    }
}