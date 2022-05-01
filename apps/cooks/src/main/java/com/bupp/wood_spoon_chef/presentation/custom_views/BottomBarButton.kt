package com.bupp.wood_spoon_chef.presentation.custom_views

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.databinding.BottomBarButtonViewBinding
import com.bupp.wood_spoon_chef.databinding.BottomBarViewBinding
import com.bupp.wood_spoon_chef.databinding.HeaderViewBinding

class BottomBarButton  @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr){

        private var binding: BottomBarButtonViewBinding = BottomBarButtonViewBinding.inflate(LayoutInflater.from(context), this, true)

        init {
        if (attrs != null) {
            val attrSet = context.obtainStyledAttributes(attrs, R.styleable.BottomBarButton)

            if (attrSet.hasValue(R.styleable.BottomBarButton_title)) {
                val title = attrSet.getString(R.styleable.BottomBarButton_title)
                setTitle(title)
            }
            if (attrSet.hasValue(R.styleable.BottomBarButton_icon)) {
                val setIcon = attrSet.getResourceId(R.styleable.BottomBarButton_icon, 0)
                setIcon(setIcon)
            }
            attrSet.recycle()
        }

    }

    private fun setTitle(title: String?) {
        binding.bottomBarBtnTitle.text = title
    }

    private fun setIcon(icon: Int) {
        binding.bottomBarBtnIcon.setImageDrawable(ContextCompat.getDrawable(context, icon))
    }

    fun isSelected(isSelected: Boolean) {
        with(binding) {
            val color = if (isSelected) {
                ContextCompat.getColor(context, R.color.dark)
            } else {
                ContextCompat.getColor(context, R.color.purpley_grey)
            }
            val typeFace = if (isSelected) {
                ResourcesCompat.getFont(context, R.font.lato_black)
            } else {
                ResourcesCompat.getFont(context, R.font.lato_reg)
            }
            bottomBarBtnTitle.setTextColor(color)
            bottomBarBtnTitle.typeface = typeFace
            bottomBarBtnIcon.isSelected = isSelected
        }
    }

    override fun isSelected():Boolean =
        binding.bottomBarBtnIcon.isSelected


}