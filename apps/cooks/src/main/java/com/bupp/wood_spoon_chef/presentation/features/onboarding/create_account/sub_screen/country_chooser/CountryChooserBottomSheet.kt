package com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account.sub_screen.country_chooser

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.presentation.custom_views.SimpleTextWatcher
import com.bupp.wood_spoon_chef.databinding.BottomSheetCountryChooserBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseDialogFragment
import com.bupp.wood_spoon_chef.data.remote.model.Country
import java.util.*

class CountryChooserBottomSheet(var countryList: List<Country>, val title: String, val listener: CountyChooserBottomSheetListener) :
    BaseDialogFragment(R.layout.bottom_sheet_country_chooser), CountryAdapter.CountryAdapterListener {

    var adapter: CountryAdapter? = CountryAdapter(this)
    var binding: BottomSheetCountryChooserBinding? = null

    interface CountyChooserBottomSheetListener{
        fun onCountrySelected(country: Country)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parent = view.parent as View
        binding = BottomSheetCountryChooserBinding.bind(view)
        parent.setBackgroundResource(R.drawable.top_cornered_bkg)
        initUi()
    }

    fun initUi(){
        with(binding!!){
            chooserFragCloseBtn.setOnClickListener { dismiss() }
            chooserFragTitle.text = title
            chooserFragList.adapter = adapter
            adapter?.submitList(countryList)

            countryCodeBottomSheetInput.addTextChangedListener(object: SimpleTextWatcher(){
                override fun afterTextChanged(s: Editable) {
                    super.afterTextChanged(s)
                    val input = s.toString()
                    val filtered = countryList.filter { it.name.toLowerCase(Locale.ROOT).contains(input.toLowerCase(Locale.ROOT)) }
                    adapter?.submitList(filtered)
                }
            })

        }
    }

    override fun onDestroyView() {
        binding = null
        adapter = null
        super.onDestroyView()
    }

    override fun onCountryClick(country: Country) {
        listener.onCountrySelected(country)
        dismiss()
    }

    override fun clearClassVariables() {
        binding = null
    }

}