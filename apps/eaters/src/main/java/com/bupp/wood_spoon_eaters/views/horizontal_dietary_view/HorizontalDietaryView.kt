package com.bupp.wood_spoon_eaters.views.horizontal_dietary_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.HorizontalDietaryViewBinding
import com.bupp.wood_spoon_eaters.model.SelectableIcon
import com.bupp.wood_spoon_eaters.utils.AnimationUtil
import com.bupp.wood_spoon_eaters.utils.Utils


class HorizontalDietaryView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr), HorizontalDietaryAdapter.HorizontalDietaryListener {

    private var binding: HorizontalDietaryViewBinding = HorizontalDietaryViewBinding.inflate(LayoutInflater.from(context), this, true)
    private var diets: List<SelectableIcon>? = null

    init {
        initUi()
    }

    var listener: HorizontalDietaryViewListener? = null

    interface HorizontalDietaryViewListener {
        fun onDietaryClick(dietary: SelectableIcon)
    }

    private fun initUi() {

        adapter = HorizontalDietaryAdapter(this)
        with(binding){
            horizontalDietaryList.adapter = adapter
            horizontalDietaryList.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }
    }
    private lateinit var adapter: HorizontalDietaryAdapter

    fun initHorizontalDietaryView(diets: List<SelectableIcon>, listener: HorizontalDietaryViewListener){
        this.listener = listener
        this.diets = diets
        adapter.submitList(diets)
    }

    fun setSelectedDietary(diets: List<SelectableIcon>){
        adapter.setSelected(diets)
    }

    fun showError() {
        with(binding) {
            Utils.vibrate(context)
            AnimationUtil().shakeView(horizontalDietaryList)
        }
    }

    override fun onDietaryClick(dietary: SelectableIcon) {
        listener?.onDietaryClick(dietary)
    }

}