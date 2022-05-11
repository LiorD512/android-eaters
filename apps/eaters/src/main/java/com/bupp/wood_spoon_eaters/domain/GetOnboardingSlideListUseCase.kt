package com.bupp.wood_spoon_eaters.domain

import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.onboarding.SliderContentPage
import com.bupp.wood_spoon_eaters.domain.comon.UseCase

class GetOnboardingSlideListUseCase : UseCase<List<SliderContentPage>, Nothing?> {

    override fun execute(params: Nothing?): List<SliderContentPage> = listOf(
        SliderContentPage(R.drawable.bg_onboarding_image_1, R.string.text_title_onboarding_1, R.string.text_description_onboarding_1),
        SliderContentPage(R.drawable.bg_onboarding_image_2, R.string.text_title_onboarding_2, R.string.text_description_onboarding_2),
        SliderContentPage(R.drawable.bg_onboarding_image_3, R.string.text_title_onboarding_3, R.string.text_description_onboarding_3),
    )
}