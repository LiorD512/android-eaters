package com.bupp.wood_spoon_eaters.features.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.android.synthetic.main.header_view.*


class FeedFragment() : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEmptyView()
    }

    private fun initEmptyView() {
        feedFragEmptyFeedLayout.visibility = View.VISIBLE
        feedFragEmptyFeedTitle.text =  String.format("Hey %s", "Neta")
        (activity as MainActivity).headerViewBackBtn
    }

}