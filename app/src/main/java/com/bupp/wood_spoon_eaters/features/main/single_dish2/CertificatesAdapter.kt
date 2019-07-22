//package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.bupp.wood_spoon_eaters.R
//import kotlinx.android.synthetic.main.certificate_list_item.view.*
//
//class CertificatesAdapter(val context: Context, val certificates: ArrayList<String>) : RecyclerView.Adapter<CertificatesAdapter.ViewHolder>() {
//
//    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val title = view.certificateItemTitle
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.certificate_list_item, parent, false))
//    }
//
//    override fun getItemCount(): Int {
//        return certificates.size
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val certificate = certificates[position]
//        holder.title.text = certificate
//    }
//}