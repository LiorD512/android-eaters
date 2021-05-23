package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_info

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.bupp.wood_spoon_eaters.databinding.DishMediaItemBinding
import com.bupp.wood_spoon_eaters.model.MediaList

class DishMediaAdapter(val listener: DishMediaAdapterListener): ListAdapter<MediaList, RecyclerView.ViewHolder>(DiffCallback()) {


    interface DishMediaAdapterListener{
        fun onPlayClick(url: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DishMediaItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaViewHolder(binding)
//        return MediaViewHolder(
//            LayoutInflater.from(parent.context).inflate(
//                R.layout.dish_media_item,
//                parent,
//                false
//            )
//        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val media = getItem(position)
        holder as MediaViewHolder
        if(media.isImage){
            Glide.with(holder.itemView.context).load(media.media).into(holder.img)
        }else{
            Glide.with(holder.itemView.context).asBitmap().load(media.media).into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    holder.img.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
            holder.playBtn.visibility = View.VISIBLE
            holder.playBtn.setOnClickListener { listener.onPlayClick(media.media) }
        }
    }

    class MediaViewHolder constructor(itemView: DishMediaItemBinding) : RecyclerView.ViewHolder(itemView.root) {
        val img: ImageView = itemView.mediaItemImg
        val playBtn: ImageView = itemView.mediaItemPlay
    }


    class DiffCallback : DiffUtil.ItemCallback<MediaList>() {

        override fun areItemsTheSame(oldItem: MediaList, newItem: MediaList): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MediaList, newItem: MediaList): Boolean {
            return oldItem == newItem
        }
    }

}

