package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.bupp.wood_spoon_eaters.databinding.ResizableTagsBinding


class ResizableTagsView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var binding: ResizableTagsBinding = ResizableTagsBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.tagFirst.isVisible = false
        binding.tagSecond.isVisible = false
    }

    fun setTags(tags:List<String>) {
        with(binding) {
            val firstTag = tags.getOrNull(0) ?: ""
            val secondTag = tags.getOrNull(1) ?: ""
            binding.tagFirst.text = firstTag
            binding.tagFirst.isVisible = firstTag.isNotEmpty()
            binding.tagSecond.text = secondTag
            binding.tagSecond.isVisible = secondTag.isNotEmpty()
        }
    }
}
