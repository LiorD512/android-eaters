package com.bupp.wood_spoon_eaters.custom_views.cuisine_chooser

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.databinding.FragmentCuisinesChooserBinding
import com.bupp.wood_spoon_eaters.model.SelectableIcon
import org.koin.androidx.viewmodel.ext.android.viewModel

class CuisinesChooserDialog(val listener: CuisinesChooserListener) : DialogFragment(), HeaderView.HeaderViewListener {

    private lateinit var adapter: CuisineChooserAdapter
    val binding: FragmentCuisinesChooserBinding by viewBinding()
    val viewModel by viewModel<CuisineChooserViewModel>()

    interface CuisinesChooserListener{
        fun onCuisineChoose(selectedCuisines: List<SelectableIcon>)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cuisines_chooser, null)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initObservers()
}

    private fun initObservers() {
        viewModel.cuisineLiveData.observe(viewLifecycleOwner, {
            adapter.submitList(it.allCuisines)
            it.selectedCuisine?.let{
                adapter.setSelected(it.toMutableList())
            }
        })
    }

    private fun initUi() {
        with(binding){

            cuisineChooserHeader.setHeaderViewListener(this@CuisinesChooserDialog)

            adapter = CuisineChooserAdapter(requireContext())
            cuisineChooserList.layoutManager = LinearLayoutManager(requireContext())
            cuisineChooserList.addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(requireContext(), R.drawable.divider)))
            cuisineChooserList.adapter = adapter
        }
    }

    override fun onHeaderBackClick() {
        listener.onCuisineChoose(adapter.selectedCuisines)
        dismiss()
    }



}