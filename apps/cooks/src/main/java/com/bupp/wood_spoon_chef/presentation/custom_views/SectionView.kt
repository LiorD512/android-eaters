package com.bupp.wood_spoon_chef.presentation.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.children
import com.bupp.wood_spoon_chef.databinding.SectionViewBinding

class SectionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var binding: SectionViewBinding? = null

    init {
        binding = SectionViewBinding.inflate(
            LayoutInflater.from(context), this
        )
    }

     val children = binding?.createCookingSlotItemViewLayoutContent?.children ?: emptySequence()

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (binding == null){
            super.addView(child, index, params)
        }else{
            binding?.createCookingSlotItemViewLayoutContent?.addView(child, index, params)
        }
    }
}