package com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account.sub_screen

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.databinding.FragmentSetupProfileBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseFragment
import com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account.CreateAccountViewModel
import com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account.sub_screen.country_chooser.CountryChooserBottomSheet
import com.bupp.wood_spoon_chef.data.remote.model.CookRequest
import com.bupp.wood_spoon_chef.data.remote.model.Country
import com.bupp.wood_spoon_chef.utils.AnimationUtil
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SetupProfileFragment : BaseFragment(R.layout.fragment_setup_profile),
    CountryChooserBottomSheet.CountyChooserBottomSheetListener {

    var binding: FragmentSetupProfileBinding? = null
    val viewModel by sharedViewModel<CreateAccountViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSetupProfileBinding.bind(view)

        initUi()
        initObservers()
    }

    private fun initUi() {
        with(binding!!) {
            setupProfileNext.setOnClickListener {
                if (allFieldsValid()) {
                    with(binding!!) {
                        val about = setupProfileAbout.text
                        val restaurantName = setupProfileRestaurantName.text
                        viewModel.saveProfile(restaurantName.toString(), about.toString())
                    }
                }
            }
            setupProfileFlagLayout.setOnClickListener {
                openSelectedCountryDialog()
            }
        }
    }

    private fun initObservers() {
        viewModel.currentUserLiveData.observe(viewLifecycleOwner, {
            loadUnSavedData(it)
        })
    }

    private fun loadUnSavedData(cookRequest: CookRequest?) {
        with(binding!!) {
            cookRequest?.let { it ->
                it.about.let {
                    setupProfileAbout.setText(it)
                }
                it.restaurantName.let {
                    setupProfileRestaurantName.setText(it)
                }
                it.countryId.let { countryId ->
                    viewModel.getCountries().find { it.id == countryId }?.let { country ->
                        selectCountry(country)
                    }
                }
            }
        }
    }

    private fun allFieldsValid(): Boolean {
        with(binding!!) {
            //restaurant name validation
            val hasName = setupProfileRestaurantName.text.isNotEmpty()
            if (!hasName) {
                AnimationUtil().shakeView(setupProfileRestaurantName, requireContext())
            }
            // restaurant about validation
            val hasAbout = setupProfileAbout.text.isNotEmpty()
            if (!hasAbout) {
                AnimationUtil().shakeView(setupProfileAbout, requireContext())
            }
            // country validation
            if (!hasCountry) {
                AnimationUtil().shakeView(setupProfileFlagLayout, requireContext())
            }
            return hasAbout && hasCountry
        }
    }

    private fun openSelectedCountryDialog() {
        val countryChooser = CountryChooserBottomSheet(
            viewModel.getCountries(),
            getString(R.string.country_chooser_title),
            this
        )
        countryChooser.show(childFragmentManager, Constants.CHOOSER_FRAG_TAG)
    }

    override fun onCountrySelected(country: Country) {
        selectCountry(country)
    }

    private var hasCountry = false
    private fun selectCountry(country: Country) {
        binding?.apply {
            hasCountry = true
            Glide.with(root).load(country.flagUrl).into(setupProfileFlag)
            viewModel.onCountrySelected(country)
        }
    }

    override fun clearClassVariables() {
        binding = null
    }
}