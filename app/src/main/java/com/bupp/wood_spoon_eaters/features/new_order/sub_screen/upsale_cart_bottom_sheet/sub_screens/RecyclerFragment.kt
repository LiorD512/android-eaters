package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet.sub_screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FragmentNewOrderMainBinding
import com.bupp.wood_spoon_eaters.databinding.FragmentRecyclerBinding
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet.UpSaleNCartViewModel
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet.adapters.UpSaleAdapter
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RecyclerFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private val viewModel by sharedViewModel<UpSaleNCartViewModel>()
    val binding: FragmentRecyclerBinding by viewBinding()
    var upSaleAdapter = UpSaleAdapter()
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initObservers()
    }

    private fun initUi() {
        with(binding){
            recyclerFrag.layoutManager = LinearLayoutManager(requireContext())
            recyclerFrag.initSwipeableRecycler(upSaleAdapter)
        }

        viewModel.initData()
    }

    private fun initObservers() {
        viewModel.upSaleLiveData.observe(viewLifecycleOwner, {
            handleUpSaleData(it)
        })
        viewModel.cartLiveData.observe(viewLifecycleOwner, {
            handleCartData(it)
        })
    }

    private fun handleUpSaleData(data: UpSaleNCartViewModel.UpsaleData?) {
        data?.let{
            with(binding){
                val adapter = it.adapter
//                recyclerFrag.adapter = adapter
//                recyclerFrag.initSwipeableRecycler(adapter)
                upSaleAdapter.submitList(it.items)
            }
        }
    }

    private fun handleCartData(data: UpSaleNCartViewModel.CartData?) {
        data?.let{
            with(binding){
                recyclerFrag.adapter = it.adapter
                it.adapter.submitList(it.items)
            }
        }
    }


//    companion object {
//        private const val ARG_PARAM2 = "param2"
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            RecyclerFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}