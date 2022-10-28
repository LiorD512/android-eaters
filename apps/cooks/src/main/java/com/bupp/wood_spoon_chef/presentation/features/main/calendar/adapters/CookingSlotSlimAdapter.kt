package com.bupp.wood_spoon_chef.presentation.features.main.calendar.adapters

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_chef.databinding.CalendarCookSlotListFooterBinding
import com.bupp.wood_spoon_chef.databinding.ItemCookingSlotSlimBinding
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlotSlim
import com.bupp.wood_spoon_chef.presentation.views.findTextAndApplySpan
import com.bupp.wood_spoon_chef.utils.getLocal
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

private object ViewType {
    const val ITEM = 1
    const val FOOTER = 2
}

class CookingSlotSlimAdapter(
    val context: Context,
    private val cookingSlotList: MutableList<CookingSlotSlim>,
    val listener: CalendarCookingSlotAdapterListener?,
    private val isShowNewCookingSlotDetailsEnabled: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface CalendarCookingSlotAdapterListener {
        fun onCreateCookingSlotClick()
        @Deprecated("use onCookingSlotClickNew() to navigate to new design. Remove all old functionality.")
        fun onCookingSlotClick(cookingSlotIds: List<Long>, selectedCookingSlotDate: Long)
        fun onCookingSlotClickNew(selectedCookingSlotId: Long)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ViewType.FOOTER -> {
                (holder as FooterViewHolder).addCookingSlot.setOnClickListener {
                    listener?.onCreateCookingSlotClick()
                }
            }
            else -> {
                val curCookingSlot: CookingSlotSlim = cookingSlotList[position]

                curCookingSlot.let { slot ->
                    (holder as ItemViewHolder).root.setOnClickListener {
                        doOnCookingSlotClick(slot)
                    }

                    val fromDateTime: DateTimeFormatter = DateTimeFormat.
                    forPattern("h:mm a").withLocale(getLocal())
                    val toDateTime: DateTimeFormatter = DateTimeFormat.
                    forPattern("h:mm a").withLocale(getLocal())
                    val fromDateTimeFormatted: String = fromDateTime.print(DateTime(curCookingSlot.startsAt)).lowercase()
                    val toDateTimeFormatted: String = toDateTime.print(DateTime(curCookingSlot.endsAt)).lowercase()

                    prepareStringSpanned(
                        textView = holder.slotTitle,
                        highlightedPart = curCookingSlot.name,
                        fullString = "${curCookingSlot.name} $fromDateTimeFormatted - $toDateTimeFormatted"
                    )
                }
            }
        }
    }

    private fun doOnCookingSlotClick(slot: CookingSlotSlim) {
        if (isShowNewCookingSlotDetailsEnabled) {
            listener?.onCookingSlotClickNew(
                selectedCookingSlotId = slot.id
            )
        } else {
            listener?.onCookingSlotClick(
                cookingSlotList.map { it.id },
                cookingSlotList.first().startsAt.time
            )
        }
    }

    private  fun prepareStringSpanned(
        textView: TextView,
        highlightedPart: String?,
        fullString: String?) {

        if (highlightedPart.isNullOrBlank() || fullString.isNullOrEmpty()) {
            textView.text  = fullString
            return
        }

        val result = SpannableString(fullString)
        findTextAndApplySpan(result, highlightedPart, fullString)
        findTextAndApplySpan(result, highlightedPart, fullString, ForegroundColorSpan(Color.BLACK))
        textView.text = result
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ViewType.FOOTER) {
            val binding = CalendarCookSlotListFooterBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
            FooterViewHolder(binding)
        } else {
            val binding = ItemCookingSlotSlimBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
            ItemViewHolder(binding)
        }
    }

    override fun getItemCount(): Int = cookingSlotList.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) {
            ViewType.FOOTER
        } else {
            ViewType.ITEM
        }
    }

    fun updateList(cookingSlots: List<CookingSlotSlim>) {
        cookingSlotList.clear()
        cookingSlotList.addAll(cookingSlots)
        notifyDataSetChanged()
    }

    fun removeCookingSlot(hideCookingSlotId: Long) {
        val tempList: ArrayList<CookingSlotSlim> = arrayListOf()
        for (cookingSlot in cookingSlotList) {
            if (cookingSlot.id != hideCookingSlotId) {
                tempList.add(cookingSlot)
            }
        }
        cookingSlotList.clear()
        cookingSlotList.addAll(tempList)
        notifyDataSetChanged()
    }
}

class ItemViewHolder(view: ItemCookingSlotSlimBinding) :
    RecyclerView.ViewHolder(view.root) {
    val slotTitle = view.tvSlotTitle
    val root = view.root
}

class FooterViewHolder(view: CalendarCookSlotListFooterBinding) :
    RecyclerView.ViewHolder(view.root) {
    val addCookingSlot = view.calendarCookFooterAddBtn
}




