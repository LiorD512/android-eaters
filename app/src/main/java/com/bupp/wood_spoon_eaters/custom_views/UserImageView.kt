package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.user_image_view.view.*

class UserImageView : FrameLayout {

    private var type: Int = -1

    fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    private var listener: UserImageViewListener? = null

    interface UserImageViewListener {
        fun onUserImageClick(type: Int)
    }

    fun setUserImageViewListener(listener: UserImageViewListener, type: Int) {
        this.listener = listener
        this.type = type
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.user_image_view, this, true)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.UserImageView)
            if (a.hasValue(R.styleable.UserImageView_imageSize)) {
                val imageSize = a.getInteger(R.styleable.UserImageView_imageSize, Constants.BIG_IMAGE_SIZE)
                setImageSize(imageSize)
            }
            a.recycle()
        }

        initUi()
    }

    private fun initUi() {
        cookImageViewLayout.setOnClickListener {
            listener?.onUserImageClick(type)
        }
    }

    private fun setImageSize(imageSize: Int) {
        when (imageSize) {
            Constants.SMALL_IMAGE_SIZE -> {
                val frame = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                frame.gravity = Gravity.CENTER_HORIZONTAL
                cookImageViewLayout.layoutParams = frame

                val imageLayout = LayoutParams(48.toPx(), 48.toPx())
                cookImageView.layoutParams = imageLayout
            }
            else -> {
                val frame = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                frame.gravity = Gravity.CENTER_HORIZONTAL
                cookImageViewLayout.layoutParams = frame

                val imageLayout = LayoutParams(76.toPx(), 76.toPx())
                imageLayout.gravity = Gravity.CENTER_HORIZONTAL
                cookImageView.layoutParams = imageLayout
            }
        }
    }

    fun setImage(imageStr: String) {
        Glide.with(context).load(imageStr).apply(RequestOptions.circleCropTransform()).into(cookImageView)
    }

    fun setImage(imageUri: Uri) {
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(cookImageView)
    }
}