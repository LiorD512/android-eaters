package com.bupp.wood_spoon_eaters.dialogs


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.dialogs.abs.AutoCompleteListAdapter
import com.bupp.wood_spoon_eaters.model.SelectableString


class ChooserFragment(val listener: ChooserFragmentListener, var dataList: ArrayList<SelectableString>, val title: String) : DialogFragment(), AutoCompleteListAdapter.AutoCompleteListAdapterListener {


    var adapter: AutoCompleteListAdapter? = null
    interface ChooserFragmentListener{
        fun onChooserFragSelected(chosenItem: SelectableString)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_chooser, null)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        if(title.isEmpty()){
//            chooserFragTitle.visibility = View.GONE
//        }else{
//            chooserFragTitle.visibility = View.VISIBLE
//            chooserFragTitle.text = title
//        }
//        chooserFragCloseBtn.setOnClickListener { dismiss() }
//        chooserFragInputClean.setOnClickListener { cleanInput() }
//
//        val decoration = DividerItemDecoration(context, VERTICAL)
//        ContextCompat.getDrawable(context!!, R.drawable.chooser_divider)?.let { decoration.setDrawable(it) }
//        chooserFragList.addItemDecoration(decoration)
//
//        chooserFragList.setLayoutManager(LinearLayoutManager(context))
//        adapter = AutoCompleteListAdapter(context!!, dataList, this)
//        chooserFragList.adapter = adapter
//        chooserFragInput.addTextChangedListener(object: TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                if(!s!!.isEmpty()){
//                    adapter!!.sortList(s.toString())
//                }else{
//                    adapter!!.clean()
//                }
//            }
//        })
//        adapter!!.clean()
    }

    private fun cleanInput() {
//        chooserFragInput.setText("")
        adapter!!.clean()
    }

    override fun onItemClick(selected: SelectableString?) {
        listener?.onChooserFragSelected(selected!!)
        dismiss()
    }

   
    
}