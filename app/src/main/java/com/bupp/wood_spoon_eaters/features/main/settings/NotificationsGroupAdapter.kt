package com.bupp.wood_spoon_eaters.features.main.settings

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.NotificationGroupItemBinding
import com.bupp.wood_spoon_eaters.model.NotificationGroup

class NotificationsGroupAdapter(val context: Context, private var notificationsGroup: List<NotificationGroup>, private var eaterPrefs: MutableList<Long>, val listener: NotificationsGroupAdapterListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface NotificationsGroupAdapterListener{
        fun onNotificationChange(notificationGroupIds: List<Long>)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val notificationGroup = notificationsGroup[position]

        (holder as ItemViewHolder).name.text = notificationGroup.name
        holder.description.text = notificationGroup.description

        if(eaterPrefs.isEmpty()){

        }else{
            holder.switch.isChecked = eaterPrefs.contains(notificationGroup.id)
        }

        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            Log.d("wowSettings", "onCheckedChanged ${notificationGroup.id}")
            if (eaterPrefs.contains(notificationGroup.id)) {
                eaterPrefs.remove(notificationGroup.id)
            } else {
                eaterPrefs.add(notificationGroup.id)
            }
            Log.d("wowSettings", "list ${eaterPrefs}")
            listener.onNotificationChange(eaterPrefs)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = NotificationGroupItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return notificationsGroup.size
    }

    fun reverseSwitchThis(lastClickedSwitchId: Long) {
        if(eaterPrefs.contains(lastClickedSwitchId)){
            eaterPrefs.remove(lastClickedSwitchId)
        }else{
            eaterPrefs.add(lastClickedSwitchId)
        }
        notifyDataSetChanged()
    }

    fun getSelectedIds(): List<Long>{
        return eaterPrefs
    }
}

class ItemViewHolder(view: NotificationGroupItemBinding) : RecyclerView.ViewHolder(view.root) {
    val mainLayout: LinearLayout = view.NotificationGroupMainLayout
    val name: TextView = view.NotificationGroupName
    val description: TextView = view.NotificationGroupDescription
    val switch: SwitchCompat = view.NotificationGroupSwitch

}