package com.bupp.wood_spoon_eaters.views.dish_tags_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.adapters.HorizontalPaddingItemDecorator
import com.bupp.wood_spoon_eaters.databinding.TagViewBinding
import com.bupp.wood_spoon_eaters.model.Tag
import com.bupp.wood_spoon_eaters.utils.Utils


class DishTagsView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private var binding: TagViewBinding = TagViewBinding.inflate(LayoutInflater.from(context), this, true)
    private lateinit var adapter: DishTagsViewAdapter

    init {
        initUi(attrs)
    }

    private fun initUi(attrs: AttributeSet?) {



            adapter = DishTagsViewAdapter()
        with(binding) {
            tagViewList.adapter = adapter
            tagViewList.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            tagViewList.addItemDecoration(HorizontalPaddingItemDecorator(Utils.toPx(8)))

            val attr = context.obtainStyledAttributes(attrs, R.styleable.DishTagsView)

            val isScrollable = attr.getBoolean(R.styleable.DishTagsView_isScrollable, true)
            tagViewList.isEnabled = isScrollable

            attr.recycle()
        }
    }

    fun initTagView(tags: List<Tag>?) {
        tags?.let{
            adapter.submitList(tags)
        }
    }
}