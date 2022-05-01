package com.bupp.wood_spoon_chef.presentation.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.databinding.BottomBarViewBinding
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot

class BottomBarView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private var binding: BottomBarViewBinding = BottomBarViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        initUi()
    }

    private lateinit var navController: NavController
    private lateinit var tabsList: List<BottomBarItem>

    data class BottomBarItem(
        val type: Int,
        val navId: Int
    )

    interface BottomViewListener {
        fun onOrdersClick() {}
        fun onCalenderClick(cookingSlot: CookingSlot? = null) {}
        fun onDishesClick() {}
        fun onEarningsClick() {}
        fun onProfileClick() {}
    }

    var listener: BottomViewListener? = null
    private var currentTab: Int = 0

    fun setBottomViewListener(listenerInstance: BottomViewListener) {
        listener = listenerInstance
    }

    fun initView(list: List<BottomBarItem>, navController: NavController) {
        this.tabsList = list
        this.navController = navController
        onTabClicked(binding.bottomViewOrders, Constants.BOTTOM_VIEW_ORDERS, null) //default
    }

    private fun initUi() {
        with(binding) {
            bottomViewOrders.setOnClickListener { v ->
                if (!bottomViewOrders.isSelected) {
                    listener?.onOrdersClick()
                }
            }
            bottomViewCalender.setOnClickListener { v ->
                if (!bottomViewCalender.isSelected) {
                    listener?.onCalenderClick()
                }
            }
            bottomViewDishes.setOnClickListener { v ->
                if (!bottomViewDishes.isSelected) {
                    listener?.onDishesClick()
                }
            }
            bottomViewEarnings.setOnClickListener { v ->
                if (!bottomViewEarnings.isSelected) {
                    listener?.onEarningsClick()
                }
            }
            bottomViewProfile.setOnClickListener { v ->
                if (!bottomViewProfile.isSelected) {
                    listener?.onProfileClick()
                }
            }
        }
    }

    fun selectTab(type: Int, extra: Any? = null) {
        with(binding) {
            when (type) {
                Constants.BOTTOM_VIEW_ORDERS -> {
                    onTabClicked(bottomViewOrders, Constants.BOTTOM_VIEW_ORDERS, extra)
                }
                Constants.BOTTOM_VIEW_CALENDER -> {
                    onTabClicked(bottomViewCalender, Constants.BOTTOM_VIEW_CALENDER, extra)
                }
                Constants.BOTTOM_VIEW_DISHES -> {
                    onTabClicked(bottomViewDishes, Constants.BOTTOM_VIEW_DISHES, extra)
                }
                Constants.BOTTOM_VIEW_EARNINGS -> {
                    onTabClicked(bottomViewEarnings, Constants.BOTTOM_VIEW_EARNINGS, extra)
                }
                Constants.BOTTOM_VIEW_PROFILE -> {
                    onTabClicked(bottomViewProfile, Constants.BOTTOM_VIEW_PROFILE, extra)
                }
            }
        }
    }

    private fun onTabClicked(v: BottomBarButton, type: Int, extra: Any?) {
        unSelectAll()
        v.isSelected(true)
        navigateToFragment(type, extra)
    }

    private fun navigateToFragment(type: Int, extra: Any?) {
        tabsList[type].let { tab ->
            val bundle = bundleOf("extra" to extra)
            navController.navigate(tab.navId, bundle, getNavOption(type))
            currentTab = type
        }
    }

    private fun unSelectAll() {
        with(binding) {
            bottomViewOrders.isSelected(false)
            bottomViewCalender.isSelected(false)
            bottomViewDishes.isSelected(false)
            bottomViewEarnings.isSelected(false)
            bottomViewProfile.isSelected(false)
        }
    }

    private fun getNavOption(clickedTab: Int): NavOptions {
        return if (clickedTab > currentTab) {
            NavOptions.Builder()
                .setEnterAnim(R.anim.slide_right_enter)
                .setExitAnim(R.anim.slide_right_exit)
                .setPopEnterAnim(R.anim.slide_left_enter)
                .setPopExitAnim(R.anim.slide_left_exit)
                .build()
        } else {
            NavOptions.Builder()
                .setEnterAnim(R.anim.slide_left_enter)
                .setExitAnim(R.anim.slide_left_exit)
                .setPopEnterAnim(R.anim.slide_right_enter)
                .setPopExitAnim(R.anim.slide_right_exit)
                .build()
        }
    }
}