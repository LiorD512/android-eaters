package com.bupp.wood_spoon_chef.presentation.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.databinding.CreateCookingSlotTopBarBinding

class CreateCookingSlotTopBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    interface CreateCookingSlotTopBarListener {
        fun onBackClick() { }
        fun onMenuClick() { }
    }

    private var binding: CreateCookingSlotTopBarBinding = CreateCookingSlotTopBarBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    private var listener: CreateCookingSlotTopBarListener? = null

    init {
        initUi()
        initAttrs(attrs)
    }

    private fun initUi() {
        binding.apply {
            createCookingSlotTopBarImg.setOnClickListener { listener?.onBackClick() }
            ivIconMenu.setOnClickListener { listener?.onMenuClick() }
        }
    }

    private fun initAttrs(attrs: AttributeSet?) {
        if (attrs != null) {
            val attrSet = context.obtainStyledAttributes(attrs, R.styleable.CreateCookingSlotTopBar)

            if (attrSet.hasValue(R.styleable.CreateCookingSlotTopBar_title)) {
                val title = attrSet.getString(R.styleable.CreateCookingSlotTopBar_title)
                setTitle(title)
            }

            if (attrSet.hasValue(R.styleable.CreateCookingSlotTopBar_subTitle)) {
                val setSubTitle = attrSet.getString(R.styleable.CreateCookingSlotTopBar_subTitle)
                setSubTitle(setSubTitle)
            }

            if (attrSet.hasValue(R.styleable.CreateCookingSlotTopBar_cooking_slot_bar_icon)) {
                val icon = attrSet.getResourceId(
                    R.styleable.CreateCookingSlotTopBar_cooking_slot_bar_icon,
                    0
                )
                setIcon(icon)

            }

            if (attrSet.hasValue(R.styleable.CreateCookingSlotTopBar_cooking_slot_bar_menu_icon)) {
                val icon = attrSet.getResourceId(
                    R.styleable.CreateCookingSlotTopBar_cooking_slot_bar_menu_icon,
                    0
                )
                setMenuIcon(icon)

            }

            attrSet.recycle()
        }
    }

    private fun setMenuIcon(icon: Int) {
        binding.ivIconMenu.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                icon
            )
        )
    }

    fun setSubTitle(subTitle: String?) {
        if (subTitle == null) {
            binding.tvSubtitle.visibility = View.INVISIBLE
        } else {
            binding.tvSubtitle.text = subTitle
        }
    }

    fun setCookingSlotTopBarListener(listenerInstance: CreateCookingSlotTopBarListener){
        listener = listenerInstance
    }

    fun setTitle(title: String?) {
        binding.createCookingSlotTopBarTitle.text = title
    }

    fun setIcon(icon: Int) {
        binding.createCookingSlotTopBarImg.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                icon
            )
        )
    }
}