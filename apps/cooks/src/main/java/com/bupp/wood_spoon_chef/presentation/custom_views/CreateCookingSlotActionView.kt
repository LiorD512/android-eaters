package com.bupp.wood_spoon_chef.presentation.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.CreateCookingSlotActionViewBinding
import com.bupp.wood_spoon_chef.utils.extensions.show

class CreateCookingSlotActionView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private val binding: CreateCookingSlotActionViewBinding =
        CreateCookingSlotActionViewBinding.inflate(
            LayoutInflater.from(context), this, true
        )


    init {
        initAttrs(attrs)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        if (attrs != null) {
            val attrSet =
                context.obtainStyledAttributes(attrs, R.styleable.CreateCookingSlotActionView)

            if (attrSet.hasValue(R.styleable.CreateCookingSlotActionView_title)) {
                val title = attrSet.getString(R.styleable.CreateCookingSlotActionView_title)
                setTitle(title)
            }

            if (attrSet.hasValue(R.styleable.CreateCookingSlotActionView_subtitle)) {
                val title = attrSet.getString(R.styleable.CreateCookingSlotActionView_subtitle)
                setSubtitle(title)
            }

            if (attrSet.hasValue(R.styleable.CreateCookingSlotActionView_mainIcon)) {
                val icon = attrSet.getResourceId(
                    R.styleable.CreateCookingSlotActionView_mainIcon,
                    0
                )
                setIcon(icon)
            }

            if (attrSet.hasValue(R.styleable.CreateCookingSlotActionView_setSubtitleAllCaps)) {
                val allCaps =
                    attrSet.getBoolean(
                        R.styleable.CreateCookingSlotActionView_setSubtitleAllCaps,
                        false
                    )
                setSubtitleAllCaps(allCaps)
            }

            if (attrSet.hasValue(R.styleable.CreateCookingSlotActionView_showSecondaryIcon)) {
                val show =
                    attrSet.getBoolean(
                        R.styleable.CreateCookingSlotActionView_showSecondaryIcon,
                        false
                    )
                showSecondaryIcon(show)
            }

            showSubtitle()
            setActionButtonTitle()

            attrSet.recycle()
        }
    }

    private fun setTitle(title: String?) {
        binding.createCookingSlotActionViewTitle.text = title
    }

    fun setSubtitle(subtitle: String?) {
        binding.createCookingSlotActionViewSubTitle.text = subtitle
        showSubtitle()
        setActionButtonTitle()
    }

    private fun setIcon(icon: Int) {
        binding.createCookingSlotActionViewMainIcon.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                icon
            )
        )
    }

    private fun showSubtitle() {
        binding.createCookingSlotActionViewSubTitle.show(binding.createCookingSlotActionViewSubTitle.text.isNotEmpty())
    }

    private fun showSecondaryIcon(show: Boolean) {
        binding.createCookingSlotActionViewSecondaryIcon.show(show)
    }

    fun setAddClickListener(clickListener: () -> Unit) {
        binding.createCookingSlotActionViewAddBtn.setOnClickListener { clickListener() }
    }

    fun setOnSecondaryIconClickListener(clickListener: () -> Unit) {
        binding.createCookingSlotActionViewSecondaryIcon.setOnClickListener { clickListener() }
    }

    private fun setSubtitleAllCaps(allCaps: Boolean) {
        binding.createCookingSlotActionViewSubTitle.isAllCaps = allCaps
    }

    private fun setActionButtonTitle() {
        if (binding.createCookingSlotActionViewSubTitle.text.isNullOrEmpty()) {
            binding.createCookingSlotActionViewAddBtn.setTitle("Add")
        } else {
            binding.createCookingSlotActionViewAddBtn.setTitle("Change")
        }
    }
}