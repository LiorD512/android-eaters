package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.ResizableTagsBinding


class ResizableTagsView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var binding: ResizableTagsBinding = ResizableTagsBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        initUi(attrs)

    }

    private fun initUi(attrs: AttributeSet?) {
        attrs?.let{
            val attr = context.obtainStyledAttributes(attrs, R.styleable.ResizableTagsView)
            val tagType = attr.getInt(R.styleable.ResizableTagsView_tagType, 1)
            updateUiByType(tagType)
        }
        binding.tagFirst.isVisible = false
        binding.tagSecond.isVisible = false
    }

    private fun updateUiByType(tagType: Int) {
        when(tagType){
           Constants.TAG_VIEW_FEED -> {
               binding.tagFirst.setBackgroundResource(R.drawable.white_90_rect)
               binding.tagSecond.setBackgroundResource(R.drawable.white_90_rect)
               TextViewCompat.setTextAppearance(binding.tagFirst, R.style.LatoReg15DarkGrey)
               TextViewCompat.setTextAppearance(binding.tagSecond, R.style.LatoReg15DarkGrey)
           }
           Constants.TAG_VIEW_DISH -> {
               //default ui state
           }
        }
    }

    fun setTags(tags:List<String>?) {
        tags?.let{
            with(binding) {
                val firstTag = it.getOrNull(0) ?: ""
                val secondTag = it.getOrNull(1) ?: ""
                binding.tagFirst.text = firstTag
                binding.tagFirst.isVisible = firstTag.isNotEmpty()
                binding.tagSecond.text = secondTag
                binding.tagSecond.isVisible = secondTag.isNotEmpty()
            }
        }
    }
}
