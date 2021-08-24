package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.custom_views.simpler_views.SimpleAnimatorListener
import com.bupp.wood_spoon_eaters.custom_views.simpler_views.SimpleBottomSheetCallback
import com.bupp.wood_spoon_eaters.databinding.UpSaleNCartBottomSheetBinding
import com.bupp.wood_spoon_eaters.di.abs.LiveEvent
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressActivity
import com.bupp.wood_spoon_eaters.utils.AnimationUtil
import com.bupp.wood_spoon_eaters.utils.Utils
import com.bupp.wood_spoon_eaters.utils.waitForLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class UpSaleNCartBottomSheet(val listener: UpsaleNCartBSListener? = null) : BottomSheetDialogFragment() {

    interface UpsaleNCartBSListener{
        fun refreshParentOnCartCleared(){}
        fun onCartDishCLick(customCartItem: CustomCartItem)
        fun onGoToCheckoutClicked()
    }

    private var defaultPeekHeight = Utils.toPx(400)

    private val binding: UpSaleNCartBottomSheetBinding by viewBinding()
    private val viewModel by viewModel<UpSaleNCartViewModel>()
    private var currentParentHeight: Int = defaultPeekHeight
    private var behavior: BottomSheetBehavior<View>? = null
    private var currentSheetView: View? = null

    private val buttonHeight = Utils.toPx(88)

    private lateinit var cartAdapter: UpSaleNCartAdapter

    private val addLocationResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        Log.d(TAG, "Activity For Result - addLocationResult")
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            viewModel.updateAddressAndProceedToCheckout()
            //check if location changed and refresh ui
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.up_sale_n_cart_bottom_sheet, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
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
    fun getScreenHeight(): Int{
        val display: Display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        val realSize = Point()
        val realDisplay: Display = requireActivity().windowManager.defaultDisplay
        realDisplay.getRealSize(realSize)

        var maxWidth = size.x
        var maxHeight = size.y

//        maxHeight = size.y - getStatusBarSize()
//        Log.d(TAG, "realSize: ${realSize.y}")
//        Log.d(TAG, "size: ${size.y}")
//        Log.d(TAG, "maxHeight: $maxHeight")
        if (size.y + getBottomBarHeight() == realSize.y){// || realSize.y == size.y) {
            // if we reached here it means that screenSize includes status bar inside - there fore subtract status bar height
//        if (realSize.y - getBottomBarHeight() == size.y || realSize.y == size.y) {
            Log.d(TAG, "getSize() includes the status bar size");
            maxHeight = size.y - getStatusBarSize()
        }else{
            maxHeight = size.y
        }
        return maxHeight
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

        var height = getScreenHeight()

        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(R.id.design_bottom_sheet)
            sheet?.let{ sheet ->
                currentSheetView = sheet
            }
            behavior = BottomSheetBehavior.from(sheet!!)
            behavior!!.peekHeight = defaultPeekHeight
            behavior!!.addBottomSheetCallback(object : SimpleBottomSheetCallback() {
                override fun onSlide(view: View, v: Float) {
                    val yPos = height - (buttonHeight).toFloat() - view.y //- 81
                    if (yPos > buttonHeight) {
                        binding.floatingCartBtnLayout.animate().y(yPos).setDuration(0).start()
//                        Log.d(TAG, "yPos: ${view.y}")
//                        Log.d(TAG, "view.measuredHeight: ${view.measuredHeight}")
//                        Log.d(TAG, "top: ${view.top}")
//                        Log.d(TAG, "bottom: ${view.bottom}")
//                        Log.d(TAG, "final: $yPos")
                    }
                    currentParentHeight = height - view.y.toInt()
                }
            })
            refreshButtonPosition()
        }

        return dialog
    }

    private fun refreshButtonPosition() {
        binding.floatingCartBtnLayout.waitForLayout {
            val height = getScreenHeight()
            val yPos = height - (buttonHeight).toFloat() - currentSheetView!!.y
            binding.floatingCartBtnLayout.animate().y(yPos).setDuration(0).start()
//            Log.d(TAG, "initial height: $height")
//            Log.d(TAG, "initial currentSheetView.y: ${currentSheetView?.y}")
//            Log.d(TAG, "initial binding.floatingCartBtnLayout.measuredHeight: ${binding.floatingCartBtnLayout.measuredHeight}")
//            Log.d(TAG, "initial binding.floatingCartBtnLayout.y: ${binding.floatingCartBtnLayout.y}")
//            Log.d(TAG, "initial getScreenSizeIncludingTopBottomBar: ${getScreenSizeIncludingTopBottomBar(requireContext())[1]}")
//            Log.d(TAG, "initial getStatusBarSize(): ${getStatusBarSize()}")
//            Log.d(TAG, "initial getBottomBarHeight: ${getBottomBarHeight()}")
        }
    }

    fun getScreenSizeIncludingTopBottomBar(context: Context): IntArray {
        val screenDimensions = IntArray(2) // width[0], height[1]
        val x: Int
        val y: Int
        val orientation = context.resources.configuration.orientation
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val screenSize = Point()
        display.getRealSize(screenSize)
        x = screenSize.x
        y = screenSize.y
        screenDimensions[0] = if (orientation == Configuration.ORIENTATION_PORTRAIT) x else y // width
        screenDimensions[1] = if (orientation == Configuration.ORIENTATION_PORTRAIT) y else x // height
        return screenDimensions
    }

    fun getBottomBarHeight(): Int{
        val resources: Resources = requireContext().resources
            val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            val navigationBarHeight = if (resourceId > 0) {
                resources.getDimensionPixelSize(resourceId)
            } else
                0
        return navigationBarHeight
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.top_cornered_30_bkg)

        viewModel.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_CART)

        initUI()
        initObservers()

    }

    private fun initUI() {
        with(binding) {
            val divider: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.line_divider)
            cartFragList.addItemDecoration(DividerItemDecorator(divider))

            upSaleCartBtn.setOnClickListener {
                viewModel.onCartBtnClick()
            }

            upsaleCartCloseBtn.setOnClickListener {
                onCloseBtnClick()
            }
            viewModel.initData()
        }
    }

    private fun onCloseBtnClick() {
        dismiss()
    }


    private fun initObservers() {
        viewModel.navigationEvent.observe(viewLifecycleOwner, {
            when (it) {
                UpSaleNCartViewModel.NavigationEvent.GO_TO_CHECKOUT -> {
                    listener?.onGoToCheckoutClicked()
                    dismiss()
                }
                UpSaleNCartViewModel.NavigationEvent.GO_TO_UP_SALE -> {
                    navToUpSale()
                }
                UpSaleNCartViewModel.NavigationEvent.GO_TO_SELECT_ADDRESS -> {
                    addLocationResult.launch(Intent(requireContext(), LocationAndAddressActivity::class.java))
                }
            }
        })
        viewModel.upsaleNCartLiveData.observe(viewLifecycleOwner, {
            handleCartData(it)
        })
        viewModel.currentOrderData.observe(viewLifecycleOwner, {
            viewModel.initData()
        })
        viewModel.onDishCartClick.observe(viewLifecycleOwner, {
            handleOnCartDishClick(it)
        })
    }

    private fun handleOnCartDishClick(cartDishData: LiveEvent<CustomCartItem>?) {
        cartDishData?.getContentIfNotHandled()?.let{
                listener?.onCartDishCLick(it)
            }
        }

    private fun handleCartData(data: UpSaleNCartViewModel.CartData?) {
        Log.d(TAG, "handleCartData data: $data")
        if(data != null){

            data.restaurantName?.let{
                binding.upsaleCartTitle.text = it
            }

            cartAdapter = UpSaleNCartAdapter(getAdapterListener())
            binding.cartFragList.initSwipeableRecycler(cartAdapter)
            cartAdapter.submitList(data.items)
        }else{
//            listener?.refreshParentOnCartCleared()
            dismiss()
        }
    }

    private fun getAdapterListener(): UpSaleNCartAdapter.UpSaleNCartAdapterListener =
        object : UpSaleNCartAdapter.UpSaleNCartAdapterListener {
            override fun onDishSwipedAdd(item: CartBaseAdapterItem) {
                when(item){
                    is CartAdapterItem -> {
                        val dishId = item.customCartItem.orderItem.dish.id
                        val note = item.customCartItem.orderItem.notes
                        val orderId = item.customCartItem.orderItem.id
                        val currentQuantity = item.customCartItem.orderItem.quantity
                        viewModel.updateDishInCart(currentQuantity+1, dishId, note, orderId)
                        viewModel.logSwipeDishInCart(Constants.EVENT_SWIPE_ADD_DISH_IN_CART, item.customCartItem)
                    }
                    is UpsaleAdapterItem -> {}
                    else -> {}
                }
            }

            override fun onDishSwipedRemove(item: CartBaseAdapterItem) {
                when(item){
                    is CartAdapterItem -> {
                        val orderItemId = item.customCartItem.orderItem.id
                        viewModel.removeSingleOrderItemId(orderItemId)
                        viewModel.logSwipeDishInCart(Constants.EVENT_SWIPE_REMOVE_DISH_IN_CART, item.customCartItem)
                    }
                    is UpsaleAdapterItem -> {}
                    else -> {}
                }
            }

            override fun onCartItemClicked(customCartItem: CustomCartItem) {
                Log.d(TAG, "onCartItemClicked: $customCartItem")
                viewModel.onCartItemClicked(customCartItem)
                viewModel.logSwipeDishInCart(Constants.EVENT_CLICK_DISH_IN_CART, customCartItem)
                dismiss()
            }
        }

    private fun navToUpSale() {
        Log.d(TAG, "navToUpSale")
        setUpsaleUi()
        animateCollapse(object : SimpleAnimatorListener() {
            override fun onAnimationEnd(p0: Animator?) {
                super.onAnimationEnd(p0)
                Log.d(TAG, "navToUpSale: onAnimationEnd")
                viewModel.initData()
                animateExpand()
            }
        })
    }

    private fun animateCollapse(listener: SimpleAnimatorListener) {
        val currentHeight = currentParentHeight
        Log.d(TAG, "animateCollapse: currentHeight $currentHeight")
        behavior!!.peekHeight = currentHeight
        behavior!!.state = STATE_COLLAPSED
        ObjectAnimator.ofInt(
            behavior!!, "peekHeight",
            currentHeight, 50
        ).apply {
                duration = 500
                interpolator = FastOutSlowInInterpolator()
                addListener(listener)
                start()
            }
    }

    private fun animateExpand() {
        Log.d(TAG, "animateExpand")
        val targetHeight = defaultPeekHeight
        ObjectAnimator.ofInt(
            behavior!!, "peekHeight",
            50, targetHeight
        ).apply {
                duration = 600
                interpolator = FastOutSlowInInterpolator()
                start()
            }
        refreshButtonPosition()

    }

    private fun setUpsaleUi() {
        Log.d(TAG, "setUpsaleUi")
        animateTitle("Any thing else?")
        animateBtn("No Thanks")
        AnimationUtil().alphaIn(binding.upsaleCartCloseBtn)
    }

    private fun setCartUi() {
        Log.d(TAG, "setCartUi")
        animateTitle("Restaurant name")
        animateBtn("Go to checkout")
        AnimationUtil().alphaOut(binding.upsaleCartCloseBtn)
    }

    private fun animateTitle(text: String) {
        AnimationUtil().alphaOut(binding.upsaleCartTitle, listener = object : SimpleAnimatorListener() {
            override fun onAnimationEnd(p0: Animator?) {
                binding.upsaleCartTitle.text = text
                AnimationUtil().alphaIn(binding.upsaleCartTitle)
            }
        })
    }

    private fun animateBtn(text: String) {
        AnimationUtil().alphaOut(binding.floatingCartBtnLayout, listener = object : SimpleAnimatorListener() {
            override fun onAnimationEnd(p0: Animator?) {
                binding.upSaleCartBtn.setBtnText(text)
                AnimationUtil().alphaIn(binding.floatingCartBtnLayout)
            }
        })
    }

    companion object {
        const val TAG = "wowUpSaleNCartBS"

    }

    override fun onDestroy() {
        currentSheetView = null
        behavior = null
        super.onDestroy()
    }

}