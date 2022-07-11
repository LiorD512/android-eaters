package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.data.remote.model.Dish
import com.bupp.wood_spoon_chef.databinding.BottomSheetMyDishesBinding
import com.bupp.wood_spoon_chef.presentation.custom_views.HeaderView
import com.bupp.wood_spoon_chef.presentation.custom_views.SimpleTextWatcher
import com.bupp.wood_spoon_chef.presentation.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_chef.utils.extensions.show
import com.bupp.wood_spoon_chef.utils.extensions.showErrorToast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.ibrahimsn.lib.util.clear
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyDishesBottomSheet() : TopCorneredBottomSheet(), HeaderView.HeaderViewListener {

    private var binding: BottomSheetMyDishesBinding? = null
    private val viewModel by viewModel<MyDishesViewModel>()
    private lateinit var myDishesAdapter: MyDishesAdapter

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
                        MyDishesEvent.ShowFilterMenu -> openFilterMenuBottomSheet()
                        is MyDishesEvent.ShowEmptyState -> showEmptyResultState(event.show)
                        is MyDishesEvent.Error -> {
                            binding?.apply {
                                showErrorToast(
                                    event.message ?: getString(R.string.generic_error),
                                    myDishesBsMainLayout, Toast.LENGTH_SHORT
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupList() {
        binding?.apply {
            myDishesBsDishesRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                myDishesAdapter = MyDishesAdapter()
                adapter = myDishesAdapter
                val dividerItemDecoration = DividerItemDecorator(
                    ContextCompat.getDrawable(requireContext(), R.drawable.divider_white_three)
                )
                addItemDecoration(dividerItemDecoration)
            }
        }
    }

    private fun updateList(data: List<Dish>?) {
        myDishesAdapter.submitList(data)
    }

    private fun showEmptyResultState(show: Boolean) {
        binding?.apply {
            myDishesBsNoResultState.show(show)
        }
    }

    private fun updateInputsWithState(state: MyDishesState) {
        binding.apply {
            updateList(state.dishes)
            setFilterImageResource(state.isListFiltered)
        }
    }

    private fun openFilterMenuBottomSheet() {
        FilterMenuBottomSheet.show(this) {
            viewModel.setIsListFiltered(it != null)
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

    override fun onHeaderBackClick() {
        dismiss()
    }
}