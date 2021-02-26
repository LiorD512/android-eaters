//package com.bupp.wood_spoon_eaters.features.main.feed_loader
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import com.bupp.wood_spoon_eaters.R
//import com.bupp.wood_spoon_eaters.databinding.FragmentFeedLoaderBinding
//import org.koin.androidx.viewmodel.ext.android.viewModel
//
//class FeedLoaderFragment : Fragment(R.layout.fragment_feed_loader) {
//
//    val viewModel by viewModel<FeedLoaderViewModel>()
//    var binding: FragmentFeedLoaderBinding? = null
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding = FragmentFeedLoaderBinding.bind(view)
//
//        initUi()
//        initObservers()
//
//        viewModel.getWelcomeScreens()
//    }
//
//    private fun initUi() {
//
//    }
//
//    private fun initObservers() {
//        viewModel.welcomeScreens.observe(viewLifecycleOwner, {
//            it?.let{
//                binding!!.feedLoaderImageView.init(it)
//            }
//        })
//    }
//}