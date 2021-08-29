package com.bupp.wood_spoon_eaters.custom_views.many_cooks_view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.ManyCooksViewItemBinding
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.views.UserImageVideoView

class ManyCooksViewAdapter(val context: Context, val cooksList: ArrayList<Cook>, val listener: ManyCooksViewListener): RecyclerView.Adapter<ManyCooksViewAdapter.ViewHolder>(),
    UserImageVideoView.UserImageViewListener {


    interface ManyCooksViewListener{
        fun onCookViewClick(selected: Cook)
    }

    class ViewHolder (view: ManyCooksViewItemBinding) : RecyclerView.ViewHolder(view.root) {
//        val CooksViewBkg = view.manyCooksViewBkg
        val cookImageView = view.manyCooksViewItem
        val cookFirstName = view.manyCooksViewName
        val cookLastName = view.manyCooksViewNameLast
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ManyCooksViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cooksList?.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curCook = cooksList.get(position)

        holder.cookFirstName.text = curCook.firstName
//        holder.cookLastName.text = curCook.lastName
        holder.cookImageView.setCookFromCooksView(curCook)

//        Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.white_circle)).apply(RequestOptions.circleCropTransform()).into(holder.CooksViewBkg)

        holder.cookImageView.setUserImageViewListener(this)

    }

    override fun onUserImageClick(cook: Cook?) {
        listener?.onCookViewClick(cook!!)
    }

    fun addCook(newCook: Cook) {
        cooksList.add(newCook)
        notifyDataSetChanged()
    }




}