package com.bupp.wood_spoon_chef.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.BottomSheetHeaderViewBinding


class BottomSheetHeaderView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr){

    private var binding: BottomSheetHeaderViewBinding = BottomSheetHeaderViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        initUi(attrs)
    }

    private fun initUi(attrs: AttributeSet?) {
        attrs?.let {
            val attr = context.obtainStyledAttributes(attrs, R.styleable.BottomSheetHeaderView)

            val title = attr.getString(R.styleable.BottomSheetHeaderView_title)
            title?.let { setTitle(it) }

            attr.recycle()
        }
    }

    fun setTitle(title: String){
        binding.headerHeader.text = title
    }

    fun setOnIconClickListener(function: () -> Unit) {
        binding.headerExitBtn.setOnClickListener{
            function.invoke()
        }

    }


    companion object {
        const val TAG = "wowTitleBodyView"
    }


}
