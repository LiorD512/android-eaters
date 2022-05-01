package com.bupp.wood_spoon_chef.presentation.views.horizontal_dietary_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.HorizontalDietaryViewBinding
import com.bupp.wood_spoon_chef.data.remote.model.SelectableIcon
import com.bupp.wood_spoon_chef.utils.AnimationUtil
import com.bupp.wood_spoon_chef.utils.Utils


class HorizontalDietaryView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr),
    HorizontalDietaryAdapter.HorizontalDietaryListener {

    private var binding: HorizontalDietaryViewBinding = HorizontalDietaryViewBinding.inflate(LayoutInflater.from(context), this, true)
    private var diets: List<SelectableIcon>? = null

    init {
        initUi(attrs)
    }

    var listener: HorizontalDietaryViewListener? = null

    interface HorizontalDietaryViewListener {
        fun onDietaryClick(dietary: List<SelectableIcon>)
    }

    private fun initUi(attrs: AttributeSet?) {
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

    fun initHorizontalDietaryViewShow(diets: List<SelectableIcon>){
        this.diets = diets
        binding.horizontalDietaryBkg.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        adapter.setSelectable(false)
        adapter.submitList(diets)
    }

    private fun getSelectedDiets(): List<SelectableIcon> {
        return adapter.getSelectedDiets()
    }

    fun getSelectedItemsIds(): List<Long>{
        val array : MutableList<Long> = mutableListOf()
        for(item in getSelectedDiets()){
            array.add(item.id)
        }
        return array
    }

    fun checkIfValidAndSHowError(): Boolean {
        if (adapter.getSelectedDiets().isNullOrEmpty()) {
            showError()
            return false
        }
        return true
    }

    private fun showError() {
        with(binding) {
            Utils.vibrate(context)
            AnimationUtil().shakeView(horizontalDietaryList, binding.root.context)
        }
    }

    fun setSelectedDietaryIds(dietsIds: List<Long>) {
        val selectedDiets = mutableListOf<SelectableIcon>()

        diets?.let{ diets->
            diets.forEach { diet ->
                dietsIds.forEach { selectedId ->
                    if(diet.id == selectedId){
                        selectedDiets.add(diet)
                    }
                }
            }
        }
        adapter.setSelected(selectedDiets)
    }

    override fun onDietaryClick(diets: List<SelectableIcon>) {
        listener?.onDietaryClick(diets)
    }

}