package com.bupp.wood_spoon_chef.presentation.features.main.my_dishes.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.databinding.MyDishesItemMenuBottomSheetBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseBottomSheetDialogFragment
import com.bupp.wood_spoon_chef.data.remote.model.DishStatus
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class MyDishItemMenuBottomSheet : BaseBottomSheetDialogFragment() {

    private var binding: MyDishesItemMenuBottomSheetBinding? = null
    private lateinit var behavior: BottomSheetBehavior<View>
    var listener: DishChooserListener? = null

    interface DishChooserListener {
        fun onDishChooserResult(result: Int)
    }

    companion object {
        private const val MSG_PARAM = "DishChooserDialog"

        fun newInstance(status: DishStatus?): MyDishItemMenuBottomSheet {
            val fragment = MyDishItemMenuBottomSheet()
            val args = Bundle()
            status?.let {
                args.putParcelable(MSG_PARAM, it)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(sheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.popup_window_transparent)

        initUi()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.my_dishes_item_menu_bottom_sheet, null)
        binding = MyDishesItemMenuBottomSheetBinding.bind(view)

        arguments?.let {
            when (requireArguments().getParcelable<DishStatus>(MSG_PARAM) ?: "") {
                DishStatus.DRAFT -> {
                    binding!!.dishChooserDialogActivate.visibility = View.GONE
                    binding!!.dishChooserDialogUnPublish.visibility = View.GONE
                }
                DishStatus.ACTIVE -> {
                    binding!!.dishChooserDialogUnPublish.visibility = View.VISIBLE
                }
                DishStatus.HIDDEN -> {
                    binding!!.dishChooserDialogActivate.visibility = View.VISIBLE
                }
            }
        }
        return view
    }

    private fun initUi() {
        with(binding!!) {
            dishChooserDialogBkg.setOnClickListener {
                dismiss()
            }
            dishChooserDialogClose.setOnClickListener {
                dismiss()
            }
            dishChooserDialogHistory.setOnClickListener {
                listener?.onDishChooserResult(Constants.DISH_CHOOSER_HISTORY)
                dismiss()
            }
            dishChooserDialogEdit.setOnClickListener {
                listener?.onDishChooserResult(Constants.DISH_CHOOSER_EDIT)
                dismiss()
            }
            dishChooserDialogHide.setOnClickListener {
                listener?.onDishChooserResult(Constants.DISH_CHOOSER_HIDE)
                dismiss()
            }
            dishChooserDialogActivate.setOnClickListener {
                listener?.onDishChooserResult(Constants.DISH_CHOOSER_ACTIVATE)
                dismiss()
            }
            dishChooserDialogUnPublish.setOnClickListener {
                listener?.onDishChooserResult(Constants.DISH_CHOOSER_UNPUBLISH)
                dismiss()
            }
            dishChooserDialogDuplicate.setOnClickListener {
                listener?.onDishChooserResult(Constants.DISH_CHOOSER_DUPLICATE)
                dismiss()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        when {
            context is DishChooserListener -> {
                listener = context
            }
            parentFragment is DishChooserListener -> {
                this.listener = parentFragment as DishChooserListener
            }
            else -> {
                throw RuntimeException("$context must implement DishChooserListener")
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun clearClassVariables() {
        binding = null
    }
}