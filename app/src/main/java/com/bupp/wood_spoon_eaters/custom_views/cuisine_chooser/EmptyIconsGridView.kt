package com.bupp.wood_spoon_eaters.custom_views.cuisine_chooser

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.GridItemDecoration
import com.bupp.wood_spoon_eaters.databinding.IconsGridViewBinding
import com.bupp.wood_spoon_eaters.model.SelectableIcon

class EmptyIconsGridView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) , EmptyIconsGridViewAdapter.EmptyIconGridViewAdapterListener{

    private val SPAN_COUNT = 5
    private lateinit var adapter: EmptyIconsGridViewAdapter
    private var selectedItemPosition : Int = -1
    private lateinit var listener : OnItemSelectedListener

    private var binding: IconsGridViewBinding = IconsGridViewBinding.inflate(LayoutInflater.from(context), this, true)

    interface OnItemSelectedListener{
        fun OnEmptyItemSelected()
    }

    public fun setListener(listener : OnItemSelectedListener){
        this.listener = listener
    }

    init {
        with(binding){
            if (attrs != null) {
                val a = context.obtainStyledAttributes(attrs, R.styleable.EmptyIconsGridView)
                if (a.hasValue(R.styleable.EmptyIconsGridView_title)) {
                    var title = a.getString(R.styleable.EmptyIconsGridView_title)
                    title?.let{
                        if(title.isBlank()){
                            iconsGridViewTitle.visibility = View.GONE
                        }else{
                            iconsGridViewTitle.text = title
                        }
                    }

                    var subTitle = a.getString(R.styleable.EmptyIconsGridView_subTitle)
                    subTitle?.let{
                        if(subTitle.isBlank()){
                            iconsGridViewSubTitle.visibility = View.GONE
                        }else{
                            iconsGridViewSubTitle.text = subTitle
                        }
                    }
                }
                a.recycle()
            }
        }

        initIconsGrid(arrayListOf())
    }

    fun initIconsGrid(icons: ArrayList<SelectableIcon>) {
        with(binding){
            iconsGridViewList.layoutManager = GridLayoutManager(context, SPAN_COUNT)

            //This will for default android divider
            iconsGridViewList.addItemDecoration(GridItemDecoration(2, 2))

            adapter = EmptyIconsGridViewAdapter(context, icons,this@EmptyIconsGridView)
            iconsGridViewList.adapter = adapter
        }
    }

    override fun onItemSelected(itemPosition: Int) {
        selectedItemPosition = itemPosition
        listener.OnEmptyItemSelected()
    }

    fun updateItems(selectedCuisines: ArrayList<SelectableIcon>) {
        adapter.updateItems(selectedCuisines)
    }

    fun getSelectedCuisines(): ArrayList<SelectableIcon>? {
        return if (adapter.getItems() == null) null else adapter.getItems()
    }


}

