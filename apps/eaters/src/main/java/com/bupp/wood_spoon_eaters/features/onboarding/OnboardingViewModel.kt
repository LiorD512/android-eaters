package com.bupp.wood_spoon_eaters.features.onboarding

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.onboarding.SliderContentPage
import com.bupp.wood_spoon_eaters.domain.FeatureFlagDynamicContentUseCase
import com.bupp.wood_spoon_eaters.domain.GetOnboardingSlideListUseCase
import com.bupp.wood_spoon_eaters.domain.GetOnboardingVideoPathUseCase
import com.bupp.wood_spoon_eaters.domain.comon.execute
import com.bupp.wood_spoon_eaters.managers.EatersAnalyticsTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import kotlin.properties.Delegates

sealed class OnboardingBackgroundViewState {
    object SliderImagesBackground : OnboardingBackgroundViewState()
    class VideoBackground(val uri: Uri) : OnboardingBackgroundViewState()
    class StaticContendBackground(
        val sliderContentPage: SliderContentPage
    ) : OnboardingBackgroundViewState()
}

class OnboardingViewModel(
    private val featureFlagDynamicContentUseCase: FeatureFlagDynamicContentUseCase,
    private val getOnboardingVideoPathUseCase: GetOnboardingVideoPathUseCase,
    getOnboardingSlideListUseCase: GetOnboardingSlideListUseCase,
    private val eatersAnalyticsTracker: EatersAnalyticsTracker
) : ViewModel() {

    val slideList = getOnboardingSlideListUseCase.execute()

    private var _featureFlagDynamicContentFlow = MutableStateFlow<OnboardingBackgroundViewState>(
        OnboardingBackgroundViewState.StaticContendBackground(slideList.first())
    )
    val featureFlagDynamicContentFlow = _featureFlagDynamicContentFlow

    var screenInForegroundTime by Delegates.notNull<Long>()

    init {
        getFeatureFlagDynamicContent()
    }

    private fun getFeatureFlagDynamicContent() {
        viewModelScope.launch {
            featureFlagDynamicContentUseCase.execute().collectLatest {
                when (it) {
                    true -> {
                        _featureFlagDynamicContentFlow.emit(OnboardingBackgroundViewState.SliderImagesBackground)
                        /**
                         * @prepareVideoBackground should be used for video background instead of image slider.
                         * */
                    }
                    false -> {
                        _featureFlagDynamicContentFlow.emit(
                            OnboardingBackgroundViewState.StaticContendBackground(slideList.first())
                        )
                    }
                }
            }
        }
    }

    fun trackAnalyticsEventTimeSpentInOnboarding() {
        eatersAnalyticsTracker.logEvent(
            eventName = Constants.EVENT_TIME_SPENT_IN_ONBOARDING,
            params = mutableMapOf<String, String>().apply {
                put("seconds", calculateSpentSecondsAmount())
            }
        )
    }

    fun onViewCreated() {
        screenInForegroundTime = System.currentTimeMillis()
    }

    private fun calculateSpentSecondsAmount(): String {
        val startTime = Date(screenInForegroundTime)
        val endTime = Date()
        val seconds = (endTime.time - startTime.time) / 1000;
        return "$seconds sec"
    }

    private suspend fun prepareVideoBackground() {
        getOnboardingVideoPathUseCase.execute().apply {
            _featureFlagDynamicContentFlow.emit(OnboardingBackgroundViewState.VideoBackground(this))
        }
    }
}