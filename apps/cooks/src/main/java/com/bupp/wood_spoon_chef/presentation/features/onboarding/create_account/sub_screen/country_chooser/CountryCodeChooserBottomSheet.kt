package com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account.sub_screen.country_chooser

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.presentation.custom_views.SimpleTextWatcher
import com.bupp.wood_spoon_chef.presentation.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_chef.databinding.BottomSheetCountryCodeChooserBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseBottomSheetDialogFragment
import com.bupp.wood_spoon_chef.presentation.features.onboarding.login.LoginViewModel
import com.bupp.wood_spoon_chef.data.remote.model.CountriesISO
import com.bupp.wood_spoon_chef.utils.CountryCodeUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import me.ibrahimsn.lib.Countries
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*


class CountryCodeChooserBottomSheet : BaseBottomSheetDialogFragment(),
    CountryIsoChooserAdapter.AddressChooserAdapterListener {

    var listener: CountryCodeListener? = null
    interface CountryCodeListener{
        fun onCountryCodeSelected(country: CountriesISO)
    }

    fun setCountryCodeListener(listener: CountryCodeListener){
        this.listener = listener
    }

    var adapter: CountryIsoChooserAdapter? = null
    private var binding: BottomSheetCountryCodeChooserBinding ? = null
    val viewModel by sharedViewModel<LoginViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_country_code_chooser, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }

    private lateinit var behavior: BottomSheetBehavior<View>
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(sheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            behavior.isFitToContents = true
//            behavior.expandedOffset = Utils.toPx(230)
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = BottomSheetCountryCodeChooserBinding.bind(view)

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.bottom_sheet_bkg)

        initUi()
        initObservers()
    }


    private fun initUi() {
        binding?.apply{
            adapter = CountryIsoChooserAdapter(this@CountryCodeChooserBottomSheet)
            countryCodeBottomSheetList.layoutManager = LinearLayoutManager(requireContext())
            countryCodeBottomSheetList.adapter = adapter
            val dividerItemDecoration = DividerItemDecorator(
                ContextCompat.getDrawable(requireContext(), R.drawable.divider)
            )
            countryCodeBottomSheetList.addItemDecoration(dividerItemDecoration)

            val countriesISO = mutableListOf<CountriesISO>()
            Countries.list.forEach {
                countriesISO.add(
                    CountriesISO(
                        name = it.name,
                        value = it.iso2.toUpperCase(Locale.ROOT),
                        country_code = it.countryCode.toString(),
                        flag = CountryCodeUtils.countryCodeToEmojiFlag(it.iso2.toUpperCase(Locale.ROOT))
                    )
                )
            }
            adapter?.submitList(countriesISO)

            countryCodeBottomSheetInput.addTextChangedListener(object: SimpleTextWatcher(){
                override fun afterTextChanged(s: Editable) {
                    super.afterTextChanged(s)
                    val input = s.toString()
                    val filtered = countriesISO.filter { it.name?.toLowerCase(Locale.ROOT)?.contains(input.toLowerCase(Locale.ROOT)) ?: false }
                    adapter?.submitList(filtered)
                }
            })

            countryCodeBottomSheetClose.setOnClickListener {
                dismiss()
            }
        }

    }

    private fun initObservers() {
        viewModel.countryCodeEvent.observe(viewLifecycleOwner, {

        })
    }

    override fun onCountryCodeSelected(selected: CountriesISO) {
        viewModel.onCountryCodeSelected(selected)
        dismiss()
    }

    override fun clearClassVariables() {
        adapter = null
        binding = null
    }

}
