package com.bupp.wood_spoon_chef.presentation.dialogs.custom_string_chooser

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.presentation.custom_views.SimpleTextWatcher
import com.bupp.wood_spoon_chef.presentation.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_chef.databinding.CustomStringChooserBottomSheetBinding
import com.bupp.wood_spoon_chef.data.remote.model.CuisineIcon
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


class CustomStringChooserBottomSheet : TopCorneredBottomSheet(),
    CustomStringChooserAdapter.CustomStringChooserListener {

    var listener: CustomStringChooserListener? = null
    var selectedCuisine: CuisineIcon? = null
    interface CustomStringChooserListener{
        fun onCuisineSelected(country: CuisineIcon)
    }

    companion object{
        private const val CUSTOM_STRING_CHOOSER_CUISINE = "custom_String_chooser"
        fun newInstance(customData: List<CuisineIcon>, listener: CustomStringChooserListener, selectedCuisine: CuisineIcon? = null): BottomSheetDialogFragment{
            return CustomStringChooserBottomSheet().apply {
                this.listener = listener
                this.selectedCuisine = selectedCuisine
                arguments = Bundle().apply {
                    putString(CUSTOM_STRING_CHOOSER_CUISINE, Gson().toJson(customData))
                }
            }
        }
    }

    private lateinit var rawData: List<CuisineIcon>
    private lateinit var adapterData: List<String>
    private var adapter: CustomStringChooserAdapter? = null
    private var binding: CustomStringChooserBottomSheetBinding ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.custom_string_chooser_bottom_sheet, container, false)
        arguments?.let{
            val listString = requireArguments().getString(CUSTOM_STRING_CHOOSER_CUISINE)
            val cuisine = Gson().fromJson<List<CuisineIcon>>(listString, object : TypeToken<List<CuisineIcon>>() {}.type)
            cuisine.let{ list->
                rawData = list
                adapterData = list.map { it.name }
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = CustomStringChooserBottomSheetBinding.bind(view)

        setFullScreenDialog()

        initUi()
//        initObservers()
    }


    private fun initUi() {
        binding?.apply{
            adapter = CustomStringChooserAdapter(this@CustomStringChooserBottomSheet)
            customChooserBottomSheetList.layoutManager = LinearLayoutManager(requireContext())
            customChooserBottomSheetList.adapter = adapter
            val dividerItemDecoration = DividerItemDecorator(
                ContextCompat.getDrawable(requireContext(), R.drawable.divider)
            )
            customChooserBottomSheetList.addItemDecoration(dividerItemDecoration)


            adapter?.submitList(adapterData)

            customChooserBottomSheetInput.addTextChangedListener(object: SimpleTextWatcher(){
                override fun afterTextChanged(s: Editable) {
                    super.afterTextChanged(s)
                    val input = s.toString()
                    val filtered = adapterData.filter { it.toLowerCase(Locale.ROOT).contains(input.toLowerCase(Locale.ROOT)) }
                    adapter?.submitList(filtered)
                }
            })

            customChooserBottomSheetClose.setOnClickListener {
                dismiss()
            }

            selectedCuisine?.let{
                adapter?.setSelected(it.name)
            }
        }

    }

    override fun onStringSelected(selected: String) {
        val selectedCuisine = rawData.find { it.name == selected }
        selectedCuisine?.let{
            listener?.onCuisineSelected(it)
            dismiss()
        }
    }

    override fun clearClassVariables() {
        adapter = null
        binding = null
    }

}