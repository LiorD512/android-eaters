package com.bupp.wood_spoon_eaters.dialogs.locationAutoComplete

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.utils.Utils
import com.taliazhealth.predictix.network_google.models.google_api.AddressIdResponse
import kotlinx.android.synthetic.main.auto_complete_list_item.view.*

class AddressAutoCompleteAdapter(val context: Context, val listener: AddressAutoCompleteAdapterListener) : RecyclerView.Adapter<AddressAutoCompleteAdapter.ViewHolder>() {

    lateinit var query: String
    var presentedList: AddressIdResponse? = null
    var lastSelectedItem: AddressIdResponse? = null

    interface AddressAutoCompleteAdapterListener {
        fun onItemClick(selected: AddressIdResponse.PredictionsItem?)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.autoCompleteItemTitle
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.auto_complete_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        if (presentedList == null || presentedList?.predictions == null)
            return 0
        return presentedList?.predictions?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curObj = presentedList?.predictions?.get(position)

        holder.title.setOnClickListener {
            listener.onItemClick(curObj)
        }

        holder.title.isSelected = curObj == lastSelectedItem?.PredictionsItem()

        holder.title.text = boldQueryText(curObj?.description!!)
    }

    fun clean() {
        presentedList = null
        notifyDataSetChanged()
    }

    fun refreshList(body: AddressIdResponse?, query: String) {
        this.query = query
        if (body != null) {
            this.presentedList = body
            notifyDataSetChanged()
        }
    }

    private fun boldQueryText(addressStr: String): SpannableStringBuilder {
        var ssbuilder = SpannableStringBuilder(addressStr)
        var ss = SpannableString("test")
        val splitString = query.split(" ")
        Log.d("wowAutoCompltAdapter", "splitted strings are $splitString in size of ${splitString.size}")



        for (str in splitString) {
            var startIndex: Int = addressStr.indexOf(str, 0, true)
            var endIndex: Int = startIndex + str.length

            if (startIndex > -1 && endIndex > -1) {
                var spannableString =
                    Utils.setCustomFontTypeSpan(context, addressStr, startIndex, endIndex, R.font.open_sans_semi_bold)
                ssbuilder.setSpan(spannableString, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                Log.d(
                    "wowAutoCompltAdapter",
                    "spanned string $ssbuilder from $startIndex -> $endIndex (which is ${ssbuilder.subSequence(
                        startIndex,
                        endIndex
                    )})"
                )
            }
        }
        if (ssbuilder.isNotEmpty()) {
            Log.d(
                "wowAutoCompltAdapter",
                "ssbuilder at the end is with ${ssbuilder.getSpans(0, ssbuilder.length, ss.javaClass).size} spans"
            )

            return ssbuilder
        }
        return SpannableStringBuilder(addressStr)
    }
}