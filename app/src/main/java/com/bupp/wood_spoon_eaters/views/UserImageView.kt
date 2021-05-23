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

    fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    private var binding: UserImageViewBinding = UserImageViewBinding.inflate(LayoutInflater.from(context), this, true)

    private var curEater: Eater? = null
    private var curCook: Cook? = null
    private var type: Int = -1
    private var imageSize: Int = 0

    private var isWithStroke: Boolean = false
    private var isWithBkg: Boolean = false
    private var isWithShadow: Boolean = false
    private var placeHolder: Drawable? = null

    init {
        initUi(attrs)
    }


    private var listener: UserImageViewListener? = null

    interface UserImageViewListener {
        fun onUserImageClick(cook: Cook?)
    }

    fun setUserImageViewListener(listener: UserImageViewListener) {
        this.listener = listener
    }

    private fun initUi(attrs: AttributeSet? = null) {
        with(binding){
            attrs?.let {

                with(binding) {

                    val a = context.obtainStyledAttributes(attrs, R.styleable.UserImageView)

                    imageSize = a.getInteger(R.styleable.UserImageView_imageSize, Constants.SMALL_IMAGE_SIZE)
                    placeHolder = a.getDrawable(R.styleable.UserImageView_placeHolder)
                    isWithStroke = a.getBoolean(R.styleable.UserImageView_isWithStroke, false)
                    isWithShadow = a.getBoolean(R.styleable.UserImageView_isWithShadow, false)
                    a.recycle()

                }
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

                    val imageLayout = RelativeLayout.LayoutParams(Utils.toPx(39), Utils.toPx(39))
                    imageLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    cookImageView.layoutParams = imageLayout
                }
                Constants.SMALL_IMAGE_SIZE -> {
                    val bkgLayout = LayoutParams(Utils.toPx(50), Utils.toPx(50))
                    cookImageViewLayout.layoutParams = bkgLayout

                    val imageLayout = RelativeLayout.LayoutParams(Utils.toPx(48), Utils.toPx(48))
                    imageLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    cookImageView.layoutParams = imageLayout
                    if (isWithBkg)
                        cookImageView.setPadding(10, 10, 10, 10)
                }
                Constants.BIG_IMAGE_SIZE -> {
                    val bkgLayout = LayoutParams(Utils.toPx(80), Utils.toPx(80))
                    cookImageViewLayout.layoutParams = bkgLayout

    //                val imageLayout = RelativeLayout.LayoutParams(Utils.toPx(65), Utils.toPx(65))
    //                imageLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
    //                cookImageView.layoutParams = imageLayout

                    if (isWithBkg){
                        val param = cookImageViewLayoutCardView.layoutParams as ViewGroup.MarginLayoutParams
                        param.setMargins(28,0,0,0)
                        cookImageViewLayoutCardView.layoutParams = param

                        cookImageView.setPadding(10, 10, 10, 10)

                        val imageLayout = RelativeLayout.LayoutParams(Utils.toPx(65), Utils.toPx(65))
                        imageLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                        cookImageView.layoutParams = imageLayout
                    }else{
                        val param = cookImageViewLayoutCardView.layoutParams as ViewGroup.MarginLayoutParams
                        param.setMargins(18,10,10,10)
                        cookImageViewLayoutCardView.layoutParams = param

                        val imageLayout = RelativeLayout.LayoutParams(Utils.toPx(75), Utils.toPx(75))
                        imageLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                        cookImageView.layoutParams = imageLayout
    //                    cookImageView.setPadding(10, 10, 10, 10)

                    }
                    if(isWithStroke){
                        cookImageViewPlay.visibility = View.VISIBLE
                        cookImageViewPlay.elevation = dpToPx(8F).toFloat()
                    }else{
                        cookImageViewPlay.visibility = View.GONE
                    }
                }
                Constants.BIGGEST_IMAGE_SIZE -> {
                    val bkgLayout = LayoutParams(Utils.toPx(90), Utils.toPx(90))
                    cookImageViewLayout.layoutParams = bkgLayout

                    val imageLayout = RelativeLayout.LayoutParams(Utils.toPx(90), Utils.toPx(90))
                    imageLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                    cookImageView.layoutParams = imageLayout
                }
                Constants.MANY_COOKS_VIEW -> {
                    val bkgLayout = LayoutParams(Utils.toPx(76), Utils.toPx(76))
                    cookImageViewLayout.layoutParams = bkgLayout

                    val imageLayout = RelativeLayout.LayoutParams(Utils.toPx(76), Utils.toPx(76))
                    imageLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                    cookImageView.setPadding(0,0,0,0)
                    cookImageView.layoutParams = imageLayout

                    isWithBkg = false
                }
            }

            if(isWithStroke){
                cookImageView.background = ContextCompat.getDrawable(context, R.drawable.blue_circle)
    //            cookImageViewPlay.visibility = View.VISIBLE
            }else{
                cookImageView.background = null
            }

            if(isWithShadow){
                cookImageViewLayoutCardView.elevation = dpToPx(8F).toFloat()
            }else{
                cookImageViewLayoutCardView.elevation = 0F
            }
        }
    }


    fun setImage(imageStr: String?) {
        if (!imageStr.isNullOrEmpty()) {
            loadSmallImage(imageStr)
//            Glide.with(context).load(imageStr).transform(CircleCrop()).into(cookImageView)
        } else if (placeHolder != null) {
            binding.cookImageView.setImageDrawable(placeHolder)
        }
    }

    fun setImage(imageUri: Uri) {
        Glide.with(context).load(imageUri).transform(CircleCrop()).into(binding.cookImageView)
    }

    fun setCookFromCooksView(cook: Cook) {
        this.curCook = cook
//        Glide.with(context).load(cook.thumbnail).transform(CircleCrop()).into(cookImageView)
        loadSmallImage(cook.thumbnail)
    }

    fun setUser(eater: Eater) {
        this.curEater = eater
        setImage(eater.thumbnail)
        initUi()
    }

    fun setUser(cook: Cook) {
        this.curCook = cook
        setImage(cook.thumbnail)
        if (cook.video.isNullOrEmpty()) {
            isWithBkg = false
            isWithStroke = false
        } else {
            isWithBkg = true
            isWithStroke = true
        }
        initUi()
    }

    private fun loadSmallImage(imageUrl: String){
        Log.d("wowUserImageView","loadSmallImage")
        val smallThumbnail = imageUrl.replace("t_medium", "t_small")
        Glide.with(context).load(smallThumbnail).transform(CircleCrop()).into(binding.cookImageView)
    }
}