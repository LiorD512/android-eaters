package com.bupp.wood_spoon_eaters.features.login.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.DepthPageTransformer
import com.bupp.wood_spoon_eaters.common.ZoomOutPageTransformer
import com.bupp.wood_spoon_eaters.databinding.FragmentLoginMainBinding
import com.bupp.wood_spoon_eaters.databinding.FragmentNewOrderMainBinding
import com.bupp.wood_spoon_eaters.features.login.LoginViewModel
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderMainViewModel
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_cook.SingleDishCookFragment
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_info.SingleDishInfoFragment
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_ingredients.SingleDishIngredientsFragment
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.fragment_single_dish_info.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginMainFragment : Fragment(R.layout.fragment_login_main) {

    var binding: FragmentLoginMainBinding? = null
    private val mainViewModel by sharedViewModel<LoginViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding!!.loginMainFragViewPager.currentItem == 0) {
                    isEnabled = false
                    activity?.onBackPressed()
                } else {
                    binding!!.loginMainFragViewPager.currentItem = 0
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentLoginMainBinding.bind(view)

        arguments?.let{
            val startWith = it.getInt("start_with_args")
            if(startWith > 0){
                binding!!.loginMainFragViewPager.currentItem = startWith
            }
        }

        with(binding!!){
            val pagerAdapter = ScreenSlidePagerAdapter(requireActivity())
            loginMainFragViewPager.adapter = pagerAdapter
            loginMainFragViewPager.isUserInputEnabled = false
            loginMainFragViewPager.setPageTransformer(DepthPageTransformer())

            loginMainFragCloseBtn.setOnClickListener {
                activity?.onBackPressed()
            }
        }


        mainViewModel.navigationEvent.observe(viewLifecycleOwner, {
            when(it){
                LoginViewModel.NavigationEventType.OPEN_PHONE_SCREEN -> {
                    binding!!.loginMainFragViewPager.currentItem = 0
                }
                LoginViewModel.NavigationEventType.OPEN_CODE_SCREEN -> {
                    binding!!.loginMainFragViewPager.currentItem = 1
                }
                LoginViewModel.NavigationEventType.OPEN_SIGNUP_SCREEN -> {
                    binding!!.loginMainFragViewPager.currentItem = 2
                }
            }
        })

//        arguments?.let{
//            val startWith = it.getInt("START_WITH")
//            if(startWith > 0){
//                binding!!.newOrderFragViewPager.currentItem = startWith
//            }
//        }
    }


    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment{
            return when(position) {
                0 -> {PhoneVerificationFragment()}
                1 -> {CodeFragment()}
                2 -> {CreateAccountFragment()}
                else -> {PhoneVerificationFragment()}
            }
        }
    }

    override fun onDestroyView() {
        singleDishInfoImagePager?.let {
            it.adapter = null
        }
        binding = null
        super.onDestroyView()
    }


    companion object{
        const val TAG = "wowMainLoginFrag"
        const val NUM_PAGES = 3
    }
}