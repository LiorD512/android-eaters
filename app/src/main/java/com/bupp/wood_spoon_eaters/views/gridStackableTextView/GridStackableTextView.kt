package com.bupp.wood_spoon_eaters.views.gridStackableTextView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.databinding.GridStackableTextViewBinding


class GridStackableTextView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private var binding: GridStackableTextViewBinding = GridStackableTextViewBinding.inflate(LayoutInflater.from(context), this, true)


    private lateinit var adapter: CertificatesAdapter

    init{
        initUi()
    }

    fun initUi() {
        adapter = CertificatesAdapter()

        with(binding){
            val layoutManager = LinearLayoutManager(context)
//            val layoutManager = FlexboxLayoutManager(context)
//            layoutManager.flexDirection = FlexDirection.COLUMN
//            layoutManager.justifyContent = JustifyContent.SPACE_EVENLY
            gridStackableTextViewList.layoutManager = layoutManager
            gridStackableTextViewList.adapter = adapter
        }

    }

    fun initStackableViewWith(certificates: List<String>){
        adapter.submitList(certificates)
    }


}