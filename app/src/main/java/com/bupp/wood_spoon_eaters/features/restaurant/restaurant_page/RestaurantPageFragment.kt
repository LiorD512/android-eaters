package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page;

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FragmentRestaurantPageBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class RestaurantPageFragment : Fragment(R.layout.fragment_restaurant_page) {

    private val binding: FragmentRestaurantPageBinding by viewBinding()
    private val viewModel by viewModel<RestaurantPageViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initObservers()

        //Only for test - delete later
        val list = mutableListOf<Int>()
        list.add(1)
        list.add(2)
        list.add(3)
        list.add(4)
        list.add(5)
        list.add(6)
        list.add(7)
        list.add(8)
        list.add(9)
        list.add(10)
        list.add(11)
        list.add(12)
        binding.mainRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val adapter = ListAdapter()
        binding.mainRecyclerView.adapter = adapter
        adapter.submitList(list)


//        binding.motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
//
//            var isFirst = true
//            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
//
//            }
//
//            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
//
//            }
//
//            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
//
//            }
//
//            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentState: Int) {
//                if (currentState == R.id.middle) {
//                    if (isFirst) {
//                        binding.motionLayout.setTransition(R.id.middleToEnd)
//                        binding.motionLayout.transitionToEnd()
//                        isFirst = false
//                    } else {
//                        binding.motionLayout.setTransition(R.id.startToMiddle)
//                        binding.motionLayout.transitionToStart()
//                        isFirst = true
//                    }
//                }
//            }
//        })

    }

    private fun initUi() {

    }

    private fun initObservers() {

    }

    companion object {
        private const val TAG = "RestaurantPageFragment"
    }
}