package com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart


import android.animation.Animator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.simpler_views.SimpleAnimatorListener
import com.bupp.wood_spoon_eaters.custom_views.simpler_views.SimpleBottomSheetCallback
import com.bupp.wood_spoon_eaters.databinding.UpSaleNCartBottomSheetBinding
import com.bupp.wood_spoon_eaters.features.free_delivery.FreeDeliveryProgressView
import com.bupp.wood_spoon_eaters.features.free_delivery.FreeDeliveryState
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressActivity
import com.bupp.wood_spoon_eaters.features.upsale.cart.CartFragment
import com.bupp.wood_spoon_eaters.features.upsale.upsale.UpSaleFragment
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.utils.AnimationUtil
import com.bupp.wood_spoon_eaters.utils.Utils
import com.bupp.wood_spoon_eaters.utils.waitForLayout
import com.eatwoodspoon.android_utils.views.setSafeOnClickListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class UpSaleNCartBottomSheet() : BottomSheetDialogFragment(), UpSaleFragment.UpSaleListener,
    CartFragment.CartListener, FreeDeliveryProgressView.FreeDeliveryProgressViewListener {

    lateinit var listener: UpsaleNCartBSListener

    private val buttonHeight = Utils.toPx(88)
    private val freeDeliveryHeight = Utils.toPx(180)
    private var defaultPeekHeight = Utils.toPx(400)
    private var currentParentHeight: Int = defaultPeekHeight
    private var behavior: BottomSheetBehavior<View>? = null
    private var currentSheetView: View? = null


    interface UpsaleNCartBSListener {
        fun refreshParentOnCartCleared() {}
        fun onCartDishCLick(customOrderItem: CustomOrderItem)
        fun onUpSaleDishCLick(menuItem: MenuItem) {}
        fun onGoToCheckoutClicked()
    }

    private var binding: UpSaleNCartBottomSheetBinding? = null
    private val viewModel by viewModel<UpSaleNCartViewModel>()


    private val addLocationResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.updateAddressAndProceedToCheckout()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.up_sale_n_cart_bottom_sheet, container, false)
        binding = UpSaleNCartBottomSheetBinding.bind(view)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
        if (savedInstanceState == null) {
            openCartFragment()
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        val height = getScreenHeight()

        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(R.id.design_bottom_sheet) ?: return@setOnShowListener
            currentSheetView = sheet
            behavior = BottomSheetBehavior.from(sheet)
            behavior!!.peekHeight = defaultPeekHeight
            behavior!!.skipCollapsed = true

            behavior!!.addBottomSheetCallback(object : SimpleBottomSheetCallback() {
                override fun onSlide(view: View, v: Float) {
                    val yPos = height - (buttonHeight).toFloat() - view.y //- 81
                    val freeDevYPos = height - (freeDeliveryHeight).toFloat() - view.y
                    if (yPos > buttonHeight) {
                        binding?.floatingCartBtnLayout?.animate()?.y(yPos)?.setDuration(0)?.start()
                    }
                    if (freeDevYPos > freeDeliveryHeight) {
                        binding?.floatingCartFreeDeliveryView?.animate()?.y(freeDevYPos)
                            ?.setDuration(0)?.start()
                    }
                    currentParentHeight = height - view.y.toInt()
                }
            })
            refreshButtonPosition()
            refreshFreeDevPosition()
        }

        return dialog
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.top_cornered_30_bkg)
        viewModel.setCurrentScreen(UpSaleAndCartScreenName.CART_SCREEN)
        setRestaurantName()
        observeEvents()
        initObservers()
        initUi()
    }


    private fun initUi() {
        binding?.apply {

            upSaleCartBtn.setSafeOnClickListener {
                if (viewModel.getCurrentScreenName() == UpSaleAndCartScreenName.CART_SCREEN) {
                    viewModel.onCheckoutClick()
                } else {
                    listener.onGoToCheckoutClicked()
                    viewModel.logUpSaleButtonClickedEvents()
                    dismiss()
                }
            }

            upsaleCartCloseBtn.setSafeOnClickListener {
                childFragmentManager.popBackStack()
                setCartFragmentUi()
            }
        }
    }

    private fun initObservers() {
        viewModel.freeDeliveryData.observe(viewLifecycleOwner) {
            setFreeDeliveryViewState(it)
        }
        viewModel.currentOrderData.observe(viewLifecycleOwner) {
            if (it?.orderItems.isNullOrEmpty()) {
                dismiss()
            }
        }
    }

    private fun setFreeDeliveryViewState(freeDeliveryState: FreeDeliveryState?) {
        binding?.apply {
            floatingCartFreeDeliveryView.setFreeDeliveryState(freeDeliveryState)
        }
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        UpSaleAndCartEvents.OpenUpSaleDialog -> openUpSaleFragmentAndSetUpSaleUi()
                        UpSaleAndCartEvents.GoToCheckout -> {
                            listener.onGoToCheckoutClicked()
                            dismiss()
                        }
                        UpSaleAndCartEvents.GoToSelectAddress -> {
                            addLocationResult.launch(
                                Intent(
                                    requireContext(),
                                    LocationAndAddressActivity::class.java
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun openCartFragment() {
        childFragmentManager.beginTransaction()
            .replace(R.id.upSaleAndCartFragmentContainer, CartFragment())
            .commit()
    }

    private fun openUpSaleFragmentAndSetUpSaleUi() {
        childFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_right_enter,
                R.anim.slide_right_exit,
                R.anim.slide_left_enter,
                R.anim.slide_left_exit
            )
            replace(R.id.upSaleAndCartFragmentContainer, UpSaleFragment())
            addToBackStack(null)
            viewModel.setCurrentScreen(UpSaleAndCartScreenName.UPSALE_SCREEN)
        }

        animateTitle(getString(R.string.upsale_title))
        animateBackBtn(true)
        animateBtn(getString(R.string.upsale_button_no_items_added_title))
    }

    private fun setCartFragmentUi() {
        animateTitle(viewModel.getRestaurantName())
        animateBackBtn(false)
        animateBtn(getString(R.string.go_to_checkout))
        viewModel.setCurrentScreen(UpSaleAndCartScreenName.CART_SCREEN)
    }

    private fun animateTitle(title: String?) {
        binding?.apply {
            AnimationUtil().alphaOut(
                upsaleCartTitle,
                listener = object : SimpleAnimatorListener() {
                    override fun onAnimationEnd(p0: Animator?) {
                        upsaleCartTitle.text = title
                        AnimationUtil().alphaIn(upsaleCartTitle)
                    }
                })
        }
    }

    private fun animateBtn(btnText: String) {
        binding?.apply {
            AnimationUtil().alphaOut(
                floatingCartBtnLayout,
                listener = object : SimpleAnimatorListener() {
                    override fun onAnimationEnd(p0: Animator?) {
                        upSaleCartBtn.setBtnText(btnText)
                        AnimationUtil().alphaIn(floatingCartBtnLayout)
                    }
                })
        }
    }

    private fun animateBackBtn(showBtn: Boolean) {
        binding?.apply {
            if (showBtn) {
                AnimationUtil().alphaIn(upsaleCartCloseBtn)
            } else {
                AnimationUtil().alphaOut(upsaleCartCloseBtn)
            }
        }
    }

    private fun setRestaurantName() {
        binding?.upsaleCartTitle?.text = viewModel.getRestaurantName()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is UpsaleNCartBSListener) {
            listener = context
        } else if (parentFragment is UpsaleNCartBSListener) {
            this.listener = parentFragment as UpsaleNCartBSListener
        } else {
            throw ClassCastException("$context must implement UpsaleNCartBSListener")
        }
    }

    override fun onBackButtonClick() {
        childFragmentManager.popBackStack()
    }

    override fun onUpSaleItemClick(menuItem: MenuItem) {
        listener.onUpSaleDishCLick(menuItem)
    }

    override fun isUpSaleItemsSelected(isSelected: Boolean) {
        if (isSelected) {
            animateBtn(getString(R.string.upsale_button_items_added_title))
        } else {
            animateBtn(getString(R.string.upsale_button_no_items_added_title))
        }
        viewModel.updateIsUpSaleItemsSelected(isSelected)
    }

    override fun onDishCartClicked(customCartItem: CustomOrderItem) {
        listener.onCartDishCLick(customCartItem)
    }


    fun animateExpand() {
        Handler(Looper.getMainLooper()).postDelayed({
            behavior!!.state = STATE_EXPANDED
        }, 1000)
    }

    private fun getStatusBarSize(): Int {
        val resources: Resources = resources
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }

    /**
     * this function calculates the height of the screen without the status bar height
     */
    private fun getScreenHeight(): Int {
        val display: Display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val realSize = Point()
        val realDisplay: Display = requireActivity().windowManager.defaultDisplay
        realDisplay.getRealSize(realSize)
        val maxHeight: Int = if (size.y + getBottomBarHeight() == realSize.y) {
            size.y - getStatusBarSize()
        } else {
            size.y
        }
        return maxHeight
    }

    fun refreshButtonPosition() {
        binding?.floatingCartBtnLayout?.waitForLayout {
            currentSheetView?.let { currentSheetView ->
                val height = getScreenHeight()
                val yPos = height - (buttonHeight).toFloat() - currentSheetView.y
                binding?.floatingCartBtnLayout?.animate()?.y(yPos)?.setDuration(0)?.start()
            }
        }
    }

    fun refreshFreeDevPosition() {
        binding?.floatingCartFreeDeliveryView?.waitForLayout {
            currentSheetView?.let { currentSheetView ->
                val height = getScreenHeight()
                val yPos = height - (freeDeliveryHeight).toFloat() - currentSheetView.y
                binding?.floatingCartFreeDeliveryView?.animate()?.y(yPos)?.setDuration(0)?.start()
            }
        }
    }

    private fun getBottomBarHeight(): Int {
        val resources: Resources = requireContext().resources
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val navigationBarHeight = if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else
            0
        return navigationBarHeight
    }

    override fun thresholdAchieved() {
        viewModel.reportThresholdAchievedEvent()
    }

    override fun viewClicked() {
        viewModel.reportViewClickedEvent()
    }
}