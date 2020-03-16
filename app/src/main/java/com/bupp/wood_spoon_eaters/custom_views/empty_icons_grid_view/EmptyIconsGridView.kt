package com.bupp.wood_spoon_eaters.custom_views.empty_icons_grid_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.GridItemDecoration
import com.bupp.wood_spoon_eaters.model.SelectableIcon
import kotlinx.android.synthetic.main.icons_grid_view.view.*

class EmptyIconsGridView : FrameLayout, EmptyIconsGridViewAdapter.EmptyIconGridViewAdapterListener{

    private val SPAN_COUNT = 5
    private lateinit var adapter: EmptyIconsGridViewAdapter
    private var selectedItemPosition : Int = -1
    private lateinit var listener : OnItemSelectedListener

    interface OnItemSelectedListener{
        fun OnEmptyItemSelected()
    }

    public fun setListener(listener : OnItemSelectedListener){
        this.listener = listener
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.icons_grid_view, this, true)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.EmptyIconsGridView)
            if (a.hasValue(R.styleable.EmptyIconsGridView_title)) {
                var title = a.getString(R.styleable.EmptyIconsGridView_title)
                if(title.isBlank()){
                    iconsGridViewTitle.visibility = View.GONE
                }else{
                    iconsGridViewTitle.text = title
                }

                var subTitle = a.getString(R.styleable.EmptyIconsGridView_subTitle)
                if(title.isBlank()){
                    iconsGridViewSubTitle.visibility = View.GONE
                }else{
                    iconsGridViewSubTitle.text = subTitle
                }
            }
            a.recycle()
        }

        initIconsGrid(arrayListOf())
    }

    fun initIconsGrid(icons: ArrayList<SelectableIcon>) {
        iconsGridViewList.layoutManager = GridLayoutManager(context, SPAN_COUNT)

        //This will for default android divider
        iconsGridViewList.addItemDecoration(GridItemDecoration(2, 2))

        adapter = EmptyIconsGridViewAdapter(context, icons,this)
        iconsGridViewList.adapter = adapter
    }

    override fun onItemSelected(itemPosition: Int) {
        selectedItemPosition = itemPosition
        if (listener != null){
            listener.OnEmptyItemSelected()
        }
    }

    fun updateItems(selectedCuisines: ArrayList<SelectableIcon>) {
        adapter.updateItems(selectedCuisines)
    }

    fun getSelectedCuisines(): ArrayList<SelectableIcon>? {
        return if (adapter.getItems() == null) null else adapter.getItems()
    }


}

