package com.bupp.wood_spoon_chef.presentation.features.new_dish.fragments.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bupp.wood_spoon_chef.databinding.NewDishMediaItemPhotoBinding
import com.bupp.wood_spoon_chef.databinding.NewDishMediaItemVideoBinding
import com.bupp.wood_spoon_chef.presentation.features.new_dish.NewDishViewModel


data class NewDishMedia(
        val viewType: Int,
        var uri: Uri?
)

class NewDishMediaAdapter(val context: Context, val listener: NewDishMediaAdapterListener) :
        ListAdapter<NewDishMedia, RecyclerView.ViewHolder>(NewDishMediaDiffCallback()) {


    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return return when (viewType) {
            VIEW_TYPE_PHOTO -> {
                val binding = NewDishMediaItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PhotoViewHolder(binding)

            }
            else -> { //VIEW_TYPE_VIDEO
                val binding = NewDishMediaItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                VideoViewHolder(binding)

            }
        }
    }

    interface NewDishMediaAdapterListener {
        fun onAddMediaClick(type: NewDishViewModel.MediaUploadArgs) {}
        fun onPlayMediaClick(uri: Uri?) {}
        fun onMediaDelete(uri: Uri?) {}
        fun onVideoDelete() {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (item.viewType) {
            VIEW_TYPE_PHOTO -> {
                val image = item.uri
                (holder as PhotoViewHolder).bind(context, image, listener, position)
            }
            VIEW_TYPE_VIDEO -> {
                val image = item.uri
                (holder as VideoViewHolder).bind(context, image, listener)
            }
        }
    }

    class PhotoViewHolder(view: NewDishMediaItemPhotoBinding) : RecyclerView.ViewHolder(view.root) {
        private val layout: FrameLayout = view.newDishMediaPhotoLayout
        private val img: ImageView = view.newDishMediaPhoto
        private val deleteBtn: ImageButton = view.newDishMediaRemoveBtn
        private val btn: TextView = view.newDishMediaPhotoBtn

        fun bind(context: Context, image: Uri?, listener: NewDishMediaAdapterListener, position: Int) {
            Glide.with(context).load("").transition(DrawableTransitionOptions.withCrossFade()).into(img)
            image?.let {
                Glide.with(context).load(it).transform(CenterCrop(), RoundedCorners(24)).transition(DrawableTransitionOptions.withCrossFade()).into(img)
                deleteBtn.visibility = View.VISIBLE
            }
            btn.setOnClickListener {
                onItemClick(position, listener)
            }
            layout.setOnClickListener {
                onItemClick(position, listener)
            }
            deleteBtn.setOnClickListener {
                listener.onMediaDelete(image)
            }
        }

        private fun onItemClick(position: Int, listener: NewDishMediaAdapterListener) {
            if (position == 0) {
                listener.onAddMediaClick(NewDishViewModel.MediaUploadArgs.IMG_2)
            } else {
                listener.onAddMediaClick(NewDishViewModel.MediaUploadArgs.IMG_3)
            }
        }
    }

    class VideoViewHolder(view: NewDishMediaItemVideoBinding) : RecyclerView.ViewHolder(view.root) {
        private val layout: FrameLayout = view.newDishMediaVideoLayout
        private val img: ImageView = view.newDishMediaVideo
        private val btn: TextView = view.newDishMediaVideoBtn
        private val deleteBtn: ImageButton = view.newDishVideoRemoveBtn
        private val play: ImageButton = view.newDishMediaVideoPlay

        fun bind(context: Context, image: Uri?, listener: NewDishMediaAdapterListener) {
            if(image == null){
                play.visibility = View.GONE
                deleteBtn.visibility = View.GONE
                img.setImageResource(0)
            }else{
                image.let{
                    Glide.with(context)
                        .load(image)
                        .transform(CenterCrop(), RoundedCorners(24))
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(img)
                    play.visibility = View.VISIBLE
                    deleteBtn.visibility = View.VISIBLE
                    deleteBtn.setOnClickListener {
                        listener.onVideoDelete()
                    }
                    play.setOnClickListener {
                        listener.onPlayMediaClick(image)
                    }
                }
            }
            layout.setOnClickListener {
                listener.onAddMediaClick(NewDishViewModel.MediaUploadArgs.VIDEO)
            }
            btn.setOnClickListener {
                listener.onAddMediaClick(NewDishViewModel.MediaUploadArgs.VIDEO)
            }
        }

    }

    class NewDishMediaDiffCallback : DiffUtil.ItemCallback<NewDishMedia>() {
        override fun areItemsTheSame(oldItem: NewDishMedia, newItem: NewDishMedia): Boolean {
            return false //keep it false so items will be always refreshing on any content change.
        }

        override fun areContentsTheSame(oldItem: NewDishMedia, newItem: NewDishMedia): Boolean {
            return oldItem == newItem
        }
    }


    companion object {
        const val VIEW_TYPE_PHOTO = 1
        const val VIEW_TYPE_VIDEO = 2
    }


}


