package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_info

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.TimePickerBottomSheet
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import com.bupp.wood_spoon_eaters.custom_views.PlusMinusView
import com.bupp.wood_spoon_eaters.databinding.FragmentSingleDishInfoBinding
import com.bupp.wood_spoon_eaters.views.UserImageVideoView
import com.bupp.wood_spoon_eaters.dialogs.VideoPlayerDialog
import com.bupp.wood_spoon_eaters.features.main.profile.video_view.VideoViewDialog
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderMainViewModel
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.FullDish
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.bupp.wood_spoon_eaters.views.CartBottomBar
import com.bupp.wood_spoon_eaters.views.WSCounterEditText
import com.segment.analytics.Analytics
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SingleDishInfoFragment : Fragment(R.layout.fragment_single_dish_info), PlusMinusView.PlusMinusInterface, UserImageVideoView.UserImageViewListener,
    DishMediaAdapter.DishMediaAdapterListener, InputTitleView.InputTitleViewListener, TimePickerBottomSheet.TimePickerListener,
    WSCounterEditText.WSCounterListener {

    val binding: FragmentSingleDishInfoBinding by viewBinding()
    private val viewModel by viewModel<SingleDishInfoViewModel>()
    private val mainViewModel by sharedViewModel<NewOrderMainViewModel>()

    private lateinit var dishMediaPagerAdapter: DishMediaAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Analytics.with(requireContext()).screen("dishInfo")

        initUi()
        initObserver()

//        mainViewModel.sendClickOnDishEvent()
    }

    private fun initUi() {
        with(binding){
            singleDishPlusMinus.setViewEnabled(true)
            singleDishInfoCook.setUserImageViewListener(this@SingleDishInfoFragment)
            singleDishInfoRating.setOnClickListener { onRatingClick() }

            dishMediaPagerAdapter = DishMediaAdapter(this@SingleDishInfoFragment)
            singleDishInfoImagePager.adapter = dishMediaPagerAdapter

            singleDishChangeTimeBtn.setOnClickListener {
                viewModel.onTimeChangeClick()
            }
            singleDishNote.setWSCounterListener(this@SingleDishInfoFragment)
        }

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
        initOrderDate(fullDish)

        with(binding){
            singleDishNote.setText("")
//            singleDishInfoCook.setUser(fullDish.restaurant)
            singleDishInfoFavorite.setIsFav(fullDish.isFavorite)
            singleDishInfoFavorite.setDishId(fullDish.id)
            singleDishInfoName.text = fullDish.name
            singleDishInfoCookName.text = "By ${fullDish.restaurant.getFullName()}"
            singleDishInfoDescription.text = fullDish.description
            singleDishInfoPrice.text = fullDish.getPriceObj().formatedValue

            singleDishInfoImagePager.offscreenPageLimit = fullDish.getMediaList().size
            dishMediaPagerAdapter.submitList(fullDish.getMediaList())
            if (fullDish.getMediaList().size > 1) {
                singleDishInfoCircleIndicator.setViewPager(singleDishInfoImagePager)
            }

//            fullDish.restaurant.country?.let{
//                Glide.with(requireContext()).load(fullDish.restaurant.country.flagUrl).into(singleDishInfoCookFlag)
//            }

            val menuItem = fullDish.menuItem
            if (menuItem != null) {
                singleDishQuantityView.initQuantityView(menuItem)
                val quantityLeft = menuItem.getQuantityCount()
                val currentCounter = singleDishCount.text.toString()
                singleDishPlusMinus.setPlusMinusListener(this@SingleDishInfoFragment, initialCounter = currentCounter.toInt(), quantityLeft = quantityLeft, canReachZero = false)
            }

            singleDishInfoRatingVal.text = fullDish.rating.toString()

            fullDish.portionSize?.let{
                singleDisInfoPortion.setBody(it)
                singleDisInfoPortion.visibility = View.VISIBLE
                singleDisInfoPortionSep.visibility = View.VISIBLE
            }
    //        if (fullDish.cooksInstructions != null && fullDish.cooksInstructions.isNotEmpty()) {
    //            singleDishInstructionsLayout.visibility = View.VISIBLE
    //            singleDishInstructionsBody.text = fullDish.cooksInstructions
    //        } else {
    //            singleDishInstructionsLayout.visibility = View.GONE
    //        }

            val isNationwide = fullDish.menuItem?.cookingSlot?.isNationwide
            if(isNationwide != null && isNationwide){
                singleDishInfoDeliveryTimeLayout.visibility = View.GONE
            }
            else{
                singleDishInfoDeliveryTimeLayout.visibility = View.VISIBLE
            }
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
        val timePickerBottomSheet = TimePickerBottomSheet(this)
        timePickerBottomSheet.setMenuItems(menuItems)
        timePickerBottomSheet.show(childFragmentManager, Constants.TIME_PICKER_BOTTOM_SHEET)
    }

    override fun onTimerPickerChange() {
        mainViewModel.refreshFullDish()
    }

    override fun onPlusMinusChange(counter: Int, position: Int) {
        viewModel.updateCurrentOrderItem(quantity = counter)
        mainViewModel.updateCartBottomBarByType(CartBottomBar.BottomBarTypes.UPDATE_COUNTER, itemCount = counter, price = viewModel.getTotalPriceForDishQuantity(counter))
    }

    private fun initOrderDate(currentDish: FullDish) {
        with(binding){
            if(currentDish.isNationwide == true){
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
    }

    override fun onUserImageClick(cook: Cook?) {
        cook?.video?.let{
            VideoViewDialog(cook).show(childFragmentManager, Constants.VIDEO_VIEW_DIALOG)
        }
    }

    override fun onPlayClick(url: String) {
        //media adapter interface
        val uri = Uri.parse(url)
        VideoPlayerDialog(uri).show(childFragmentManager, Constants.VIDEO_PLAYER_DIALOG)
    }

    override fun onDestroyView() {
        binding.singleDishInfoImagePager.adapter = null
        super.onDestroyView()
    }

    companion object{
        const val TAG = "wowNSingleDishInfoFrag"
    }


}