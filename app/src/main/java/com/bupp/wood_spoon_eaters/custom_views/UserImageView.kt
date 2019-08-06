package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.user_image_view.view.*


class UserImageView : FrameLayout {

    private var type: Int = -1
    private var imageSize: Int = 0

    private var isWithBkg: Boolean = false
    private var placeHolder: Drawable? = null


    fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    private var listener: UserImageViewListener? = null

    interface UserImageViewListener {
        fun onUserImageClick()
    }

    fun setUserImageViewListener(listener: UserImageViewListener) {
        this.listener = listener
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)


    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.user_image_view, this, true)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.UserImageView)
//            isWithBkg = a.getBoolean(R.styleable.UserImageView_isWithBkg, false)
            imageSize = a.getInteger(R.styleable.UserImageView_imageSize, Constants.SMALL_IMAGE_SIZE)
            placeHolder = a.getDrawable(R.styleable.UserImageView_placeHolder)
            a.recycle()
        }

        cookImageView.setOnClickListener {
            listener?.onUserImageClick()
        }

        initUi()
    }

    private fun initUi() {
        if (placeHolder != null) {
            cookImageView.setImageDrawable(placeHolder)
        }
        when (imageSize) {
            Constants.HEADER_IMAGE_SIZE -> {
                val bkgLayout = LayoutParams(40.toPx(), 40.toPx())
                cookImageViewLayout.layoutParams = bkgLayout

                val imageLayout = RelativeLayout.LayoutParams(39.toPx(), 39.toPx())
                imageLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                cookImageView.layoutParams = imageLayout
            }
            Constants.SMALL_IMAGE_SIZE -> {
                val bkgLayout = LayoutParams(50.toPx(), 50.toPx())
                cookImageViewLayout.layoutParams = bkgLayout

                val imageLayout = RelativeLayout.LayoutParams(48.toPx(), 48.toPx())
                imageLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                cookImageView.layoutParams = imageLayout
                if (isWithBkg)
                    cookImageView.setPadding(10, 10, 10, 10)
            }
            Constants.BIG_IMAGE_SIZE -> {
                val bkgLayout = LayoutParams(80.toPx(), 80.toPx())
                cookImageViewLayout.layoutParams = bkgLayout

                val imageLayout = RelativeLayout.LayoutParams(76.toPx(), 76.toPx())
                imageLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                cookImageView.layoutParams = imageLayout
                if (isWithBkg)
                    cookImageView.setPadding(10, 10, 10, 10)
            }
            Constants.BIGGEST_IMAGE_SIZE -> {
                val bkgLayout = LayoutParams(90.toPx(), 90.toPx())
                cookImageViewLayout.layoutParams = bkgLayout

                val imageLayout = RelativeLayout.LayoutParams(86.toPx(), 86.toPx())
                imageLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                cookImageView.layoutParams = imageLayout
            }
        }

        if (isWithBkg) {
            cookImageViewBkg.visibility = View.VISIBLE
        } else {
            cookImageViewBkg.visibility = View.GONE
        }
    }

    fun setImage(imageStr: String?) {
        if (!imageStr.isNullOrEmpty()) {
            Glide.with(context).load(imageStr).apply(RequestOptions.circleCropTransform()).into(cookImageView)
        } else if (placeHolder != null) {
            cookImageView.setImageDrawable(placeHolder)
        }
    }

    fun setImage(imageUri: Uri) {
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(cookImageView)
    }


    fun setUser(cook: Cook) {
        setImage(cook.thumbnail)
        if (!cook.video.isNullOrEmpty()) {
            isWithBkg = true
        } else {
            isWithBkg = false
        }
        initUi()
    }
}