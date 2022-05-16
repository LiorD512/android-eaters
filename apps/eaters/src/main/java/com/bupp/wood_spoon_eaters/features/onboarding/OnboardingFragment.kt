package com.bupp.wood_spoon_eaters.features.onboarding

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.custom_views.onboarding.SliderContentPage
import com.bupp.wood_spoon_eaters.databinding.FragmentOnboardingBinding
import com.bupp.wood_spoon_eaters.features.login.LoginViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {
    private var binding: FragmentOnboardingBinding? = null
    private val sharedLoginViewModel: LoginViewModel by sharedViewModel()
    private val viewModel: OnboardingViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onViewCreated()

        binding = FragmentOnboardingBinding.bind(view)

        setupSliderView()
        trackAnalyticsEventPageVisitOnboarding()
        observeViewModel()

        binding?.welcomeFragmentLogin?.setOnClickListener {
            sharedLoginViewModel.directToPhoneFrag()
        }
    }

    private fun setupSliderView() {
        binding?.sliderFadeInOut?.let {
            it.registerLifecycleOwner(lifecycle)
            it.setupSlideList(viewModel.slideList)
        }
    }

    override fun onStop() {
        super.onStop()
        trackAnalyticsEventTimeSpentInOnboarding()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.featureFlagDynamicContentFlow.collectLatest { isContentDynamic ->
                when (isContentDynamic) {
                    is OnboardingBackgroundViewState.SliderImagesBackground -> showSliderImagesBackground()
                    is OnboardingBackgroundViewState.StaticContendBackground -> showStaticContentBackground(isContentDynamic.sliderContentPage)
                    is OnboardingBackgroundViewState.VideoBackground -> showVideoBackground(isContentDynamic.uri)
                }
            }
        }
    }

    private fun showVideoBackground(path: Uri) {
        binding?.let {
            with(it.sliderFadeInOut) {
                showSliderAnimation(isImageSliding = false, isTextSliding = false)
                showVideoBackground(path)
            }
        }
    }

    private fun showStaticContentBackground(sliderContentPage: SliderContentPage) {
        binding?.let {
            with(it.sliderFadeInOut) {
                showSliderAnimation(isImageSliding = false, isTextSliding = false)
                showStaticBackground(sliderContentPage)
            }
        }
    }

    private fun showSliderImagesBackground() {
        binding?.let {
            with(it.sliderFadeInOut) {
                showSliderAnimation(isImageSliding = true, isTextSliding = true)
            }
        }
    }

    private fun trackAnalyticsEventPageVisitOnboarding() =
        sharedLoginViewModel.trackPageEvent(
            FlowEventsManager.FlowEvents.PAGE_VISIT_ON_BOARDING
        )

    private fun trackAnalyticsEventTimeSpentInOnboarding() {
        viewModel.trackAnalyticsEventTimeSpentInOnboarding()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}