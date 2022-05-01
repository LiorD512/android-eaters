package com.bupp.wood_spoon_chef.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.TitleBodyViewBinding


class TitleBodyView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr){

    private var binding: TitleBodyViewBinding = TitleBodyViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        initUi(attrs)
    }

    private fun initUi(attrs: AttributeSet?) {
        attrs?.let {

            with(binding) {
                val attr = context.obtainStyledAttributes(attrs, R.styleable.TitleBodyView)

                val title = attr.getString(R.styleable.TitleBodyView_title)
                title?.let { titleBodyTitle.text = it }

                val body = attr.getString(R.styleable.TitleBodyView_body)
                body?.let { titleBodyBody.text = it }

                val showhDivider = attr.getBoolean(R.styleable.TitleBodyView_shouldShowDivider, true)
                if(!showhDivider){
                    titleBodyDivider.visibility = View.GONE
                }

                attr.recycle()


            }
        }
    }

    fun setBody(body: String){
        binding.titleBodyBody.text = body
    }

    fun setTitle(title: String){
        binding.titleBodyTitle.text = title
    }


    companion object {
        const val TAG = "wowTitleBodyView"
    }


}
