package com.bupp.wood_spoon_eaters.features.main.filter

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.custom_views.IconsGridView
import com.bupp.wood_spoon_eaters.custom_views.PriceRangeView
import com.bupp.wood_spoon_eaters.custom_views.RatingStarsView
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.model.SelectableIcon
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.filters_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class FilterFragment(val listener: FilterFragmentListener) : DialogFragment(), IconsGridView.IconsGridViewListener, PriceRangeView.PriceRangeViewListener,RatingStarsView.RatingStarsViewListener,
    HeaderView.HeaderViewListener {
    var estimatedTimeArrivalId: Int? = null

    interface FilterFragmentListener{
        fun onFilterDone(isFiltered: Boolean)
    }

    val viewModel by viewModel<PickFiltersViewModel>()

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

        filterFragPriceRangeView.setPriceRangeViewListener(this)
        pickFiltersFragRatingStarsView.setRatingStarsViewListener(this)
        filterFragDietaryIcons.setIconsGridViewListener(this)

        filterFragDietaryIcons.initIconsGrid(viewModel.getDietaryList(), Constants.ENDLESS_SELECTION)

        filterFragHeader.setHeaderViewListener(this)
        filterFragClearAllBtn.setOnClickListener { clearAllFilters() }
        disableClearBtn()

        initTimeArrivalUi()

        getCurrentFilterParam()
    }

    private fun initTimeArrivalUi() {
        filterFragArraival20Min.setOnClickListener {
            estimatedTimeArrivalId = 1
            setArrivalTimeSelected(it)
        }
        filterFragArraival60Min.setOnClickListener {
            estimatedTimeArrivalId = 2
            setArrivalTimeSelected(it)
        }
        filterFragArraival120Min.setOnClickListener {
            estimatedTimeArrivalId = 3
            setArrivalTimeSelected(it)
        }
    }

    private fun setArrivalTimeSelected(it: View?) {
        unSelectAll()
        it?.isSelected = true
    }

    private fun unSelectAll() {
        filterFragArraival20Min.isSelected = false
        filterFragArraival60Min.isSelected = false
        filterFragArraival120Min.isSelected = false
    }

    private fun getCurrentFilterParam() {
        viewModel.getCurrentFilterParam()
        viewModel.restoreDetailsEvent.observe(this, Observer { event ->
            if(event != null){
                if(event.hasParmas){
                    if(event.currentDiets != null && event.currentDiets!!.size > 0){
                        filterFragDietaryIcons.setSelectedItems(event.currentDiets)
                    }
                    if(event.minPrice != null){
                        filterFragPriceRangeView.setSelectedRange(event.minPrice!!)
                    }
                }else{
                    clearAllFilters()
                }
            }
        })
    }



    private fun disableClearBtn(){
        filterFragClearAllBtn.alpha = 0.5f

        filterFragClearAllBtn.isClickable = false
    }


    private fun clearAllFilters() {
        unSelectAll()
        filterFragPriceRangeView.reset()
        pickFiltersFragRatingStarsView.setRating(0)
        filterFragDietaryIcons.setSelectedItems(arrayListOf())
        disableClearBtn()
        viewModel.clearSearchParams()
    }

    override fun onIconClick(selected: SelectableIcon) {
        validateFields()
    }

    override fun onPriceRangeClick() {
        validateFields()
    }

    override fun onRatingClick() {
        validateFields()
    }

    private fun validateFields() {
        if (filterFragPriceRangeView.getSelectedRange() != Constants.PRICE_NOT_SELECTED
            || estimatedTimeArrivalId != null
            || pickFiltersFragRatingStarsView.getRating() > 0
            || filterFragDietaryIcons.getSelectedItems().isNotEmpty()
        ) {
            filterFragClearAllBtn.alpha = 1f
            filterFragClearAllBtn.isClickable = true
        } else {
            disableClearBtn()
        }
    }


    override fun onHeaderBackClick() {
        listener.onFilterDone(false)
        dismiss()
    }

    override fun onHeaderDoneClick() {
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
        viewModel.updateSearchParams(price, dietsIds)//, estimatedTimeArrivalId)
        listener.onFilterDone(isFiltered)
        dismiss()
    }

}
