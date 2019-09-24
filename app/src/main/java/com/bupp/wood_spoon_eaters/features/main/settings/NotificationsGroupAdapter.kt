package com.bupp.wood_spoon_eaters.features.main.settings

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.NotificationGroup
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.notification_group_item.view.*
import kotlinx.android.synthetic.main.orders_history_item.view.*

class NotificationsGroupAdapter(val context: Context, private var notificationsGroup: ArrayList<NotificationGroup>, private var eaterPrefs: ArrayList<Long>, val listener: NotificationsGroupAdapterListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface NotificationsGroupAdapterListener{
        fun onNotificationChange(notificationGroupId: Long)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val notificationGroup = notificationsGroup[position]

        (holder as ItemViewHolder).name.text = notificationGroup.name
        holder.description.text = notificationGroup.description

        if(eaterPrefs.isEmpty()){

        }else{
            if(eaterPrefs.contains(notificationGroup.id)){
                holder.switch.isChecked = true
            }else{
                holder.switch.isChecked = false
            }
        }

        holder.switch.setOnCheckedChangeListener(object: CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                Log.d("wowSettings","onCheckedChanged ${notificationGroup.id}")
                if(eaterPrefs.contains(notificationGroup.id)){
                    eaterPrefs.remove(notificationGroup.id)
                }else{
                    eaterPrefs.add(notificationGroup.id)
                }
                Log.d("wowSettings","list ${eaterPrefs}")
                listener.onNotificationChange(notificationGroup.id)
            }
        })

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.notification_group_item, parent, false))
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

    fun getSelectedIds(): ArrayList<Long>{
        return eaterPrefs
    }
}

class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mainLayout: LinearLayout = view.NotificationGroupMainLayout
    val name: TextView = view.NotificationGroupName
    val description: TextView = view.NotificationGroupDescription
    val switch: SwitchCompat = view.NotificationGroupSwitch

}