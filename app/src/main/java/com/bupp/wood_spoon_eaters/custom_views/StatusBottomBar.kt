//package com.bupp.wood_spoon_eaters.custom_views
//
//import android.content.Context
//import android.util.AttributeSet
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.widget.FrameLayout
//import com.bupp.wood_spoon_eaters.R
//import com.bupp.wood_spoon_eaters.common.Constants
//import java.text.DecimalFormat
//
//
//class StatusBottomBar : FrameLayout {
//
//    interface StatusBottomBarListener{
//        fun onStatusBarClicked(type: Int?)
//    }
//    private var listener: StatusBottomBarListener? = null
//
//    private var curType: Int? = Constants.CART_BOTTOM_BAR_TYPE_CART
//
//    constructor(context: Context) : this(context, null)
//    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
//    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
//        LayoutInflater.from(context).inflate(R.layout.status_bottom_bar, this, true)
//        initUi()
//    }
//
//    private fun initUi() {
//        statusBottomBarLayout.setOnClickListener { listener?.onStatusBarClicked(curType) }
//    }
//
//    fun setStatusBottomBarListener(listener: StatusBottomBarListener){
//        this.listener = listener
//    }
//
//    override fun setEnabled(isEnabled: Boolean) {
//        super.setEnabled(isEnabled)
//        Log.d("wowStatusBottomBar", "isEnabled: $isEnabled")
////        this.isEnabled = isEnabled
//        statusBottomBarLayout.isEnabled = isEnabled
//        if (isEnabled) {
//            statusBottomBarLayout.alpha = 1f
//        } else {
//            statusBottomBarLayout.alpha = 0.5f
//        }
//    }
//
//    fun updateStatusBottomBar(type: Int? = null, price: Double? = null, itemCount: Int? = null) {
//        if(type != null){
//            this.curType = type
//        }
//        if(price != null){
//            val priceStr = DecimalFormat("##.##").format(price)
//            statusBottomBarPrice.text = "$$priceStr"
//        }
//        when(curType){
//            Constants.CART_BOTTOM_BAR_TYPE_CART -> {
//                statusBottomBarPrice.visibility = View.VISIBLE
//                if(itemCount != null){
//                    statusBottomBarTitle.text = "Add $itemCount To Cart"
//                }
//            }
//            Constants.CART_BOTTOM_BAR_TYPE_CHECKOUT -> {
//                statusBottomBarPrice.visibility = View.VISIBLE
//                statusBottomBarTitle.text = "Proceed To Checkout"
//            }
//            Constants.CART_BOTTOM_BAR_TYPE_FINALIZE -> {
//                statusBottomBarTitle.text = "PLACE AN ORDER"
//            }
//        }
//    }
//
//}