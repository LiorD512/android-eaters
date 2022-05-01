package com.bupp.wood_spoon_chef.presentation.features.main.single_dish

import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.presentation.custom_views.HeaderView
import com.bupp.wood_spoon_chef.databinding.SingleDishDialogBinding
import com.bupp.wood_spoon_chef.presentation.dialogs.VideoViewDialog
import com.bupp.wood_spoon_chef.presentation.features.base.BaseDialogFragment
import com.bupp.wood_spoon_chef.data.remote.model.Dish
import com.bupp.wood_spoon_chef.utils.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DecimalFormat


class SingleDishDialog : BaseDialogFragment(R.layout.single_dish_dialog),
    HeaderView.HeaderViewListener, DishMediaAdapter.DishMediaAdapterListener {

    private var curDishId: Long? = null
    private var binding: SingleDishDialogBinding? = null
    val viewModel: SingleDishViewModel by viewModel()

    companion object {
        private const val SINGLE_DISH_PARAM = "singleDishParam"

        fun newInstance(dishId: Long): SingleDishDialog {
            val fragment = SingleDishDialog()
            val args = Bundle()
            args.putLong(SINGLE_DISH_PARAM, dishId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            curDishId = requireArguments().getLong(SINGLE_DISH_PARAM)
        }
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = SingleDishDialogBinding.bind(view)

        initUi()
        initObservers()
        curDishId?.let { fetchDish(it) }
    }

    private fun initObservers() {
        viewModel.progressData.observe(viewLifecycleOwner, {
            handleProgressBar(it)
        })
        viewModel.getDishEvent.observe(viewLifecycleOwner, { dish ->
            updateUi(dish)
        })
    }

    private fun initUi() {
        binding?.apply {
            singleDishHeaderView.setHeaderViewListener(this@SingleDishDialog)
        }
    }

    private fun fetchDish(dishId: Long) {
        binding?.apply {
            handleProgressBar(false)
            viewModel.getDish(dishId)
        }
    }

    private fun updateUi(dish: Dish) {
        binding?.apply {
            singleDishHeaderView.setType(Constants.HEADER_VIEW_TYPE_TITLE_BACK, dish.name)

            val mediaAdapter = DishMediaAdapter(this@SingleDishDialog)
            singleDishTopMediaList.layoutManager =
                LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            singleDishTopMediaList.adapter = mediaAdapter

            mediaAdapter.setItem(dish.getMediaList())

            if (dish.getMediaList().size > 1) {
                val pagerSnapHelper = PagerSnapHelper()
                pagerSnapHelper.attachToRecyclerView(singleDishTopMediaList)
                singleDishCircleIndicator.attachToRecyclerView(
                    singleDishTopMediaList,
                    pagerSnapHelper
                )
            }

            singleDishFragNameDetails.setTitle(dish.name)
            singleDishFragNameDetails.setBody(dish.description)

            dish.prepTimeRangeId?.let {
                singleDishFragPreparationTime.setBody(it.getRangeStr())
            }
            dish.ingredients?.let {
                singleDishFragIngredients.setBody(it)
            }
            dish.instruction?.let {
                singleDishFragInstructions.setBody(it)
                singleDishFragInstructions.visibility = View.VISIBLE
            }
            dish.accommodations?.let {
                singleDishFragAccommodations.setBody(it)
                singleDishFragAccommodations.visibility = View.VISIBLE
            }
            dish.portionSize?.let {
                singleDishFragPortion.setBody(it)
            }
            dish.diets?.let {
                singleDishFragDiets.initHorizontalDietaryViewShow(it)
                singleDishFragDietsLayout.visibility = View.VISIBLE
            }
            dish.cuisines?.let {
                singleDishFragCuisine.setBody(dish.getCuisineAsString())
            }
            dish.price?.let {
                val clearPrice = Utils.parsePriceStringToNumber(it.formattedValue)

                val defaultFeePercentage = viewModel.getDefaultServiceFee()

                val feePrice = clearPrice * (defaultFeePercentage / 100)
                val totalProfit = (clearPrice - feePrice)

                val df = DecimalFormat("#.##")

                singleDishFragPriceDish.text = it.formattedValue
                singleDishFragPriceServiceFee.text = "$${df.format(feePrice)}"
                singleDishFragPriceTotal.text = "$${df.format(totalProfit)}"
            }
        }
    }

    override fun onPlayClick(url: String) {
        val uri = Uri.parse(url)
        VideoViewDialog(uri.toString()).show(childFragmentManager, Constants.VIDEO_PLAYER_DIALOG)
    }


    override fun onHeaderBackClick() {
        dismiss()
    }

    override fun clearClassVariables() {
        binding = null
    }


}