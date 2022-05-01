package com.bupp.wood_spoon_chef.presentation.features.main.orders.order_details.dialogs.cancel_order

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_chef.databinding.CancelOrderDialogItemBinding
import com.bupp.wood_spoon_chef.data.remote.model.CancellationReason


class CancelOrderDialogAdapter(val context: Context, private val reasons: List<CancellationReason>?, val listener: CancelOrderDialogListener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedReasonId: Long? = null

    interface CancelOrderDialogListener{
        fun onCheckedChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val reason = reasons?.get(position)
        (holder as ItemViewHolder).checkBox.text = reason?.name

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = reason?.id == selectedReasonId

        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                listener.onCheckedChanged()
                selectedReasonId = reason!!.id
            }
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = CancelOrderDialogItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        if (reasons == null)
            return 0
        return reasons.size
    }

    fun getSelectedReasonId(): Long? {
        return selectedReasonId
    }

    fun clearSelectedReason() {
        selectedReasonId = null
        notifyDataSetChanged()
    }
}

class ItemViewHolder(view: CancelOrderDialogItemBinding): RecyclerView.ViewHolder(view.root){
    val checkBox = view.cancelOrderDialogItemCb
}






