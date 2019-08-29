package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.action_title_view.view.*


class ActionTitleView : FrameLayout {

    interface ActionTitleViewListener {
        fun onActionViewClick(type: Int){}
        fun onMyLocationClick() {}
    }

    fun setActionTitleViewListener(listener: ActionTitleViewListener) {
        this.listener = listener
    }

    var listener: ActionTitleViewListener? = null
    var isMandatory = false
    var type = -1

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.action_title_view, this, true)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.ActionTitleView)
            if (a.hasValue(R.styleable.ActionTitleView_title)) {
                isMandatory = a.getBoolean(R.styleable.ActionTitleView_isMandatory, false)
                var title = a.getString(R.styleable.ActionTitleView_title)
                if (isMandatory) {
                    actionTitleViewSuffix.visibility = View.VISIBLE
                }
                actionTitleViewTitle.text = title
            } else {
                actionTitleViewTitle.visibility = View.GONE
            }

            if (a.hasValue(R.styleable.ActionTitleView_hint)) {
                val hint = a.getString(R.styleable.ActionTitleView_hint)
                if (!hint.isEmpty()) {
                    actionTitleViewInput.hint = hint
                }
            }

            if (a.hasValue(R.styleable.ActionTitleView_action)) {
                type = a.getInt(R.styleable.ActionTitleView_action, -1)
            }

            val isWithLocation = a.getBoolean(R.styleable.ActionTitleView_isWithLocation, false)
            if (isWithLocation) {
                actionTitleViewLocation.visibility = View.VISIBLE
                actionTitleViewLocation.setOnClickListener { getMyLocation() }
            }
            a.recycle()
        }

        initUi()
    }
    
    fun updateLocationIcon(isEnabled: Boolean){
        actionTitleViewLocation.isEnabled = isEnabled
    }

    private fun getMyLocation() {
        listener?.onMyLocationClick()
    }

    private fun initUi() {
        actionTitleViewInput.setOnClickListener {
            listener?.onActionViewClick(type)
        }


    }

    fun setText(text: String) {
        actionTitleViewInput.text = text
    }

    fun getText(): String {
        return actionTitleViewInput.text.toString()
    }

    fun isValid(): Boolean {
        if (isMandatory) {
            if (getText().isNotEmpty()) {
                actionTitleViewInput.setTextColor(ContextCompat.getColor(context, R.color.dark))
                return false
            } else {
                actionTitleViewInput.setTextColor(ContextCompat.getColor(context, R.color.red))
                return false
            }
        } else {
            return true
        }
    }


}