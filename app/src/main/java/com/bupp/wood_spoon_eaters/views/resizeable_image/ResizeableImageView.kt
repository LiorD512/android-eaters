package com.bupp.wood_spoon_eaters.views.resizeable_image

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.ResizeableImageViewBinding
import com.bupp.wood_spoon_eaters.model.CloudinaryTransformationsType
import kotlinx.android.synthetic.main.user_image_view.view.*

class ResizeableImageView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    val viewModel = ResizeableImageViewModel()

    var size: Int = -1
    var isRound: Boolean = false

    private var binding: ResizeableImageViewBinding = ResizeableImageViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
         initUi(attrs)
    }

    private fun initUi(attrs: AttributeSet?) {
        attrs?.let{

            with(binding){
                val attr = context.obtainStyledAttributes(attrs, R.styleable.ResizeableImageView)

                size = attr.getInt(R.styleable.ResizeableImageView_sizeType, -1)
                isRound = attr.getBoolean(R.styleable.ResizeableImageView_isRound, false)

                attr.recycle()

            }

        }
    }

    fun loadResizableImage(imgUrl: String?){
        imgUrl?.let{
            var finalUrl = imgUrl
            when(size){
                Constants.RESIZEABLE_IMAGE_SMALL -> {
                    val small = viewModel.getByType(CloudinaryTransformationsType.SMALL)
                    small?.let{
                        Log.d(TAG, "resizing to: $small")
                        finalUrl = finalUrl!!.replace("t_medium", small)
                    }
                }
                Constants.RESIZEABLE_IMAGE_MEDIUM -> {
                    val medium = viewModel.getByType(CloudinaryTransformationsType.MEDIUM)
                    medium?.let{
                        Log.d(TAG, "resizing to: $medium")
                        finalUrl = finalUrl!!.replace("t_medium", medium)
                    }
                }
                Constants.RESIZEABLE_IMAGE_LARGE -> {
                    val large = viewModel.getByType(CloudinaryTransformationsType.LARGE)
                    large?.let{
                        Log.d(TAG, "resizing to: $large")
                        finalUrl = finalUrl!!.replace("t_medium", large)
                    }
                }
            }
            if(isRound){
                Glide.with(context).load(finalUrl).transform(CircleCrop()).into(cookImageView)
            }else{
                Glide.with(context).load(finalUrl).into(binding.resizeableImageView)
            }
        }
    }

    companion object{
        const val TAG = "wowResizeableView"
    }


}
