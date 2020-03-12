package com.bupp.wood_spoon_eaters.custom_views.orders_bottom_bar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.orders_bottom_bar.view.*
import java.text.DecimalFormat


class OrdersBottomBar : FrameLayout{

    private var curType: Int = -1
    var showSep = 0
    var ordersVisible = false
    var checkoutVisible = false

    var listener: OrderBottomBatListener? = null
    interface OrderBottomBatListener{
        fun onBottomBarOrdersClick(curType: Int)
        fun onBottomBarCheckoutClick()
    }

    fun setOrdersBottomBarListener(listener: OrderBottomBatListener){
        this.listener = listener
    }



    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.orders_bottom_bar, this, true)

        initUi()

    }

    private fun initUi() {
        ordersBottomBarOrder.setOnClickListener { listener?.onBottomBarOrdersClick(curType) }
        ordersBottomBarCheckout.setOnClickListener { listener?.onBottomBarCheckoutClick() }
    }

    fun handleBottomBar(showActiveOrders: Boolean? = null, showCheckout: Boolean? = null){
       showActiveOrders?.let{
           if(it){
               ordersBottomBarOrder.visibility = View.VISIBLE
               ordersVisible = true
           }else{
               ordersBottomBarOrder.visibility = View.GONE
               ordersVisible = false
           }
       }
       showCheckout?.let{
           if(it){
               ordersBottomBarCheckout.visibility = View.VISIBLE
               checkoutVisible = true
           }else{
               ordersBottomBarCheckout.visibility = View.GONE
               checkoutVisible = false
           }
       }
       checkSepEnable()
   }

    private fun checkSepEnable() {
        if(ordersVisible && checkoutVisible){
            bottomBarSeparator.visibility = View.VISIBLE
        }else{
            bottomBarSeparator.visibility = View.GONE
        }
    }

    fun updateStatusBottomBar(type: Int? = null, price: Double? = null, checkoutPrice : Double? = null, itemCount: Int? = null) {
//        ordersBottomBarOrder.visibility = View.VISIBLE
//        ordersVisible = true
        if(type != null){
            this.curType = type
        }



        when(curType){
            Constants.STATUS_BAR_TYPE_CART -> {
                ordersBottomBarOrder.visibility = View.VISIBLE
                ordersVisible = true
                ordersBottomBarOrderPrice.visibility = View.VISIBLE
                itemCount?.let{
                    ordersBottomBarOrderTitle.text = "Add $itemCount To Cart"
                }
                price?.let{
                    val priceStr = DecimalFormat("##.##").format(price)
                    ordersBottomBarOrderPrice.text = "$$priceStr"
                }
            }
            Constants.STATUS_BAR_TYPE_CHECKOUT -> {
                checkoutVisible = true
                ordersBottomBarCheckout.visibility = View.VISIBLE
//                ordersBottomBarOrderTitle.text = "Proceed To Cart"

//                ordersBottomBarCheckout.visibility = View.GONE
//                checkoutVisible = false
                checkoutPrice?.let{
                    val checkoutPriceStr = DecimalFormat("##.##").format(checkoutPrice)
                    ordersBottomBarCheckoutPrice.text = "$$checkoutPriceStr"
                    ordersBottomBarCheckoutPrice.visibility = View.VISIBLE
                }
            }
            Constants.STATUS_BAR_TYPE_FINALIZE -> {
                ordersBottomBarOrderTitle.text = "PLACE AN ORDER"
                ordersBottomBarCheckout.visibility = View.GONE
                checkoutVisible = false
            }
        }
        checkSepEnable()
    }


}