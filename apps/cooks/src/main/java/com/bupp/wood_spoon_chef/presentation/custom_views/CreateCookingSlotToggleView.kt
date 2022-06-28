package com.bupp.wood_spoon_chef.presentation.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.CreateCookingSlotToggleViewBinding
import com.bupp.wood_spoon_chef.utils.extensions.show

class CreateCookingSlotToggleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: CreateCookingSlotToggleViewBinding =
        CreateCookingSlotToggleViewBinding.inflate(
            LayoutInflater.from(context), this, true
        )

    init {
        initAttrs(attrs)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        if (attrs != null) {
            val attrSet =
                context.obtainStyledAttributes(attrs, R.styleable.CreateCookingSlotToggleView)

            if (attrSet.hasValue(R.styleable.CreateCookingSlotToggleView_title)) {
                val title = attrSet.getString(R.styleable.CreateCookingSlotToggleView_title)
                setTitle(title)
            }

            if (attrSet.hasValue(R.styleable.CreateCookingSlotToggleView_subtitle)) {
                val title = attrSet.getString(R.styleable.CreateCookingSlotToggleView_subtitle)
                setSubtitle(title)
            }

            if (attrSet.hasValue(R.styleable.CreateCookingSlotToggleView_mainIcon)) {
                val icon = attrSet.getResourceId(
                    R.styleable.CreateCookingSlotToggleView_mainIcon,
                    0
                )
                setIcon(icon)
            }

            if (attrSet.hasValue(R.styleable.CreateCookingSlotToggleView_showSubTitle)) {
                val show =
                    attrSet.getBoolean(R.styleable.CreateCookingSlotToggleView_showSubTitle, false)
                showSubtitle(show)
            }

            attrSet.recycle()
        }
    }

    private fun setTitle(title: String?) {
        binding.createCookingSlotToggleViewTitle.text = title
    }

    private fun setSubtitle(subtitle: String?) {
        binding.createCookingSlotToggleViewSubTitle.text = subtitle
    }

    private fun setIcon(icon: Int) {
        binding.createCookingSlotToggleViewMainIcon.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                icon
            )
        )
    }

    private fun showSubtitle(show: Boolean) {
        binding.createCookingSlotToggleViewSubTitle.show(show)
    }
}