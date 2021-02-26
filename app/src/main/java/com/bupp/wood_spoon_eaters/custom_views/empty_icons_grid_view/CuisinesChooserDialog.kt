package com.bupp.wood_spoon_eaters.custom_views.empty_icons_grid_view

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.model.SelectableIcon
import kotlinx.android.synthetic.main.fragment_cuisines_chooser.*
import kotlin.collections.ArrayList

class CuisinesChooserDialog(val listener: CuisinesChooserListener, val cuisine: List<SelectableIcon>, val choiceCount: Int) : DialogFragment(), HeaderView.HeaderViewListener {

    private var selectedCuisine: ArrayList<SelectableIcon>? = null

    interface CuisinesChooserListener{
        fun onCuisineChoose(selectedCuisines: List<SelectableIcon>)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cuisines_chooser, null)
        getDialog()?.getWindow()?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)));
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cuisineChooserFragHeader.setHeaderViewListener(this)
        cuisineChooserFragGrid.initIconsGrid(cuisine, choiceCount)
        if (selectedCuisine != null) {
            cuisineChooserFragGrid.loadSelectedIcons(selectedCuisine)
        }
}

    public fun setSelectedCuisine(selectedCuisine: ArrayList<SelectableIcon>?){
        this.selectedCuisine = selectedCuisine
    }


//    override fun onDismiss(dialog: DialogInterface?) {
//        super.onDismiss(dialog)
//        Log.d("wow","WelcomeDialog dismiss")
//    }


    override fun onHeaderCloseClick() {
        dismiss()
    }
    override fun onHeaderSaveClick() {
    }

    override fun onHeaderDoneClick() {
        listener.onCuisineChoose(cuisineChooserFragGrid.getSelectedItems())
        dismiss()
    }

    override fun onHeaderNextClick() {
    }

    override fun onHeaderSettingsClick() {
    }


    fun setPreviousData(selectedCuisineId: Long?) {
        val selectedArr = arrayListOf<SelectableIcon>()
        selectedCuisineId?.let{
            for(icon in cuisine){
                if(icon.id == selectedCuisineId){
                    selectedArr.add(icon)
                    setSelectedCuisine(selectedArr)
                }
            }
        }
    }
}