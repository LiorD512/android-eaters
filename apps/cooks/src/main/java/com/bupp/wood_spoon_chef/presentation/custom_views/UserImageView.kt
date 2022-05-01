package com.bupp.wood_spoon_chef.presentation.custom_views

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.databinding.UserImageViewBinding

class UserImageView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr) {

    private var binding: UserImageViewBinding = UserImageViewBinding.inflate(LayoutInflater.from(context), this, true)

    private var type: Int = -1
    private var placeHolder: Drawable? = null


    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.UserImageView)
            if (a.hasValue(R.styleable.UserImageView_imageSize)) {
                val imageSize = a.getInteger(R.styleable.UserImageView_imageSize, Constants.BIG_IMAGE_SIZE)
                setImageSize(imageSize)
            }
            val placeHolderId = a.getResourceId(R.styleable.UserImageView_placeHolder, R.drawable.profile_pic_placeholder)
            placeHolder = ContextCompat.getDrawable(context, placeHolderId)
            a.recycle()
        }

        initUi()
    }




    fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    private var listener: UserImageViewListener? = null

    interface UserImageViewListener {
        fun onUserImageClick(type: Int)
    }

    fun setUserImageViewListener(listener: UserImageViewListener, type: Int) {
        this.listener = listener
        this.type = type
    }


    private fun initUi() {
        with(binding){
            placeHolder?.let{
                userImageViewPic.setImageDrawable(placeHolder)
            }
            userImageViewLayout.setOnClickListener {
                listener?.onUserImageClick(type)
            }
        }
    }

    private fun setImageSize(imageSize: Int) {
        when (imageSize) {
            Constants.SMALL_IMAGE_SIZE -> {
                val frame = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                frame.gravity = Gravity.CENTER_HORIZONTAL
                binding.userImageViewLayout.layoutParams = frame

                val imageLayout = LayoutParams(61.toPx(), 61.toPx())
                binding.userImageViewPic.layoutParams = imageLayout

            }
            Constants.BIG_IMAGE_SIZE -> {
                val frame = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                frame.gravity = Gravity.CENTER_HORIZONTAL
                binding.userImageViewLayout.layoutParams = frame

                val imageLayout = LayoutParams(99.toPx(), 99.toPx())
                binding.userImageViewPic.layoutParams = imageLayout

            }
            else -> {
                val frame = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                frame.gravity = Gravity.CENTER_HORIZONTAL
                binding.userImageViewLayout.layoutParams = frame

                val imageLayout = LayoutParams(86.toPx(), 86.toPx())
                imageLayout.gravity = Gravity.CENTER_HORIZONTAL
                binding.userImageViewPic.layoutParams = imageLayout

            }
        }
    }

    fun setImage(imageStr: String) {
        Glide.with(context).load(imageStr).apply(RequestOptions.circleCropTransform()).into(binding.userImageViewPic)
    }

    fun setImage(imageUri: Uri) {
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(binding.userImageViewPic)
    }


}