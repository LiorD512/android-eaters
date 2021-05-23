//package com.bupp.wood_spoon_eaters.custom_views
//
//import android.content.Context
//import android.content.res.Resources
//import android.graphics.drawable.Drawable
//import android.net.Uri
//import android.util.AttributeSet
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.widget.FrameLayout
//import android.widget.RelativeLayout
//import androidx.core.content.ContextCompat
//import com.bumptech.glide.Glide
//import com.bumptech.glide.request.RequestOptions
//import com.bupp.wood_spoon_eaters.R
//import com.bupp.wood_spoon_eaters.model.Cook
//import com.bupp.wood_spoon_eaters.model.Eater
//import com.bupp.wood_spoon_eaters.common.Constants
//import com.bumptech.glide.load.engine.DiskCacheStrategy
//import com.bumptech.glide.load.resource.bitmap.CenterCrop
//import com.bumptech.glide.load.resource.bitmap.CircleCrop
//import com.bumptech.glide.load.resource.bitmap.RoundedCorners
//import com.trading212.stickyheader.dpToPx
//
//
//class UserImageView : FrameLayout {
//
//    private var curEater: Eater? = null
//    private var curCook: Cook? = null
//    private var type: Int = -1
//    private var imageSize: Int = 0
//
//    private var isWithStroke: Boolean = false
//    private var isWithBkg: Boolean = false
//    private var isWithShadow: Boolean = false
//    private var placeHolder: Drawable? = null
//
//
//    fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
//
//    private var listener: UserImageViewListener? = null
//
//    interface UserImageViewListener {
//        fun onUserImageClick(cook: Cook?)
//    }
//
//    fun setUserImageViewListener(listener: UserImageViewListener) {
//        this.listener = listener
//    }
//
//    constructor(context: Context) : this(context, null)
//    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
//
//
//    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
//        LayoutInflater.from(context).inflate(R.layout.user_image_view, this, true)
//
//        if (attrs != null) {
//            val a = context.obtainStyledAttributes(attrs, R.styleable.UserImageView)
////            isWithBkg = a.getBoolean(R.styleable.UserImageView_isWithBkg, false)
//            imageSize = a.getInteger(R.styleable.UserImageView_imageSize, Constants.SMALL_IMAGE_SIZE)
//            placeHolder = a.getDrawable(R.styleable.UserImageView_placeHolder)
//            isWithStroke = a.getBoolean(R.styleable.UserImageView_isWithStroke, false)
//            isWithShadow = a.getBoolean(R.styleable.UserImageView_isWithShadow, false)
//            a.recycle()
//        }
//
//        cookImageView.setOnClickListener {
//            listener?.onUserImageClick(curCook)
//        }
//
//        initUi()
//    }
//
//    private fun initUi() {
//        if (placeHolder != null) {
//            cookImageView.setImageDrawable(placeHolder)
//        }
//        when (imageSize) {
//            Constants.HEADER_IMAGE_SIZE -> {
//                val bkgLayout = LayoutParams(40.toPx(), 40.toPx())
//                cookImageViewLayout.layoutParams = bkgLayout
//
//                val imageLayout = RelativeLayout.LayoutParams(39.toPx(), 39.toPx())
//                imageLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//                cookImageView.layoutParams = imageLayout
//            }
//            Constants.SMALL_IMAGE_SIZE -> {
//                val bkgLayout = LayoutParams(50.toPx(), 50.toPx())
//                cookImageViewLayout.layoutParams = bkgLayout
//
//                val imageLayout = RelativeLayout.LayoutParams(48.toPx(), 48.toPx())
//                imageLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//                cookImageView.layoutParams = imageLayout
//                if (isWithBkg)
//                    cookImageView.setPadding(10, 10, 10, 10)
//            }
//            Constants.BIG_IMAGE_SIZE -> {
//                val bkgLayout = LayoutParams(80.toPx(), 80.toPx())
//                cookImageViewLayout.layoutParams = bkgLayout
//
//                if (isWithBkg){
//                    cookImageView.setPadding(10, 10, 10, 10)
//                    val imageLayout = RelativeLayout.LayoutParams(76.toPx(), 76.toPx())
//                    imageLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//                    cookImageView.layoutParams = imageLayout
//                }else{
//                    cookImageView.setPadding(0,0,0,0)
//                    val imageLayout = RelativeLayout.LayoutParams(80.toPx(), 80.toPx())
//                    imageLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//                    cookImageView.layoutParams = imageLayout
//                }
//                if(isWithStroke){
//                    cookImageViewPlay.visibility = View.VISIBLE
//                    cookImageViewPlay.elevation = dpToPx(8F).toFloat()
//                }else{
//                    cookImageViewPlay.visibility = View.GONE
//                }
//            }
//            Constants.BIGGEST_IMAGE_SIZE -> {
//                val bkgLayout = LayoutParams(90.toPx(), 90.toPx())
//                cookImageViewLayout.layoutParams = bkgLayout
//
//                val imageLayout = RelativeLayout.LayoutParams(90.toPx(), 90.toPx())
//                imageLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
//                cookImageView.layoutParams = imageLayout
//            }
//            Constants.MANY_COOKS_VIEW -> {
//                val bkgLayout = LayoutParams(76.toPx(), 76.toPx())
//                cookImageViewLayout.layoutParams = bkgLayout
//
//                val imageLayout = RelativeLayout.LayoutParams(76.toPx(), 76.toPx())
//                imageLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//                cookImageView.setPadding(0,0,0,0)
//                cookImageView.layoutParams = imageLayout
//
//                isWithBkg = false
//            }
//        }
//
//        if(isWithStroke){
//            cookImageView.background = ContextCompat.getDrawable(context, R.drawable.blue_circle)
////            cookImageViewPlay.visibility = View.VISIBLE
//        }else{
//            cookImageView.background = null
//        }
//
//        if(isWithShadow){
//            cookImageViewLayoutCardView.elevation = dpToPx(8F).toFloat()
//        }else{
//            cookImageViewLayoutCardView.elevation = 0F
//        }
//
////        if (isWithBkg) {
////            cookImageViewBkg.visibility = View.VISIBLE
////        } else {
////            cookImageViewBkg.visibility = View.GONE
////        }
//    }
//
//    fun setImage(imageStr: String?) {
//        if (!imageStr.isNullOrEmpty()) {
//            loadSmallImage(imageStr)
////            Glide.with(context).load(imageStr).transform(CircleCrop()).into(cookImageView)
//        } else if (placeHolder != null) {
//            cookImageView.setImageDrawable(placeHolder)
//        }
//    }
//
//    fun setImage(imageUri: Uri) {
//        Glide.with(context).load(imageUri).transform(CircleCrop()).into(cookImageView)
//    }
//
//    fun setCookFromCooksView(cook: Cook) {
//        this.curCook = cook
////        Glide.with(context).load(cook.thumbnail).transform(CircleCrop()).into(cookImageView)
//        loadSmallImage(cook.thumbnail)
//    }
//
//    fun setUser(eater: Eater) {
//        this.curEater = eater
//        setImage(eater.thumbnail)
//        initUi()
//    }
//
//    fun setUser(cook: Cook) {
//        this.curCook = cook
//        setImage(cook.thumbnail)
//        if (cook.video.isNullOrEmpty()) {
//            isWithBkg = false
//            isWithStroke = false
//        } else {
//            isWithBkg = true
//            isWithStroke = true
//        }
//        initUi()
//    }
//
//    private fun loadSmallImage(imageUrl: String){
//        Log.d("wowUserImageView","loadSmallImage")
//        val smallThumbnail = imageUrl.replace("t_medium", "t_small")
//        Glide.with(context).load(smallThumbnail).transform(CircleCrop()).into(cookImageView)
//    }
//}