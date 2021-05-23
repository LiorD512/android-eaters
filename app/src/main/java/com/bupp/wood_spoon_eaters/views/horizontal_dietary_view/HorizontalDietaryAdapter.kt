//package com.bupp.wood_spoon_eaters.views.horizontal_dietary_view
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.LinearLayout
//import android.widget.TextView
//import androidx.core.content.res.ResourcesCompat
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.bupp.wood_spoon_eaters.R
//import com.bupp.wood_spoon_eaters.databinding.HorizontalDietaryViewItemBinding
//import com.bupp.wood_spoon_eaters.model.SelectableIcon
//
//class HorizontalDietaryAdapter(val listener: HorizontalDietaryListener) :
//        ListAdapter<SelectableIcon, RecyclerView.ViewHolder>(DiffCallback()) {
//
//    private val selectedDiets: MutableList<SelectableIcon> = mutableListOf()
//    var isSelectEnabled = true
//
//    interface HorizontalDietaryListener {
////        fun onDietaryClick(dietary: SelectableIcon)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val binding = HorizontalDietaryViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val dietary = getItem(position)
//        val itemViewHolder = holder as ViewHolder
//        val context = itemViewHolder.itemView.context
//
//        itemViewHolder.name.text = dietary.name
//
//        if(isSelectEnabled){
//            itemViewHolder.layout.setOnClickListener {
//                if (this.selectedDiets.contains(dietary)) {
//                    selectedDiets.remove(dietary)
//                } else {
//                    selectedDiets.add(dietary)
//                }
//                notifyItemChanged(position)
//            }
//        }
//
//        if (this.selectedDiets.contains(dietary)) {
//
//            Glide.with(context).load(dietary.iconSelected).into(itemViewHolder.icon)
//            holder.name.isSelected = true
//
//            val face = ResourcesCompat.getFont(context, R.font.lato_bold)
//            holder.name.typeface = face
//        } else {
//            Glide.with(context).load(dietary.icon).into(itemViewHolder.icon)
//            holder.name.isSelected = false
//
//            val face = ResourcesCompat.getFont(context, R.font.lato_reg)
//            holder.name.typeface = face
//        }
//
//    }
//
//    fun setSelected(diets: List<SelectableIcon>) {
//        selectedDiets.clear()
//        selectedDiets.addAll(diets)
//        notifyDataSetChanged()
//    }
//
//    fun getSelectedDiets(): List<SelectableIcon> {
//        return this.selectedDiets
//    }
//
//    fun setSelectable(isSelectable: Boolean) {
//        this.isSelectEnabled = isSelectable
//    }
//
//    class ViewHolder(view: HorizontalDietaryViewItemBinding) : RecyclerView.ViewHolder(view.root) {
//        val name: TextView = view.horizontalDietaryName
//        val icon: ImageView = view.horizontalDietaryIcon
//        val layout: LinearLayout = view.horizontalDietaryLayout
//    }
//
//    class DiffCallback : DiffUtil.ItemCallback<SelectableIcon>() {
//
//        override fun areItemsTheSame(oldItem: SelectableIcon, newItem: SelectableIcon): Boolean {
//            return oldItem == newItem
//        }
//
//        override fun areContentsTheSame(oldItem: SelectableIcon, newItem: SelectableIcon): Boolean {
//            return oldItem.id == newItem.id
//        }
//    }
//}