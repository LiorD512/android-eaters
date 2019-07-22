package com.bupp.wood_spoon_eaters.dialogs.locationAutoComplete

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.dialogs.AddressAutoCompleteAdapter
import com.taliazhealth.predictix.network_google.models.google_api.AddressIdResponse
import com.bupp.wood_spoon_eaters.network.google.models.GoogleAddressResponse
import kotlinx.android.synthetic.main.fragment_chooser.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LocationChooserFragment(val listener: LocationChooserFragmentListener, val input: String?): DialogFragment(),
    AddressAutoCompleteAdapter.AddressAutoCompleteAdapterListener {

    val viewModel by viewModel<LocationChooserViewModel>()
    var adapter: AddressAutoCompleteAdapter? = null

    interface LocationChooserFragmentListener {
        fun onLocationSelected(selected: GoogleAddressResponse?)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chooser, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initObservers()
        openKeyboard()

    }

    private fun initUi() {
        chooserFragInput.requestFocus()

        chooserFragCloseBtn.setOnClickListener { closeChooser() }
        chooserFragInputClean.setOnClickListener { cleanInput() }

        val decoration = DividerItemDecoration(context, VERTICAL)
        ContextCompat.getDrawable(context!!, R.drawable.chooser_divider)?.let { decoration.setDrawable(it) }
        chooserFragList.addItemDecoration(decoration)

        chooserFragList.layoutManager = LinearLayoutManager(context)
        adapter = AddressAutoCompleteAdapter(context!!, this)
        chooserFragList.adapter = adapter

        chooserFragInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty()) {
                    viewModel.search(s.toString())
                    chooserFragInputClean.visibility = View.VISIBLE
                } else {
                    adapter!!.clean()
                    chooserFragInputClean.visibility = View.GONE
                }
            }
        })

        if(input != null){
            chooserFragInput.setText(input)
        }
    }

    private fun initObservers() {
        viewModel.googleAddressResponse.observe(this,Observer { addressIdResponse -> handleAddressResponse(addressIdResponse) })
        viewModel.queryEvent.observe(this, Observer { event ->
            if (event != null) {
                handleAutoCompleteResponse(event.response, event.query)
            }
        })
    }


    private fun handleAutoCompleteResponse(addressIdResponse: AddressIdResponse?, query: String) {
        adapter?.refreshList(addressIdResponse, query)
    }

    private fun handleAddressResponse(googleAddressResponse: GoogleAddressResponse?) {
        closeKeyboard(chooserFragInput)
        listener.onLocationSelected(googleAddressResponse!!)
        dismiss()
    }

    private fun cleanInput() {
        chooserFragInput.setText("")
        adapter!!.clean()
    }

    override fun onItemClick(selected: AddressIdResponse.PredictionsItem?) {
        viewModel.fetchAddress(selected?.placeId)
    }

    private fun closeKeyboard(focusedView: View) {
        focusedView.clearFocus()

        val imm = focusedView.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(focusedView.windowToken, 0)
    }

    private fun openKeyboard() {
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    private fun closeChooser() {
        closeKeyboard(chooserFragInput)
        listener.onLocationSelected(null)
        dismiss()
    }
}