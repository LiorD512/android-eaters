package com.bupp.wood_spoon_eaters.features.new_order.sub_screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.DepthPageTransformer
import com.bupp.wood_spoon_eaters.databinding.FragmentNewOrderMainBinding
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderMainViewModel
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_cook.SingleDishCookFragment
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_info.SingleDishInfoFragment
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_ingredients.SingleDishIngredientsFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NewOrderMainFragment : Fragment(R.layout.fragment_new_order_main) {

    var binding: FragmentNewOrderMainBinding? = null
    private val mainViewModel by sharedViewModel<NewOrderMainViewModel>()

    override fun onResume() {
        super.onResume()
        mainViewModel.checkCartStatusAndUpdateUi()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding!!.newOrderFragViewPager.currentItem == 0) {
                    isEnabled = false
                    mainViewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.FINISH_ACTIVITY)
                } else {
                    binding!!.newOrderFragViewPager.currentItem = 0
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentNewOrderMainBinding.bind(view)

        with(binding!!){
            val pagerAdapter = ScreenSlidePagerAdapter(requireActivity())
            newOrderFragViewPager.adapter = pagerAdapter
            newOrderFragViewPager.offscreenPageLimit = 2
            newOrderFragViewPager.isUserInputEnabled = false
            newOrderFragViewPager.setPageTransformer(DepthPageTransformer())

            newOrderFragHeader.setViewPager(newOrderFragViewPager)

            NewOrderTabBack.setOnClickListener {
                activity?.onBackPressed()
            }
        }


        mainViewModel.navigationEvent.observe(viewLifecycleOwner, {
            when(it){
                NewOrderMainViewModel.NewOrderNavigationEvent.REDIRECT_TO_COOK_PROFILE -> {
                    binding!!.newOrderFragViewPager.currentItem = 2
                }
                NewOrderMainViewModel.NewOrderNavigationEvent.REDIRECT_TO_DISH_INFO -> {
                    binding!!.newOrderFragViewPager.currentItem = 0
                    binding!!.newOrderFragHeader.handleTabGestures(false)
                }
                NewOrderMainViewModel.NewOrderNavigationEvent.LOCK_SINGLE_DISH_COOK -> {
                    binding!!.newOrderFragViewPager.currentItem = 2
                    binding!!.newOrderFragHeader.handleTabGestures(true)
                }
            }
        })
    }


    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment{
            return when(position) {
                0 -> {SingleDishInfoFragment()}
                1 -> {SingleDishIngredientsFragment()}
                2 -> {SingleDishCookFragment()}
                else -> {SingleDishInfoFragment()}
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }


    companion object{
        const val TAG = "wowNewOrderMainFrag"
        const val NUM_PAGES = 3
    }
}