package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_info

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
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.MediaList
import kotlinx.android.synthetic.main.dish_media_item.view.*

class DishMediaAdapter(val listener: DishMediaAdapterListener) : RecyclerView.Adapter<MediaViewHolder>() {

    interface DishMediaAdapterListener{
        fun onPlayClick(url: String)
    }
    var list: List<MediaList> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        return MediaViewHolder(parent)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val media = list[position]

        if(media.isImage){
            Glide.with(holder.context).load(media.media).into(holder.img)
        }else{
            Glide.with(holder.context).asBitmap().load(media.media).into(object : CustomTarget<Bitmap>() {
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


class MediaViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    constructor(parent: ViewGroup) : this(LayoutInflater.from(parent.context).inflate(R.layout.dish_media_item, parent, false))
    val context = itemView.context
    val img: ImageView = itemView.mediaItemImg
    val playBtn: ImageView = itemView.mediaItemPlay

}