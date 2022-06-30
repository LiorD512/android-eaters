package com.bupp.wood_spoon_chef.presentation.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.CreateCookingSlotOptionViewBinding
import com.bupp.wood_spoon_chef.utils.extensions.show

class CreateCookingSlotOptionView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private val binding: CreateCookingSlotOptionViewBinding =
        CreateCookingSlotOptionViewBinding.inflate(
            LayoutInflater.from(context), this, true
        )

    init {
        initAttrs(attrs)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        if (attrs != null) {
            val attrSet =
                context.obtainStyledAttributes(attrs, R.styleable.CreateCookingSlotOptionView)

            if (attrSet.hasValue(R.styleable.CreateCookingSlotOptionView_title)) {
                val title = attrSet.getString(R.styleable.CreateCookingSlotOptionView_title)
                setTitle(title)
            }

            if (attrSet.hasValue(R.styleable.CreateCookingSlotOptionView_subtitle)) {
                val title = attrSet.getString(R.styleable.CreateCookingSlotOptionView_subtitle)
                setSubtitle(title)
            }

            if (attrSet.hasValue(R.styleable.CreateCookingSlotOptionView_mainIcon)) {
                val icon = attrSet.getResourceId(
                    R.styleable.CreateCookingSlotOptionView_mainIcon,
                    0
                )
                setIcon(icon)
            }

            showSubtitle()

            attrSet.recycle()
        }
    }

    private fun setTitle(title: String?) {
        binding.createCookingSlotOptionViewTitle.text = title
    }

    fun setSubtitle(subtitle: String?) {
        binding.createCookingSlotOptionViewSubTitle.text = subtitle
        showSubtitle()
    }

    private fun setIcon(icon: Int) {
        binding.createCookingSlotOptionViewMainIcon.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                icon
            )
        )
    }

    private fun showSubtitle() {
        binding.createCookingSlotOptionViewSubTitle.show(binding.createCookingSlotOptionViewSubTitle.text.isNotEmpty())
    }

    fun setForwardBtnClickListener(clickListener: () -> Unit) {
        binding.createCookingSlotOptionViewForwardBtn.setOnClickListener { clickListener() }
    }
}
