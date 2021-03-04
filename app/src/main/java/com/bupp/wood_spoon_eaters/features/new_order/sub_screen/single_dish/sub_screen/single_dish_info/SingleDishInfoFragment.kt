package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_info

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.TimePickerBottomSheet
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import com.bupp.wood_spoon_eaters.custom_views.PlusMinusView
import com.bupp.wood_spoon_eaters.custom_views.UserImageView
import com.bupp.wood_spoon_eaters.dialogs.VideoPlayerDialog
import com.bupp.wood_spoon_eaters.features.main.profile.video_view.VideoViewDialog
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderMainViewModel
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.FullDish
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.bupp.wood_spoon_eaters.views.CartBottomBar
import com.segment.analytics.Analytics
import kotlinx.android.synthetic.main.fragment_single_dish_info.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SingleDishInfoFragment : Fragment(R.layout.fragment_single_dish_info), PlusMinusView.PlusMinusInterface, UserImageView.UserImageViewListener,
    DishMediaAdapter.DishMediaAdapterListener, InputTitleView.InputTitleViewListener {

    private val viewModel by viewModel<SingleDishInfoViewModel>()
    private val mainViewModel by sharedViewModel<NewOrderMainViewModel>()

    private lateinit var dishMediaPagerAdapter: DishMediaAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Analytics.with(requireContext()).screen("dishInfo")

        initUi()
        initObserver()
    }

    private fun initUi() {
        singleDishPlusMinus.setViewEnabled(true)
        singleDishInfoCook.setUserImageViewListener(this)
        singleDishInfoRating.setOnClickListener { onRatingClick() }

        dishMediaPagerAdapter = DishMediaAdapter(this)
        singleDishInfoImagePager.adapter = dishMediaPagerAdapter

        singleDishChangeTimeBtn.setOnClickListener {
            viewModel.onTimeChangeClick()
        }
        singleDishNote.setInputTitleViewListener(this)
    }

    private fun initObserver() {
        viewModel.timeChangeEvent.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let{
                openOrderTimeBottomSheet(it)
            }
        })
        viewModel.availability.observe(viewLifecycleOwner, { availabilityEvent ->
            availabilityEvent.startingTime?.let {
                if (!availabilityEvent.isAvailable) {
                    handleUnAvailableCookingSlot(it)
                }
                if (availabilityEvent.isSoldOut) {
                    handleSoldoutCookingSlot(it)
                }
            }
        })
        mainViewModel.dishInfoEvent.observe(viewLifecycleOwner, {
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

        singleDishInfoImagePager.offscreenPageLimit = fullDish.getMediaList().size
        dishMediaPagerAdapter.submitList(fullDish.getMediaList())
        if (fullDish.getMediaList().size > 1) {
            singleDishInfoCircleIndicator.setViewPager(singleDishInfoImagePager)
        }

        fullDish.cook.country?.let{
            Glide.with(requireContext()).load(fullDish.cook.country.flagUrl).into(singleDishInfoCookFlag)
        }

        val menuItem = fullDish.menuItem
        if (menuItem != null) {
            singleDishQuantityView.initQuantityView(menuItem)
            val quantityLeft = menuItem.getQuantityCount()
            val currentCounter = singleDishCount.text.toString()
            singleDishPlusMinus.setPlusMinusListener(this, initialCounter = currentCounter.toInt(), quantityLeft = quantityLeft, canReachZero = false)
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

    private fun handleUnAvailableCookingSlot(startsAt: Date) {
//        Log.d("wowSingleDish", "handleUnAvailableCookingSlot startsAt: $startsAt")
//        startsAt?.let {
//            viewModel.updateChosenDeliveryDate(newChosenDate = it)
//        }
    }



    private fun handleSoldoutCookingSlot(startsAt: Date) {
//        DishSoldOutDialog().show(childFragmentManager, Constants.UNAVAILABLE_DISH_DIALOG_TAG)
//        singleDishStatusBar.handleBottomBar(showActiveOrders = false)
//        singleDishPlusMinus.setViewEnabled(false)
    }

    override fun onInputTitleChange(str: String?) {
        viewModel.updateCurrentOrderItem(note = str)
    }

    private fun onRatingClick() {
        mainViewModel.getDishReview(null)
    }

    private fun openOrderTimeBottomSheet(menuItems: List<MenuItem>) {
        val timePickerBottomSheet = TimePickerBottomSheet()
        timePickerBottomSheet.setMenuItems(menuItems)
        timePickerBottomSheet.show(childFragmentManager, Constants.TIME_PICKER_BOTTOM_SHEET)
    }

    override fun onPlusMinusChange(counter: Int, position: Int) {
        viewModel.updateCurrentOrderItem(quantity = counter)
        mainViewModel.updateCartBottomBarByType(CartBottomBar.BottomBarTypes.UPDATE_COUNTER, itemCount = counter, price = viewModel.getTotalPriceForDishQuantity(counter))
    }

    private fun initOrderDate(currentDish: FullDish) {
        if(currentDish.isNationwide){
            singleDishInfoDeliveryTimeLayout.visibility = View.GONE
        }else {
            singleDishInfoDeliveryTimeLayout.visibility = View.VISIBLE
            if(currentDish.menuItem?.orderAt == null){
                //Dish is offered today.
                singleDishInfoDate.text = "ASAP, ${currentDish.doorToDoorTime}"
            }else{
                currentDish.menuItem?.orderAt?.let{
                    //Dish is offered in the future.
                    if(DateUtils.isToday(it)){
                        singleDishInfoDate.text = "Today, ${DateUtils.parseDateHalfHourInterval(it)}"
                    }else{
                        singleDishInfoDate.text = "${DateUtils.parseDateToDayDateAndTime(it)}"
                    }
                }

            }
            singleDishInfoDelivery.text = "${viewModel.getDropOffLocation()}"
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

    override fun onDestroyView() {
        singleDishInfoImagePager?.let {
            it.adapter = null
        }
        super.onDestroyView()
    }

    companion object{
        const val TAG = "wowNSingleDishInfoFrag"
    }

}