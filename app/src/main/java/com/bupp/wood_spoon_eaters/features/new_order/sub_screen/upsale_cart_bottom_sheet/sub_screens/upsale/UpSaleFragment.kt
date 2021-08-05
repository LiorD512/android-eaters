//package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet.sub_screens.upsale
//
//import android.graphics.drawable.Drawable
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.content.ContextCompat
//import by.kirich1409.viewbindingdelegate.viewBinding
//import com.bupp.wood_spoon_eaters.R
//import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
//import com.bupp.wood_spoon_eaters.databinding.FragmentUpsaleBinding
//import org.koin.androidx.viewmodel.ext.android.viewModel
//
//class UpSaleFragment : Fragment() {
//
//    private val viewModel by viewModel<UpSaleViewModel>()
//    private val binding: FragmentUpsaleBinding by viewBinding()
//    private val upSaleAdapter = UpSaleAdapter()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_upsale, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        initUi()
//        initObservers()
//    }
//
//    private fun initUi() {
//        with(binding){
//            val divider: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.line_divider)
//            upsaleFragList.initSwipeableRecycler(upSaleAdapter)
//            upsaleFragList.addItemDecoration(DividerItemDecorator(divider))
//        }
//        viewModel.initData()
//    }
//
//    private fun initObservers() {
//        viewModel.upSaleLiveData.observe(viewLifecycleOwner, {
//            handleUpSaleData(it)
//        })
//    }
//
//    private fun handleUpSaleData(data: UpSaleViewModel.UpsaleData?) {
//        data?.let{
//            with(binding){
//                upSaleAdapter.submitList(it.items)
//            }
//        }
//    }
//
//}