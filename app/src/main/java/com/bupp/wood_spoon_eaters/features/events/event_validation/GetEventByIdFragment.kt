package com.bupp.wood_spoon_eaters.features.events.event_validation


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.events.EventActivity
import com.bupp.wood_spoon_eaters.features.events.EventActivityViewModel
import kotlinx.android.synthetic.main.fragment_get_event_by_id.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.view.KeyEvent.KEYCODE_ENTER
import android.widget.TextView
import android.widget.EditText
import android.view.KeyEvent


class GetEventByIdFragment : Fragment() {

    val viewModel by sharedViewModel<EventActivityViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_get_event_by_id, container, false)
    }


    companion object {
        fun newInstance() = GetEventByIdFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initObservers()
    }

    private fun initObservers() {
        viewModel.errorEvent.observe(this, Observer { errorEvent ->
            if (errorEvent == EventActivityViewModel.ErrorEvent.WRONG_CODE) {
                Log.d("wowEventValidation", "request to join event failed")
                eventValidationFragError.visibility = View.VISIBLE
            }
        })

        viewModel.progressData.observe(this, Observer {
            (activity as EventActivity).handlePb(it)
        })
    }


    private fun initUi() {
        eventValidationFragInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (it.toString().length > 0) {
                        (activity as EventActivity).handleDoneBtnState(true)
                    } else {
                        (activity as EventActivity).handleDoneBtnState(false)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        eventValidationFragInput.setOnEditorActionListener(object: TextView.OnEditorActionListener{
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendEventCode()
                    return true;
                }
                return false;
            }

        })


    }

    fun sendEventCode() {
        val code = eventValidationFragInput.text.toString()
        viewModel.validateEventCode(code)
    }


}
