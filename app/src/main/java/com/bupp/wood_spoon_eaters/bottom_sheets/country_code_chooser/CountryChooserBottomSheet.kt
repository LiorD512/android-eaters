package com.bupp.wood_spoon_eaters.bottom_sheets.country_code_chooser

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.simpler_views.SimpleTextWatcher
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.databinding.CountryChooserBottomSheetBinding
import com.bupp.wood_spoon_eaters.features.login.LoginViewModel
import com.bupp.wood_spoon_eaters.model.CountriesISO
import com.bupp.wood_spoon_eaters.utils.CountryCodeUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import me.ibrahimsn.lib.Countries
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*


class CountryChooserBottomSheet : BottomSheetDialogFragment(), CountryIsoChooserAdapter.AddressChooserAdapterListener {

    var adapter: CountryIsoChooserAdapter? = null
    private val binding: CountryChooserBottomSheetBinding by viewBinding()
    val viewModel by sharedViewModel<LoginViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.country_chooser_bottom_sheet, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FloatingBottomSheetStyle)
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

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.bottom_sheet_bkg)

        initUi()
        initObservers()
    }


    private fun initUi() {
        with(binding){
            adapter = CountryIsoChooserAdapter(this@CountryChooserBottomSheet)
            countryCodeBottomSheetList.layoutManager = LinearLayoutManager(requireContext())
            countryCodeBottomSheetList.adapter = adapter
            val dividerItemDecoration = DividerItemDecorator(ContextCompat.getDrawable(requireContext(), R.drawable.divider))
            countryCodeBottomSheetList.addItemDecoration(dividerItemDecoration)

            val countriesISO = mutableListOf<CountriesISO>()
            Countries.list.forEach {
                countriesISO.add(
                    CountriesISO(
                        name = it.name,
                        value = it.iso2.uppercase(Locale.ROOT),
                        country_code = it.countryCode.toString(),
                        flag = CountryCodeUtils.countryCodeToEmojiFlag(it.iso2.uppercase(Locale.ROOT))
                    )
                )
            }
            adapter?.submitList(countriesISO)

            countryCodeBottomSheetInput.addTextChangedListener(object: SimpleTextWatcher(){
                override fun afterTextChanged(s: Editable) {
                    super.afterTextChanged(s)
                    val input = s.toString()
                    val filtered = countriesISO.filter { it.name?.lowercase(Locale.ROOT)?.contains(input.lowercase(Locale.ROOT)) ?: false }
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
            adapter?.setSelected(it)
        })
    }

    override fun onCountryCodeSelected(selected: CountriesISO) {
        viewModel.onCountryCodeSelected(selected)
        dismiss()
    }

    override fun onDestroyView() {
        adapter = null
        super.onDestroyView()
    }


}