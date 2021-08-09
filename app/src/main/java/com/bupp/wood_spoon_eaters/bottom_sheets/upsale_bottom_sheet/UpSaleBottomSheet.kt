package com.bupp.wood_spoon_eaters.bottom_sheets.upsale_bottom_sheet

import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.SwipeableAddDishItemDecorator
import com.bupp.wood_spoon_eaters.databinding.UpSaleBottomSheetBinding
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.SwipeableAddDishItemTouchHelper
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.views.WSCounterEditText
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.SwipeableRemoveDishItemDecorator
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.SwipeableRemoveDishItemTouchHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import androidx.recyclerview.widget.SimpleItemAnimator

import android.R.string.no
import android.R.string.no

class UpSaleBottomSheet : BottomSheetDialogFragment(), WSCounterEditText.WSCounterListener, HeaderView.HeaderViewListener {

    private val binding: UpSaleBottomSheetBinding by viewBinding()
    private val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var adapter: UpSaleAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.up_sale_bottom_sheet, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }

    private lateinit var behavior: BottomSheetBehavior<View>
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(sheet!!)
            behavior.isFitToContents = true
            behavior.isDraggable = false
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            behavior.expandedOffset = Utils.toPx(230)
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.top_cornered_bkg)

        initUI()
    }

    private fun initUI() {
        with(binding) {
            adapter = UpSaleAdapter()
            upSaleList.layoutManager = LinearLayoutManager(requireContext())
            upSaleList.adapter = adapter
            upSaleList.initSwipeableRecycler(adapter)

            val list = mutableListOf<UpSaleAdapterItem>()
//            list.add(UpSaleAdapterItem(0, Dish(0, null, "a", null, "d", null, "a", "",null, null, null, null, null, null, null)))
//            list.add(UpSaleAdapterItem(1, Dish(0, null, "b", null, "d", null, "a", "" ,null, null, null, null, null, null, null)))
//            list.add(UpSaleAdapterItem(0, Dish(0, null, "c", null, "d", null, "a", "" ,null, null, null, null, null, null, null)))
//            list.add(UpSaleAdapterItem(10, Dish(0, null, "d", null, "d", null, "a", "" ,null, null, null, null, null, null, null)))
//            list.add(UpSaleAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "" ,null, null, null, null, null, null, null)))
//            list.add(UpSaleAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "" ,null, null, null, null, null, null, null)))
//            list.add(UpSaleAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "" ,null, null, null, null, null, null, null)))
//            list.add(UpSaleAdapterItem(1, Dish(0, null, "z", null, "d", null, "a", "" ,null, null, null, null, null, null, null)))
//            list.add(UpSaleAdapterItem(10, Dish(0, null, "d", null, "d", null, "a", "" ,null, null, null, null, null, null, null)))
//            list.add(UpSaleAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "" ,null, null, null, null, null, null, null)))
//            list.add(UpSaleAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "" ,null, null, null, null, null, null, null)))
//            list.add(UpSaleAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "" ,null, null, null, null, null, null, null)))
//            list.add(UpSaleAdapterItem(1, Dish(0, null, "z", null, "d", null, "a", "" ,null, null, null, null, null, null, null)))
//            list.add(UpSaleAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "" ,null, null, null, null, null, null, null)))
//            list.add(UpSaleAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "" ,null, null, null, null, null, null, null)))
//            list.add(UpSaleAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "" ,null, null, null, null, null, null, null)))
//            list.add(UpSaleAdapterItem(1, Dish(0, null, "z", null, "d", null, "a", "" ,null, null, null, null, null, null, null)))
            adapter.submitList(list)
        }
    }

//    fun updateAllRecyclerChildren(dx: Int, dy: Int) {
//        for (i in 1 until adapter.itemCount) {
//            val childView = binding.upSaleList.getChildAt(i)
//            if(childView != null){
//                val songViewHolder = binding.upSaleList.getChildViewHolder(childView) as? UpSaleAdapter.UpSaleItemViewHolder
//                songViewHolder?.setMotionParam(dx, dy)
//            }
//        }
//    }

    override fun onHeaderCloseClick() {
        dismiss()
    }


}