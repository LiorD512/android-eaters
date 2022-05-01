//package com.bupp.wood_spoon_chef.presentation.features.main.events.sub_screen.join_event
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.bumptech.glide.request.RequestOptions
//import com.bupp.wood_spoon_chef.databinding.JoinEventItemBinding
//import com.bupp.wood_spoon_chef.model.Event
//import com.bupp.wood_spoon_chef.utils.DateUtils
//
//class EventsAdapter(val context: Context, val eventsList: ArrayList<Event>, val listener: EventsAdapterListener): RecyclerView.Adapter<EventsAdapter.ViewHolder>() {
//
//    interface EventsAdapterListener{
//        fun onEventClicked(event: Event)
//    }
//
//    class ViewHolder (view: JoinEventItemBinding) : RecyclerView.ViewHolder(view.root) {
//        val mainLayout = view.joinEventItemMainLayout
//        val img = view.joinEventItemIcon
//        val title = view.joinEventItemTitle
//        val date = view.joinEventItemDate
//        val location = view.joinEventItemLocation
//        val pendingBkg = view.joinEventItemPendingLayout
//        val arrow = view.joinEventItemArrow
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = JoinEventItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(binding)
//    }
//
//
//    override fun getItemCount(): Int {
//        return eventsList.size
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val curEvent = eventsList.get(position)
//
//        Glide.with(context).load(curEvent.thumbnail).apply(RequestOptions.circleCropTransform()).into(holder.img)
//        holder.title.text = curEvent.title
//        holder.date.text = DateUtils.parseDateToDayDateHour(curEvent.startsAt)
//        holder.location.text = "${curEvent.location.streetLine1}"
//
//        curEvent.invitation?.let{
//            when(it.status){
//                "active" -> {
//                    holder.arrow.visibility = View.GONE
//                    holder.pendingBkg.visibility = View.GONE
//                }
//                "pending" -> {
//                    holder.arrow.visibility = View.VISIBLE
//                    holder.pendingBkg.visibility = View.VISIBLE
//                }
//            }
//        }
//
//        holder.mainLayout.setOnClickListener {
//            listener.onEventClicked(curEvent)
//        }
//
//
//    }
//
//
//
//}