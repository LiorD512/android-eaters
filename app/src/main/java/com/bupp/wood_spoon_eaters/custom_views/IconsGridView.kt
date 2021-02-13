package com.bupp.wood_spoon_eaters.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.adapters.IconsGridViewAdapter
import com.bupp.wood_spoon_eaters.model.SelectableIcon
import kotlinx.android.synthetic.main.icons_grid_view.view.*


class IconsGridView : FrameLayout, IconsGridViewAdapter.IconGridViewAdapterListener {

    interface IconsGridViewListener{
        fun onIconClick(selected: ArrayList<SelectableIcon>)
    }

    private var listener: IconsGridViewListener? = null
    private lateinit var adapter: IconsGridViewAdapter

    fun setIconsGridViewListener(listener: IconsGridViewListener){
        this.listener = listener
    }
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.icons_grid_view, this, true)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.IconsGridView)
            if (a.hasValue(R.styleable.IconsGridView_title)) {
                var title = a.getString(R.styleable.IconsGridView_title)
                title?.let{
                    if (it.isBlank()) {
                        iconsGridViewTitle.visibility = View.GONE
                    } else {
                        iconsGridViewTitle.text = title
                    }
                }

                var subTitle = a.getString(R.styleable.IconsGridView_subTitle)
                subTitle?.let{
                    if (subTitle.isBlank()) {
                        iconsGridViewSubTitle.visibility = View.GONE
                    } else {
                        iconsGridViewSubTitle.text = subTitle
                    }
                }
            }
            a.recycle()
        }
    }

    public fun loadSelectedIcons(icons: ArrayList<SelectableIcon>?) {
        if (icons != null) {
            adapter.loadSelectedIcons(icons)
        }
    }

    public fun initIconsGrid(icons: List<SelectableIcon>, choiceCount: Int) {
        iconsGridViewList.layoutManager = GridLayoutManager(context, 5)

        //This will for default android divider
        iconsGridViewList.addItemDecoration(GridItemDecoration(2, 2))

        adapter = IconsGridViewAdapter(context, icons, this, choiceCount)
        iconsGridViewList.adapter = adapter
    }

    fun getSelectedItems(): ArrayList<SelectableIcon> {
        return adapter.getSelectedIcons()
    }

    fun getSelectedItemsIds(): ArrayList<Long> {
        var array: ArrayList<Long> = arrayListOf()
        for (item in getSelectedItems()) {
            array.add(item.id)
        }
        return array
    }

    fun setSelectedItems(selectedList: ArrayList<SelectableIcon>) {
        return adapter.setSelectedIcons(selectedList)
    }

    override fun onItemClick(selected: SelectableIcon) {
        listener?.onIconClick(getSelectedItems())
    }
}