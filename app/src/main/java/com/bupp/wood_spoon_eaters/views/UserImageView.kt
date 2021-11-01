package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.UserImageViewBinding

class UserImageView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RelativeLayout(context, attrs, defStyleAttr) {

    private var binding: UserImageViewBinding = UserImageViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        initUi(attrs)
    }

    private fun initUi(attrs: AttributeSet? = null) {
        with(binding) {
            attrs?.let {
                val a = context.obtainStyledAttributes(attrs, R.styleable.UserImageView)
                val src = a.getResourceId(R.styleable.UserImageView_srcImage, 0)
                if (src != 0) {
                    setSrc(src)
                }
                a.recycle()
            }
        }
    }

    fun setImage(imageUri: String?) {
        imageUri?.let{
            Glide.with(context).load(imageUri).transform(CircleCrop()).into(binding.userThumbnail)
        }
    }

    private fun setSrc(drawableID: Int) {
        binding.userThumbnail.setImageDrawable(ContextCompat.getDrawable(context, drawableID))
    }
}