package com.bupp.wood_spoon_eaters.features.main.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.IconsGridView
import com.bupp.wood_spoon_eaters.custom_views.PriceRangeView
import com.bupp.wood_spoon_eaters.custom_views.RatingStarsView
import com.bupp.wood_spoon_eaters.model.SelectableIcon
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.filters_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class FilterFragment : Fragment(), IconsGridView.IconsGridViewListener, PriceRangeView.PriceRangeViewListener,RatingStarsView.RatingStarsViewListener {

    val viewModel by viewModel<PickFiltersViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.filters_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pickFilterFragPriceRangeView.setPriceRangeViewListener(this)
        pickFilterFragTimeIcons.setIconsGridViewListener(this)
        pickFiltersFragRatingStarsView.setRatingStarsViewListener(this)
        pickFilterFragDietaryIcons.setIconsGridViewListener(this)

        pickFilterFragTimeIcons.initIconsGrid(viewModel.getArrivalTimes(), Constants.SINGLE_SELECTION)
        pickFilterFragDietaryIcons.initIconsGrid(viewModel.getDietaryList(), Constants.MULTI_SELECTION)

        pickFilterFragClearAllBtn.setOnClickListener { clearAllFilters() }
        disableClearBtn()
    }

    private fun disableClearBtn(){
        pickFilterFragClearAllBtn.alpha = 0.5f

        pickFilterFragClearAllBtn.isClickable = false

    }


    private fun clearAllFilters() {
        pickFilterFragPriceRangeView.reset()
        pickFilterFragTimeIcons.setSelectedItems(arrayListOf())
        pickFiltersFragRatingStarsView.setRating(0)
        pickFilterFragDietaryIcons.setSelectedItems(arrayListOf())
        disableClearBtn()
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
        if (pickFilterFragPriceRangeView.getSelectedRange() != Constants.PRICE_NOT_SELECTED
            || pickFilterFragTimeIcons.getSelectedItems().isNotEmpty()
            || pickFiltersFragRatingStarsView.getRating() > 0
            || pickFilterFragDietaryIcons.getSelectedItems().isNotEmpty()
        ) {
            pickFilterFragClearAllBtn.alpha = 1f
            pickFilterFragClearAllBtn.isClickable = true
        } else {
            disableClearBtn()
        }
    }

}
