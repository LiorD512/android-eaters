package com.bupp.wood_spoon_chef.presentation.custom_views

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
import android.widget.PopupWindow
import android.widget.TextView
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.databinding.ToolTipViewBinding
import com.daasuu.bl.ArrowDirection
import com.daasuu.bl.BubbleLayout
import com.daasuu.bl.BubblePopupHelper

class ToolTipView : FrameLayout{

    private var binding: ToolTipViewBinding = ToolTipViewBinding.inflate(LayoutInflater.from(context), this, true)
    private var type: Int = 0
    private var attr: TypedArray? = null
    private var isShowing: Boolean = false
    private var bodyText: String = ""
    private var titleText: String = ""
    private var popupWindow: PopupWindow? = null
    private lateinit var bubbleLayout: BubbleLayout
    fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()


    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) :
            this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.tool_tip_view, this, true)

        initUi(context, attrs)
    }

    private fun initUi(context: Context, attrs: AttributeSet?) {
        bubbleLayout = LayoutInflater.from(context).inflate(R.layout.layout_tool_tip, null) as BubbleLayout
        popupWindow = BubblePopupHelper.create(context, bubbleLayout)
        popupWindow!!.animationStyle = R.style.ToolTipAnimation
        popupWindow!!.isFocusable = true

        popupWindow!!.setOnDismissListener { isShowing = false }

        attr = context.obtainStyledAttributes(attrs, R.styleable.ToolTipView)
        type = attr!!.getInt(R.styleable.ToolTipView_tip_type, 0)

        val isClickableFromOutside = attr!!.getBoolean(R.styleable.ToolTipView_is_clickable_from_outside, false)
        if(!isClickableFromOutside){
            binding.toolTipBtn.setOnClickListener(OnClickListener { v ->
                if (popupWindow == null)
                    return@OnClickListener
                showTooltip(v)
                isShowing = true
            })
        }

        attr!!.recycle()

    }


    private fun customBubble() {
        when (type) {
            Constants.TOOL_TIP_SERVICE_FEE -> {
                titleText = resources.getString(R.string.tool_tip_service_fee_title)
                bodyText = resources.getString(R.string.tool_tip_service_fee_body)
                bubbleLayout.arrowDirection = ArrowDirection.TOP
                bubbleLayout.arrowPosition = 85.toPx().toFloat()
            }
            Constants.TOOL_TIP_SSN -> {
                titleText = resources.getString(R.string.tool_tip_ssn_title)
                bodyText = resources.getString(R.string.tool_tip_ssn_body)
                bubbleLayout.arrowDirection = ArrowDirection.TOP
                bubbleLayout.arrowPosition = 43.toPx().toFloat()
            }
            Constants.TOOL_TIP_PROFILE_FLAG -> {
                titleText = resources.getString(R.string.tool_tip_profile_flag_title)
                bodyText = resources.getString(R.string.tool_tip_profile_flag_body)
                bubbleLayout.arrowDirection = ArrowDirection.TOP
                bubbleLayout.arrowPosition = 110.toPx().toFloat()
            }
        }
    }

    private fun showTooltip(view: View?) {
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
            Constants.TOOL_TIP_SSN -> {
//                textView.maxWidth = DpUtils.getPixelsFromDP(context, 150)
//                val topOffset = 83.toDp()
                val marginLeft = 23.toPx()
                popupWindow!!.showAtLocation(view, Gravity.NO_GRAVITY, marginLeft, location!!.bottom)
            }
            Constants.TOOL_TIP_PROFILE_FLAG -> {
//                textView.maxWidth = DpUtils.getPixelsFromDP(context, 150)
//                val topOffset = 83.toDp()
                val marginLeft = 23.toPx()
                popupWindow!!.showAtLocation(view, Gravity.NO_GRAVITY, marginLeft, location!!.bottom)
            }
        }
    }

    private fun locateView(v: View?): Rect? {
        val locInt = IntArray(2)
        if (v == null) return null
        try {
            v.getLocationOnScreen(locInt)
        } catch (npe: NullPointerException) {
            //Happens when the view doesn't exist on screen anymore.
            return null
        }

        val location = Rect()
        location.left = locInt[0]
        location.top = locInt[1]
        location.right = location.left + v.width
        location.bottom = location.top + v.height
        return location
    }

    fun setToolTipType(type: Int) {
        this.type = type
        customBubble()
    }
}