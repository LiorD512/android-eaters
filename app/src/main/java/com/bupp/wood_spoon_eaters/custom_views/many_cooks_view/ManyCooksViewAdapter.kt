package com.bupp.wood_spoon_eaters.custom_views.many_cooks_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.UserImageView
import com.bupp.wood_spoon_eaters.model.Cook
import kotlinx.android.synthetic.main.many_cooks_view_item.view.*

class ManyCooksViewAdapter(val context: Context, val cooksList: ArrayList<Cook>, val listener: ManyCooksViewListener): RecyclerView.Adapter<ManyCooksViewAdapter.ViewHolder>(),
    UserImageView.UserImageViewListener {


    interface ManyCooksViewListener{
        fun onCookViewClick(selected: Cook)
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val cookImageView = view.manyCooksViewItem
        val cookFirstName = view.manyCooksViewName
        val cookLastName = view.manyCooksViewNameLast
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.many_cooks_view_item, parent, false))
    }

    override fun getItemCount(): Int {
        return cooksList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curCook = cooksList.get(position)

        holder.cookImageView.setCookFromCooksView(curCook)
        holder.cookFirstName.text = curCook.firstName
        holder.cookLastName.text = curCook.lastName


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