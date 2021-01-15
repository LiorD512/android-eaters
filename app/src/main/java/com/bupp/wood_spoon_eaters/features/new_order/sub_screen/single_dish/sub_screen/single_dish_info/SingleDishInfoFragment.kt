package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_info

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.PlusMinusView
import com.bupp.wood_spoon_eaters.custom_views.UserImageView
import com.bupp.wood_spoon_eaters.dialogs.VideoPlayerDialog
import com.bupp.wood_spoon_eaters.dialogs.rating_dialog.RatingsDialog
import com.bupp.wood_spoon_eaters.features.main.profile.video_view.VideoViewDialog
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderMainViewModel
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.FullDish
import com.bupp.wood_spoon_eaters.utils.DateUtils
import kotlinx.android.synthetic.main.fragment_single_dish_info.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SingleDishInfoFragment : Fragment(R.layout.fragment_single_dish_info), PlusMinusView.PlusMinusInterface, UserImageView.UserImageViewListener,
    DishMediaAdapter.DishMediaAdapterListener {

    private val viewModel by viewModel<SingleDishInfoViewModel>()
    private val mainViewModel by sharedViewModel<NewOrderMainViewModel>()

    private lateinit var dishMediaPagerAdapter: DishMediaAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initObserver()
    }

    private fun initUi() {
        singleDishPlusMinus.setViewEnabled(true)
        singleDishInfoCook.setUserImageViewListener(this)
        singleDishInfoRating.setOnClickListener { onRatingClick() }

        dishMediaPagerAdapter = DishMediaAdapter(this)
        singleDishInfoImagePager.adapter = dishMediaPagerAdapter

        singleDishChangeTimeBtn.setOnClickListener { openOrderTimeDialog() }
    }

    private fun initObserver() {
        mainViewModel.dishInfoEvent.observe(viewLifecycleOwner, Observer{
            updateDishInfoUi(it)
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateDishInfoUi(fullDish: FullDish) {
        singleDishInfoCook.setUser(fullDish.cook)
        singleDishInfoFavorite.setIsFav(fullDish.isFavorite)
        singleDishInfoFavorite.setDishId(fullDish.id)
        singleDishInfoName.text = fullDish.name
        singleDishInfoCookName.text = "By ${fullDish.cook.getFullName()}"
        singleDishInfoDescription.text = fullDish.description
        singleDishInfoPrice.text = fullDish.getPriceObj().formatedValue


        dishMediaPagerAdapter.setItem(fullDish.getMediaList())
        if (fullDish.getMediaList().size > 1) {
            singleDishInfoCircleIndicator.setViewPager(singleDishInfoImagePager)
        }

        fullDish.cook.country?.let{
            Glide.with(requireContext()).load(fullDish.cook.country.flagUrl).into(singleDishInfoCookFlag)
        }

//        if (fullDish.cook?.diets?.size > 0) {
//            singleDishInfoDietryList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//            val dietaryAdapter = CooksDietaryAdapter(requireContext(), fullDish.cook.diets)
//            singleDishInfoDietryList.adapter = dietaryAdapter
//        } else {
//            singleDishInfoDietryList.visibility = View.GONE
//        }


        val menuItem = fullDish.menuItem
        if (menuItem != null) {
            singleDishQuantityView.initQuantityView(menuItem)
            val quantityLeft = menuItem.quantity - menuItem.unitsSold
            val currentCounter = singleDishCount.text.toString()
            singleDishPlusMinus.setPlusMinusListener(this, initialCounter = currentCounter.toInt(), quantityLeft = quantityLeft)
        }

        initOrderDate(fullDish)
        singleDishInfoRatingVal.text = fullDish.rating.toString()

        if (fullDish.cooksInstructions != null && fullDish.cooksInstructions.isNotEmpty()) {
            singleDishInstructionsLayout.visibility = View.VISIBLE
            singleDishInstructionsBody.text = fullDish.cooksInstructions
        } else {
            singleDishInstructionsLayout.visibility = View.GONE
        }

        val isNationwide = fullDish.menuItem?.cookingSlot?.isNationwide
        if(isNationwide != null && isNationwide){
            singleDishInfoDeliveryTimeLayout.visibility = View.GONE
        }
        else{
            singleDishInfoDeliveryTimeLayout.visibility = View.VISIBLE
        }
    }

    private fun onRatingClick() {
        mainViewModel.getCooksReview()
    }

    private fun openOrderTimeDialog() {
//        viewModel.getCurrentDish()?.let {
//            val currentDateSelected = it.menuItem
//            val availableMenuItems = it.availableMenuItems
//            OrderDateChooserDialog(currentDateSelected, availableMenuItems, this)
//                .show(childFragmentManager, Constants.ORDER_DATE_CHOOSER_DIALOG_TAG)
//        }
    }

    override fun onPlusMinusChange(counter: Int, position: Int) {
        //updating ui only
//        viewModel.getCurrentDish().let {
//            val newValue = it.price.value * counter
//            updateStatusBottomBar(price = newValue, itemCount = counter)
//        }
//        singleDishCount.text = "$counter"
    }

    private fun initOrderDate(currentDish: FullDish) {
        if(currentDish.isNationwide){
            singleDishInfoDeliveryTimeLayout.visibility = View.GONE
        }else {
            singleDishInfoDeliveryTimeLayout.visibility = View.VISIBLE

//            if(newSelectedDate != null){
//                singleDishInfoDate.text = DateUtils.parseDateToDayDateHour(newSelectedDate)
//            }else{
                val orderAtDate = currentDish.menuItem?.orderAt
                if (orderAtDate != null) {
                    currentDish.menuItem?.cookingSlot?.orderFrom?.let {
                        singleDishInfoDate.text = DateUtils.parseDateToDayDateHour(it)
                    }
                } else if (currentDish.doorToDoorTime != null) {
                    singleDishInfoDate.text = "ASAP, ${currentDish.doorToDoorTime}"
                }
//            }

            singleDishInfoDelivery.text = "${viewModel.getDropoffLocation()}"
        }
    }

    override fun onUserImageClick(cook: Cook?) {
        cook?.let{
            VideoViewDialog(cook).show(childFragmentManager, Constants.VIDEO_VIEW_DIALOG)
        }
    }

    override fun onPlayClick(url: String) {
        //media adapter interface
        val uri = Uri.parse(url)
        VideoPlayerDialog(uri).show(childFragmentManager, Constants.VIDEO_PLAYER_DIALOG)
    }


}