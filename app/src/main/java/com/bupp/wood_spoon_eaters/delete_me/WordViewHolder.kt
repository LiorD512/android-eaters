package com.bupp.wood_spoon_eaters.delete_me

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R

class WordViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    fun setData(word: String){
        val wordTV = view.findViewById<TextView>(R.id.word_tv)
        wordTV.text = word
    }
}