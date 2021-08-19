package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.common.Constants
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bupp.wood_spoon_eaters.databinding.TitleBodyViewBinding
import com.bupp.wood_spoon_eaters.databinding.UserImageViewBinding
import com.bupp.wood_spoon_eaters.utils.Utils
import com.trading212.stickyheader.dpToPx


class UserImageView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RelativeLayout(context, attrs, defStyleAttr){

    private var binding: UserImageViewBinding = UserImageViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun setImage(imageUri: String) {
        Glide.with(context).load(imageUri).transform(CircleCrop()).into(binding.userThumbnail)
    }

}