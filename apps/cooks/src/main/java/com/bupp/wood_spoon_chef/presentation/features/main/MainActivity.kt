package com.bupp.wood_spoon_chef.presentation.features.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.findNavController
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.common.navigateToCallApp
import com.bupp.wood_spoon_chef.common.navigateToSMSApp
import com.bupp.wood_spoon_chef.presentation.custom_views.BottomBarView
import com.bupp.wood_spoon_chef.presentation.custom_views.HeaderView
import com.bupp.wood_spoon_chef.databinding.ActivityMainBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseActivity
import com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.edit_account_and_kitchen.UpdateAccountViewModel
import com.bupp.wood_spoon_chef.presentation.features.main.dialogs.NotYetApprovedDialog
import com.bupp.wood_spoon_chef.presentation.features.main.my_dishes.dialogs.WelcomeDialog
import com.bupp.wood_spoon_chef.presentation.features.main.orders.order_details.OrderDetailsFragment
import com.bupp.wood_spoon_chef.presentation.features.new_dish.NewDishActivity
import com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account.CreateAccountActivity
import com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account.CreateAccountViewModel
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.network.base.CustomError
import com.bupp.wood_spoon_chef.presentation.features.main.orders.OrdersFragmentDirections
import com.bupp.wood_spoon_chef.presentation.features.main.orders.order_details.ArgumentModelOrderDetails
import com.bupp.wood_spoon_chef.utils.media_utils.MediaUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity(),
    WelcomeDialog.WelcomeDialogListener,
    HeaderView.HeaderViewListener,
    BottomBarView.BottomViewListener,
    OrderDetailsFragment.SingleOrderDialogListener,
    MediaUtils.MediaUtilListener {

    private lateinit var binding: ActivityMainBinding
    val viewModel by viewModel<MainViewModel>()
    private val accountViewModel by viewModel<UpdateAccountViewModel>()

    private val mediaUtil = MediaUtils(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initUi()
        viewModel.checkMetaDataExist()
    }

    private fun initUi() {
        with(binding) {
            mainActBottomView.setBottomViewListener(this@MainActivity)
            mainActBottomView.initView(
                list = listOf(
                    BottomBarView.BottomBarItem(Constants.BOTTOM_VIEW_ORDERS, R.id.ordersFragment),
                    BottomBarView.BottomBarItem(
                        Constants.BOTTOM_VIEW_CALENDER,
                        R.id.calendarFragment
                    ),
                    BottomBarView.BottomBarItem(
                        Constants.BOTTOM_VIEW_DISHES,
                        R.id.myDishesFragment
                    ),
                    BottomBarView.BottomBarItem(
                        Constants.BOTTOM_VIEW_EARNINGS,
                        R.id.earningsFragment
                    ),
                    BottomBarView.BottomBarItem(Constants.BOTTOM_VIEW_PROFILE, R.id.profileFragment)
                ),
                navController = findNavController(R.id.mainActContainer)
            )
        }

        onDishesClick()
        checkForActiveOrder()
        initObservers()
    }

    private fun initObservers() {
        viewModel.getActiveCookingSlot.observe(this) { event ->
            if (event.isSuccess) {
                event.cookingSlots?.let {
                    openActiveCookingSlot(event.cookingSlots)
                }
            }
        }
        viewModel.notApprovedEvent.observe(this) { event ->
            if (event != null) {
                if (event) {
                    binding.mainActBottomView.selectTab(Constants.BOTTOM_VIEW_DISHES)
                    NotYetApprovedDialog().show(
                        supportFragmentManager,
                        Constants.NOT_YET_APPROVED_DIALOG
                    )
                }
            }
        }
        accountViewModel.navigationEvent.observe(this) {
            when (it) {
                CreateAccountViewModel.NavigationEventType.START_VIDEO_CAPTURE -> {
                    mediaUtil.startVideoFetcher(Constants.MEDIA_TYPE_VIDEO)
                }
                CreateAccountViewModel.NavigationEventType.START_IMAGE_CAPTURE -> {
                    mediaUtil.startPhotoFetcher(Constants.MEDIA_TYPE_COOK_IMAGE)
                }
                CreateAccountViewModel.NavigationEventType.START_COVER_CAPTURE -> {
                    mediaUtil.startPhotoFetcher(Constants.MEDIA_TYPE_COVER_IMAGE)
                }
                else -> {
                }
            }
        }
    }

    private fun openActiveCookingSlot(cookingSlot: CookingSlot) {
        findNavController(R.id.mainActContainer).apply {
            val action = OrdersFragmentDirections.actionOrderFragmentToOrderDetailsFragment(
                ArgumentModelOrderDetails(argCookingSlot = cookingSlot)
            )
            navigate(action)
        }
    }

    override fun onSingleOrderDismiss(shouldRefreshOrderList: Boolean) {
        //do nothing
    }

    private fun checkForActiveOrder() {
        viewModel.getActiveCookingSlot()
    }

    // new activities method
    private fun openNewDishForResult() {
        afterNewDishResult.launch(Intent(this, NewDishActivity::class.java))
    }

    fun addNewDishToCalendar() {
        openNewDishForResult()
    }

    //welcome dialog interface -
    override fun onContinueClick() {
        Log.d("wow", "onContinueClick")
    }

    override fun onNotNowClick() {
        Log.d("wow", "onNotNowClick")
    }


    //header view interface
    override fun onHeaderBackClick() {
        onBackPressed()
    }

    override fun onHeaderSettingsClick() {
        viewModel.onHeaderSettingsClick()
    }

    override fun onHeaderAddClick() {
        openNewDishForResult()
    }

    //header view interface done

    override fun onBackPressed() {
        when (findNavController(R.id.mainActContainer).currentDestination?.id) {
            R.id.myDishesFragment -> {
                finish()
            }
            R.id.calendarFragment, R.id.ordersFragment, R.id.earningsFragment, R.id.profileFragment -> {
                onDishesClick()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    //bottom bar interface
    override fun onOrdersClick() {
        if (!viewModel.isChefPending()) {
            with(binding) {
                mainActBottomView.selectTab(Constants.BOTTOM_VIEW_ORDERS)
            }
        }
    }

    override fun onCalenderClick(cookingSlot: CookingSlot?) {
        if (!viewModel.isChefPending()) {
            with(binding) {
                mainActBottomView.selectTab(Constants.BOTTOM_VIEW_CALENDER, cookingSlot)
            }
        }
    }

    override fun onDishesClick() {
        with(binding) {
            mainActBottomView.selectTab(Constants.BOTTOM_VIEW_DISHES)
        }
    }

    override fun onEarningsClick() {
        if (!viewModel.isChefPending()) {
            with(binding) {
                mainActBottomView.selectTab(Constants.BOTTOM_VIEW_EARNINGS)
            }
        }
    }

    override fun onProfileClick() {
        with(binding) {
            mainActBottomView.selectTab(Constants.BOTTOM_VIEW_PROFILE)
        }
    }

    fun onContactUsClick() {
        val phone = viewModel.getContactUsPhoneNumber()
        navigateToCallApp(phone)
    }

    fun sendSmsText() {
        val phone = viewModel.getContactUsTextNumber()
        navigateToSMSApp(phone)
    }

    fun startEditCookingSlot(cookingSlot: CookingSlot) {
        onCalenderClick(cookingSlot)
    }

    //activityForResult
    private val afterNewDishResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                onDishesClick()
            }
        }

    override fun onMediaUtilResult(result: MediaUtils.MediaUtilResult) {
        when (result.mediaType) {
            Constants.MEDIA_TYPE_VIDEO -> {
                result.fileUri?.let { accountViewModel.onVideoResult(it) }
            }
            Constants.MEDIA_TYPE_COOK_IMAGE -> {
                accountViewModel.onThumbnailResult(result)
            }
            Constants.MEDIA_TYPE_COVER_IMAGE -> {
                accountViewModel.onCoverResult(result)
            }
        }
    }

    override fun fileToBigError() {
        val error =
            CustomError("Sorry, your file is too large to upload. It needs to be 100 MB or smaller in size")
        handleErrorEvent(error, binding.root)
    }
}