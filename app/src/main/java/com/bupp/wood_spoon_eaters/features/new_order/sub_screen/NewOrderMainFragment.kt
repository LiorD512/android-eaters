package com.bupp.wood_spoon_eaters.features.new_order.sub_screen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.DepthPageTransformer
import com.bupp.wood_spoon_eaters.databinding.FragmentNewOrderMainBinding
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderMainViewModel
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_cook.SingleDishCookFragment
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_info.SingleDishInfoFragment
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_ingredients.SingleDishIngredientsFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NewOrderMainFragment : Fragment(R.layout.fragment_new_order_main) {

    val binding: FragmentNewOrderMainBinding by viewBinding()
    private val mainViewModel by sharedViewModel<NewOrderMainViewModel>()

    override fun onResume() {
        super.onResume()
        mainViewModel.checkCartStatusAndUpdateUi()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.newOrderFragViewPager.currentItem == 0) {
                    isEnabled = false
                    mainViewModel.handleNavigation(NewOrderMainViewModel.NewOrderScreen.FINISH_ACTIVITY)
                } else {
                    binding.newOrderFragViewPager.currentItem = 0
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        with(binding){
            val pagerAdapter = ScreenSlidePagerAdapter(requireActivity())
            newOrderFragViewPager.adapter = pagerAdapter
            newOrderFragViewPager.offscreenPageLimit = 3
            newOrderFragViewPager.isUserInputEnabled = false
            newOrderFragViewPager.setPageTransformer(DepthPageTransformer())

            newOrderFragHeader.setViewPager(newOrderFragViewPager)

            NewOrderTabBack.setOnClickListener {
                activity?.onBackPressed()
            }

        }


        mainViewModel.navigationEvent.observe(viewLifecycleOwner, {
            Log.d(TAG, "navigationEvent: $it")
            when(it){
                NewOrderMainViewModel.NewOrderNavigationEvent.REDIRECT_TO_COOK_PROFILE -> {
                    binding.newOrderFragViewPager.currentItem = 2
                }
                NewOrderMainViewModel.NewOrderNavigationEvent.REDIRECT_TO_DISH_INFO -> {
                    binding.newOrderFragViewPager.currentItem = 0
                    binding.newOrderFragHeader.handleTabGestures(false)
                }
                NewOrderMainViewModel.NewOrderNavigationEvent.LOCK_SINGLE_DISH_COOK -> {
                    binding.newOrderFragViewPager.post {
                        binding.newOrderFragViewPager.currentItem = 2
                        binding.newOrderFragHeader.handleTabGestures(true)
                    }
                }
                else -> {}
            }
        })

    }


    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment{
            Log.d(TAG, "createFragment: $position")
            return when(position) {
                0 -> {SingleDishInfoFragment()}
                1 -> {SingleDishIngredientsFragment()}
                2 -> {SingleDishCookFragment()}
                else -> {SingleDishCookFragment()}
            }
        }
    }

    companion object{
        const val TAG = "wowNewOrderMainFrag"
        const val NUM_PAGES = 3
    }
}