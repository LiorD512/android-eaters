package com.bupp.wood_spoon_chef.presentation.features.main.single_dish

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.bupp.wood_spoon_chef.databinding.DishMediaItemBinding
import com.bupp.wood_spoon_chef.data.remote.model.MediaList

class DishMediaAdapter(val listener: DishMediaAdapterListener) : RecyclerView.Adapter<MediaViewHolder>() {

    interface DishMediaAdapterListener{
        fun onPlayClick(url: String)
    }
    var list: List<MediaList> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {


        val binding = DishMediaItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val media = list[position]

        if(media.isImage){
            Glide.with(holder.itemView.context).load(media.media).centerCrop().into(holder.img)
        }else{
            Glide.with(holder.itemView.context).asBitmap().load(media.media).centerCrop().into(object : CustomTarget<Bitmap>() {
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

    fun setItem(list: List<MediaList>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size
}


class MediaViewHolder constructor(itemView: DishMediaItemBinding) : RecyclerView.ViewHolder(itemView.root) {
    val img: ImageView = itemView.mediaItemImg
    val playBtn: ImageView = itemView.mediaItemPlay

}