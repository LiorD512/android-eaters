package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet.sub_screens

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FragmentRecyclerBinding
import com.bupp.wood_spoon_eaters.databinding.FragmentUpsaleBinding
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet.UpSaleNCartViewModel
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet.adapters.UpSaleAdapter
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.DividerItemDecoratorDish
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSectionsViewType
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UpSaleFragment : Fragment() {

    private val viewModel by sharedViewModel<UpSaleNCartViewModel>()
    val binding: FragmentUpsaleBinding by viewBinding()
    private val upSaleAdapter = UpSaleAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upsale, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initObservers()
    }

    private fun initUi() {
        with(binding){
            val divider: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.line_divider)
            recyclerFrag.initSwipeableRecycler(upSaleAdapter, divider = divider)
//            recyclerFrag.layoutManager = LinearLayoutManager(requireContext())
//            val divider: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_down)
//            recyclerFrag.addItemDecoration(DividerItemDecoratorDish(divider))
//            recyclerFrag.adapter = upSaleAdapter
//            val divider: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.chooser_divider)
//            recyclerFrag.addItemDecoration(DividerItemDecoratorDish(divider))
//            recyclerFrag.initSwipeableRecycler(upSaleAdapter)
        }

        viewModel.initData()
    }

    private fun initObservers() {
        viewModel.upSaleLiveData.observe(viewLifecycleOwner, {
            handleUpSaleData(it)
        })
    }

    private fun handleUpSaleData(data: UpSaleNCartViewModel.UpsaleData?) {
        data?.let{
            with(binding){
//                val adapter = it.adapter
//                recyclerFrag.adapter = adapter
//                recyclerFrag.initSwipeableRecycler(adapter)
                recyclerFrag.scheduleLayoutAnimation()
                upSaleAdapter.submitList(it.items)
            }
        }
    }

}