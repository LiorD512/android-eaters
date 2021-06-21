package com.bupp.wood_spoon_eaters.custom_views.tool_tip_view

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.StackableTextViewBinding
import com.bupp.wood_spoon_eaters.databinding.ToolTipViewBinding
import com.daasuu.bl.ArrowDirection
import com.daasuu.bl.BubbleLayout
import com.daasuu.bl.BubblePopupHelper
import com.example.matthias.mvvmcustomviewexample.custom.ToolTipViewModel


class ToolTipView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private var binding: ToolTipViewBinding = ToolTipViewBinding.inflate(LayoutInflater.from(context), this, true)


    private var type: Int = 0
    private var attr: TypedArray? = null
    private var isShowing: Boolean = false
    private var bodyText: String = ""
    private var titleText: String = ""
    private var popupWindow: PopupWindow? = null
    private lateinit var bubbleLayout: BubbleLayout

    val viewModel = ToolTipViewModel()

    init {
        initUi(context, attrs)
    }

    fun customInit(context: Context, attrs: AttributeSet?){
        initUi(context, attrs)
    }

    private fun initUi(context: Context, attrs: AttributeSet?) {
        with(binding) {
            bubbleLayout = LayoutInflater.from(context).inflate(R.layout.layout_tool_tip, null) as BubbleLayout
            popupWindow = BubblePopupHelper.create(context, bubbleLayout)
            popupWindow!!.animationStyle = R.style.ToolTipAnimation
            popupWindow!!.isFocusable = true

            popupWindow!!.setOnDismissListener { isShowing = false }

            attr = context.obtainStyledAttributes(attrs, R.styleable.ToolTipView)
            type = attr!!.getInt(R.styleable.ToolTipView_tip_type, 0)
            attr!!.recycle()

            toolTipBtn.setOnClickListener(OnClickListener { v ->
                if (popupWindow == null)
                    return@OnClickListener
                showTooltip(v)
                isShowing = true
            })
        }
    }

    fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
    fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    private fun customBubble() {
        when (type) {
            Constants.TOOL_TIP_SERVICE_FEE -> {
                titleText = resources.getString(R.string.tool_tip_service_fee_title)
                bodyText = resources.getString(R.string.tool_tip_service_fee_body)
                bubbleLayout.arrowDirection = ArrowDirection.TOP
                bubbleLayout.arrowPosition = 85.toPx().toFloat()
            }
            Constants.TOOL_TIP_CHECKOUT_SERVICE_FEE -> {
                titleText = resources.getString(R.string.tool_tip_service_fee_title)
                bodyText = resources.getString(R.string.tool_tip_service_fee_body)
                bubbleLayout.arrowDirection = ArrowDirection.TOP
                bubbleLayout.arrowPosition = 85.toPx().toFloat()
            }
            Constants.TOOL_TIP_CHECKOUT_DELIVERY_FEE -> {
                titleText = resources.getString(R.string.tool_tip_delivery_fee_title)
                bodyText = resources.getString(R.string.tool_tip_delivery_fee_body)
                bubbleLayout.arrowDirection = ArrowDirection.TOP
                bubbleLayout.arrowPosition = 85.toPx().toFloat()
            }
            Constants.TOOL_TIP_MINMUM_ORDER_FEE -> {
                titleText = resources.getString(R.string.tool_tip_min_order_fee_title)
                bodyText =
                    "${resources.getString(R.string.tool_tip_min_order_fee_body)} ${viewModel.getMinOrderFeeString()} ${resources.getString(com.bupp.wood_spoon_eaters.R.string.tool_tip_min_order_fee_body2)}"
                bubbleLayout.arrowDirection = ArrowDirection.TOP
                bubbleLayout.arrowPosition = 95.toPx().toFloat()
            }
            Constants.TOOL_TIP_COURIER_TIP -> {
                titleText = resources.getString(R.string.tool_tip_courier_title)
                bodyText =
                    "${resources.getString(R.string.tool_tip_courier_body)}"
                bubbleLayout.arrowDirection = ArrowDirection.TOP
                bubbleLayout.arrowPosition = 135.toPx().toFloat()
            }
        }
    }

    private fun showTooltip(view: View?) {
        with(binding) {
            customBubble()

            val title = bubbleLayout.findViewById<TextView>(R.id.toolTipTextTitle)
            val body = bubbleLayout.findViewById<TextView>(R.id.toolTipText)
            title.text = titleText
            body.text = bodyText

            val location = locateView(view)

            when (type) {
                Constants.TOOL_TIP_SERVICE_FEE -> {
                    val marginLeft = 23.toPx()
                    popupWindow!!.showAtLocation(view, Gravity.NO_GRAVITY, marginLeft, location!!.bottom)
                }
                Constants.TOOL_TIP_CHECKOUT_SERVICE_FEE -> {
                    val marginLeft = 2.toPx()
                    popupWindow!!.showAtLocation(view, Gravity.NO_GRAVITY, marginLeft, location!!.bottom)
                }
                Constants.TOOL_TIP_CHECKOUT_DELIVERY_FEE -> {
                    val marginLeft = 8.toPx()
                    popupWindow!!.showAtLocation(view, Gravity.NO_GRAVITY, marginLeft, location!!.bottom)
                }
                Constants.TOOL_TIP_MINMUM_ORDER_FEE -> {
                    val marginLeft = 23.toPx()
                    popupWindow!!.showAtLocation(view, Gravity.NO_GRAVITY, marginLeft, location!!.bottom)
                }
                Constants.TOOL_TIP_COURIER_TIP -> {
                    val marginLeft = 23.toPx()
                    popupWindow!!.showAtLocation(view, Gravity.NO_GRAVITY, marginLeft, location!!.bottom)
                }
            }
        }
    }

    fun locateView(v: View?): Rect? {
        val loc_int = IntArray(2)
        if (v == null) return null
        try {
            v.getLocationOnScreen(loc_int)
        } catch (npe: NullPointerException) {
            //Happens when the view doesn't exist on screen anymore.
            return null
        }

        val location = Rect()
        location.left = loc_int[0]
        location.top = loc_int[1]
        location.right = location.left + v.width
        location.bottom = location.top + v.height
        return location
    }

    fun isNationWide(nationwide: Boolean? = false) {
        viewModel.isNationwide = nationwide!!
    }
}