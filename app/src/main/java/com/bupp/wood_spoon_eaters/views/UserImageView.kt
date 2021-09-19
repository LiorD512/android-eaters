package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bupp.wood_spoon_eaters.databinding.UserImageViewBinding


class UserImageView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RelativeLayout(context, attrs, defStyleAttr){

    private var binding: UserImageViewBinding = UserImageViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun setImage(imageUri: String) {
        Glide.with(context).load(imageUri).transform(CircleCrop()).into(binding.userThumbnail)
    }

}