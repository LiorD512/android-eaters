package com.bupp.wood_spoon_eaters.features.main.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.custom_views.IconsGridView
import com.bupp.wood_spoon_eaters.custom_views.PriceRangeView
import com.bupp.wood_spoon_eaters.custom_views.RatingStarsView
import com.bupp.wood_spoon_eaters.model.SelectableIcon
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.FiltersFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class FilterFragment(val listener: FilterFragmentListener) : DialogFragment(), IconsGridView.IconsGridViewListener, PriceRangeView.PriceRangeViewListener,RatingStarsView.RatingStarsViewListener,
    HeaderView.HeaderViewListener {
    private var isAsap: Boolean? = null
    var estimatedTimeArrivalId: Int? = null

    interface FilterFragmentListener{
        fun onFilterDone(isFiltered: Boolean)
    }

    val viewModel by viewModel<PickFiltersViewModel>()
    val binding: FiltersFragmentBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.filters_fragment, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        getCurrentFilterParam()
    }

    private fun initUi() {
        with(binding){
            filterFragPriceRangeView.setPriceRangeViewListener(this@FilterFragment)
            pickFiltersFragRatingStarsView.setRatingStarsViewListener(this@FilterFragment)
            filterFragDietaryIcons.setIconsGridViewListener(this@FilterFragment)

            filterFragDietaryIcons.initIconsGrid(viewModel.getDietaryList(), Constants.ENDLESS_SELECTION)

            filterFragHeader.setHeaderViewListener(this@FilterFragment)
            filterFragClearAllBtn.setOnClickListener { clearAllFilters() }
            disableClearBtn()

            initTimeArrivalUi()

        }
    }

    private fun initTimeArrivalUi() {
        with(binding){
            filterFragArraivalAsap.setOnClickListener {
                isAsap = true
                unSelectAll()
                filterFragArraivalAsap.isSelected = true
                it?.isSelected = true
                validateFields()
            }
            filterFragArraivalLater.setOnClickListener {
                isAsap = false
                unSelectAll()
                filterFragArraivalLater.isSelected = true
                validateFields()
            }
        }

    }

    private fun unSelectAll() {
        binding.filterFragArraivalAsap.isSelected = false
        binding.filterFragArraivalLater.isSelected = false

    }

    private fun getCurrentFilterParam() {
        viewModel.getCurrentFilterParam()
        viewModel.restoreDetailsEvent.observe(this, Observer { event ->
            if(event != null){
                if(event.hasParmas){
                    if(event.currentDiets != null && event.currentDiets!!.size > 0){
                        binding.filterFragDietaryIcons.setSelectedItems(event.currentDiets)
                    }
                    if(event.minPrice != null){
                        binding.filterFragPriceRangeView.setSelectedRange(event.minPrice!!)
                    }
                    if(event.isAsap != null){
                        isAsap = event.isAsap
                        when(isAsap){
                            true -> binding.filterFragArraivalAsap.performClick()
                            false -> binding.filterFragArraivalLater.performClick()

                        }
                    }
                }else{
                    clearAllFilters()
                }
            }
        })
    }



    private fun disableClearBtn(){
        binding.filterFragClearAllBtn.alpha = 0.5f

        binding.filterFragClearAllBtn.isClickable = false
    }


    private fun clearAllFilters() {
        unSelectAll()
        binding.filterFragPriceRangeView.reset()
        binding.pickFiltersFragRatingStarsView.setRating(0)
        binding.filterFragDietaryIcons.setSelectedItems(arrayListOf())
        isAsap = null
        disableClearBtn()
        viewModel.clearSearchParams()
    }

    override fun onIconClick(selected: List<SelectableIcon>) {
        validateFields()
    }

    override fun onPriceRangeClick() {
        validateFields()
    }

    override fun onRatingClick() {
        validateFields()
    }

    private fun validateFields() {
        with(binding){
            if (filterFragPriceRangeView.getSelectedRange() != Constants.PRICE_NOT_SELECTED
                || estimatedTimeArrivalId != null
                || pickFiltersFragRatingStarsView.getRating() > 0
                || filterFragDietaryIcons.getSelectedItems().isNotEmpty()
                || isAsap != null
            ) {
                filterFragClearAllBtn.alpha = 1f
                filterFragClearAllBtn.isClickable = true
            } else {
                disableClearBtn()
            }
        }
    }


    override fun onHeaderBackClick() {
        listener.onFilterDone(false)
        dismiss()
    }

    override fun onHeaderDoneClick() {
        with(binding){
            var isFiltered = false
            var price: Pair<Double?, Double?>? = null
            var dietsIds: ArrayList<Long>? = null
            if (filterFragPriceRangeView.getSelectedRange() != Constants.PRICE_NOT_SELECTED){
                price = filterFragPriceRangeView.getMinMax()
                isFiltered = true
            }
            if(filterFragDietaryIcons.getSelectedItems().isNotEmpty()){
                dietsIds = filterFragDietaryIcons.getSelectedItemsIds()
                isFiltered = true
            }
            viewModel.updateSearchParams(price, dietsIds, isAsap)//, estimatedTimeArrivalId)
            listener.onFilterDone(isFiltered)
            dismiss()
        }
    }

}
