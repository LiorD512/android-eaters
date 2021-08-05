package com.bupp.wood_spoon_eaters.delete_me

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R

class WordAdapter(private val words: ArrayList<String>, val itemClick: (String) -> Unit): RecyclerView.Adapter<WordViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_list_item_reviews, parent, false)
        return WordViewHolder(inflater)
    }

    override fun getItemCount(): Int = words.count()

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.setData(words[position])
        holder.itemView.setOnClickListener {
            itemClick(words[position])
        }
    }
}