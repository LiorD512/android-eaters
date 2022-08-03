package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.common

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.BottomSheetConfirmationBinding
import com.eatwoodspoon.android_utils.binding.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shared.presentation.dialog.bottomsheet.ActionListBottomSheetFragment
import kotlinx.parcelize.Parcelize

open class ConfirmationBottomSheet : BottomSheetDialogFragment() {

    @Parcelize
    data class ConfirmationArguments(
        val title: String? = null,
        val subject: String? = null,
        val primaryBtn: ConfirmationAction,
        val secondaryBtn: ConfirmationAction
    ) : Parcelable

    @Parcelize
    data class ConfirmationAction(
        val type: ConfirmationButtonType = ConfirmationButtonType.PRIMARY,
        val title: String
    ) : Parcelable {
        enum class ConfirmationButtonType {
            PRIMARY,
            SECONDARY
        }
    }

    private val binding by viewBinding(BottomSheetConfirmationBinding::bind)

    override fun getTheme() = R.style.BottomSheetTransparentStyle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.bottom_sheet_confirmation,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments?.getParcelable<ConfirmationArguments>(ARG_ARGUMENTS)
        initUi(args)
    }

    private fun initUi(confirmationArgs: ConfirmationArguments?) {
        binding.apply {
            confirmationArgs?.let { confirmationArgs ->
                confirmationBsTitle.text = confirmationArgs.title
                confirmationBsSubject.text = confirmationArgs.subject
                confirmationBsPrimaryBtn.setText(confirmationArgs.primaryBtn.title)
                confirmationBsPrimaryBtn.setOnClickListener {
                    onActionSelected(confirmationArgs.primaryBtn)
                }
                confirmationBsSecondaryBtn.setText(confirmationArgs.secondaryBtn.title)
                confirmationBsSecondaryBtn.setOnClickListener {
                    onActionSelected(confirmationArgs.secondaryBtn)
                }
            }
        }
    }


    fun showWithResultListener(
        manager: FragmentManager,
        tag: String? = null,
        lifecycleOwner: LifecycleOwner,
        listener: ((ConfirmationAction) -> Unit)
    ) {
        manager.setFragmentResultListener(RESULT_REQUEST_KEY, lifecycleOwner) { _, resultBundle ->
            resultBundle.getParcelable<ConfirmationAction>(
                ActionListBottomSheetFragment.RESULT_BUNDLE_KEY
            )?.let {
                listener.invoke(it)
            }
        }

        show(manager, tag)
    }

    open fun onActionSelected(action: ConfirmationAction) {
        parentFragmentManager.setFragmentResult(
            RESULT_REQUEST_KEY, bundleOf(
                RESULT_BUNDLE_KEY to action
            )
        )
        dismiss()
    }

    companion object {

        const val ARG_ARGUMENTS = "arg_arguments"
        const val RESULT_REQUEST_KEY = "result_request_key"
        const val RESULT_BUNDLE_KEY = "result_bundle_key"

        fun newInstance(confirmationArgs: ConfirmationArguments): ConfirmationBottomSheet =
            ConfirmationBottomSheet().apply {
                arguments = bundleOf(
                    ARG_ARGUMENTS to confirmationArgs
                )
            }
    }
}