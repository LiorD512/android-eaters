package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.UserImageVideoViewBinding
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.utils.Utils


class UserImageVideoView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RelativeLayout(context, attrs, defStyleAttr) {

    fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    private var binding: UserImageVideoViewBinding = UserImageVideoViewBinding.inflate(LayoutInflater.from(context), this, true)

    private var curEater: Eater? = null
    private var curCook: Cook? = null
    private var imageSize: Int = 0

    private var isWithStroke: Boolean = false
    private var isWithBkg: Boolean = false
    private var isWithShadow: Boolean = false
    private var placeHolder: Drawable? = null

    init {
        initUi(attrs)
    }


    private var listener: UserImageVideoViewListener? = null

    interface UserImageVideoViewListener {
        fun onUserImageClick(cook: Cook?)
    }

    fun setUserImageVideoViewListener(listener: UserImageVideoViewListener) {
        this.listener = listener
    }

    private fun initUi(attrs: AttributeSet? = null) {
        with(binding) {
            attrs?.let {

                val a = context.obtainStyledAttributes(attrs, R.styleable.UserImageVideoView)

                imageSize = a.getInteger(R.styleable.UserImageVideoView_imageSize, Constants.SMALL_IMAGE_SIZE)
                placeHolder = a.getDrawable(R.styleable.UserImageVideoView_placeHolder)
                isWithStroke = a.getBoolean(R.styleable.UserImageVideoView_isWithStroke, false)
                isWithShadow = a.getBoolean(R.styleable.UserImageVideoView_isWithShadow, false)
                a.recycle()
            }

            if (placeHolder != null) {
                cookImageView.setImageDrawable(placeHolder)
            }
            cookImageView.setOnClickListener {
                listener?.onUserImageClick(curCook)
            }

            when (imageSize) {
                Constants.HEADER_IMAGE_SIZE -> {
                    val bkgLayout = LayoutParams(Utils.toPx(40), Utils.toPx(40))
                    cookImageViewLayout.layoutParams = bkgLayout

                    val imageLayout = LayoutParams(Utils.toPx(39), Utils.toPx(39))
                    imageLayout.addRule(CENTER_IN_PARENT, TRUE)
                    cookImageView.layoutParams = imageLayout
                }
                Constants.SMALL_IMAGE_SIZE -> {
                    val bkgLayout = LayoutParams(Utils.toPx(50), Utils.toPx(50))
                    cookImageViewLayout.layoutParams = bkgLayout

                    val imageLayout = LayoutParams(Utils.toPx(48), Utils.toPx(48))
                    imageLayout.addRule(CENTER_IN_PARENT, TRUE)
                    cookImageView.layoutParams = imageLayout
                    if (isWithBkg)
                        cookImageView.setPadding(10, 10, 10, 10)
                }
                Constants.BIG_IMAGE_SIZE -> {
                    val bkgLayout = LayoutParams(Utils.toPx(85), Utils.toPx(85))
                    cookImageViewLayout.layoutParams = bkgLayout

                    if (isWithBkg) {
                        val param = cookImageViewLayoutCardView.layoutParams as MarginLayoutParams
                        param.setMargins(28, 0, 0, 0)
                        cookImageViewLayoutCardView.layoutParams = param

                        cookImageView.setPadding(10, 10, 10, 10)

                        val imageLayout = LayoutParams(Utils.toPx(65), Utils.toPx(65))
                        imageLayout.addRule(CENTER_IN_PARENT, TRUE)
                        cookImageView.layoutParams = imageLayout
                    } else {
                        val param = cookImageViewLayoutCardView.layoutParams as MarginLayoutParams
                        param.setMargins(18, 10, 10, 10)
                        cookImageViewLayoutCardView.layoutParams = param

                        val imageLayout = LayoutParams(Utils.toPx(75), Utils.toPx(75))
                        imageLayout.addRule(CENTER_IN_PARENT, TRUE)
                        cookImageView.layoutParams = imageLayout

                    }
                    if (isWithStroke) {
                        cookImageViewPlay.visibility = View.VISIBLE
                        cookImageViewPlay.elevation = Utils.toPx(8).toFloat()
                    } else {
                        cookImageViewPlay.visibility = View.GONE
                    }
                }
                Constants.BIGGEST_IMAGE_SIZE -> {
                    val bkgLayout = LayoutParams(Utils.toPx(90), Utils.toPx(90))
                    cookImageViewLayout.layoutParams = bkgLayout

                    val imageLayout = LayoutParams(Utils.toPx(90), Utils.toPx(90))
                    imageLayout.addRule(CENTER_IN_PARENT, TRUE)
                    cookImageView.layoutParams = imageLayout
                }
                Constants.MANY_COOKS_VIEW -> {
                    val bkgLayout = LayoutParams(Utils.toPx(76), Utils.toPx(76))
                    cookImageViewLayout.layoutParams = bkgLayout

                    val imageLayout = LayoutParams(Utils.toPx(76), Utils.toPx(76))
                    imageLayout.addRule(CENTER_IN_PARENT, TRUE)
                    cookImageView.setPadding(0, 0, 0, 0)
                    cookImageView.layoutParams = imageLayout

                    isWithBkg = false
                }
            }

            if (isWithStroke) {
                cookImageView.background = ContextCompat.getDrawable(context, R.drawable.blue_circle)
            } else {
                cookImageView.background = null
            }

            if (isWithShadow) {
                cookImageViewLayoutCardView.elevation = Utils.toPx(8).toFloat()
            } else {
                cookImageViewLayoutCardView.elevation = 0F
            }
        }
    }


    fun setImage(imageStr: String?) {
        if (!imageStr.isNullOrEmpty()) {
            loadSmallImage(imageStr)
        } else if (placeHolder != null) {
            binding.cookImageView.setImageDrawable(placeHolder)
        }
    }

    fun setImage(imageUri: Uri) {
        Glide.with(context).load(imageUri).transform(CircleCrop()).into(binding.cookImageView)
    }

    fun setUser(eater: Eater) {
        this.curEater = eater
        setImage(eater.thumbnail)
        initUi()
    }


    private fun loadSmallImage(imageUrl: String) {
        Log.d("wowUserImageVideoView", "loadSmallImage")
        val smallThumbnail = imageUrl.replace("t_medium", "t_small")
        Glide.with(context).load(smallThumbnail).transform(CircleCrop()).into(binding.cookImageView)
    }
}