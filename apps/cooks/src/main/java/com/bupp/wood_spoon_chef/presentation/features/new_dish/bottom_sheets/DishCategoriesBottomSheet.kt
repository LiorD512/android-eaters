package com.bupp.wood_spoon_chef.presentation.features.new_dish.bottom_sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.DishCategoriesBottomSheetBinding
import com.bupp.wood_spoon_chef.data.remote.model.DishCategory
import com.bupp.wood_spoon_chef.common.FloatingBottomSheet

class DishCategoriesBottomSheet(
    private val categories: List<DishCategory>,
    val listener: DishCategoriesBottomSheetListener
) : FloatingBottomSheet(), DishCategoryAdapter.DishCategoryAdapterListener {

    private var binding: DishCategoriesBottomSheetBinding? = null
    val adapter = DishCategoryAdapter(this)

    interface DishCategoriesBottomSheetListener {
        fun onCategorySelected(category: DishCategory?)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dish_categories_bottom_sheet, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DishCategoriesBottomSheetBinding.bind(view)
        initUi()
    }


    private fun initUi() {
        with(binding!!) {
            dishCategoriesList.adapter = adapter
            adapter.submitList(categories)
            dishCategoriesSelectBtn.setOnClickListener {
                listener.onCategorySelected(adapter.selectedCategory)
                dismiss()
            }
        }
    }

    override fun onCategorySelected(dishCategory: DishCategory?, oldItemPosition: Int?) {
        oldItemPosition?.let {
            adapter.notifyItemChanged(oldItemPosition)
        }
    }

    override fun clearClassVariables() {
        binding = null
    }
}