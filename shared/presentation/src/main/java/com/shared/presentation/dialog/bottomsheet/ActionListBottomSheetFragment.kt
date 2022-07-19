package com.shared.presentation.dialog.bottomsheet

import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shared.presentation.R
import com.shared.presentation.databinding.FragmentActionListBottomSheetListDialogBinding
import kotlinx.parcelize.Parcelize
import com.eatwoodspoon.android_utils.binding.viewBinding
import com.shared.presentation.databinding.FragmentActionListBottomSheetListDialogItemBinding
import com.shared.presentation.dialog.adapter.DividerItemDecorator

/**
 *
 * A fragment that shows a list of actions as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    ActionListBottomSheetFragment.newInstance(...).show(supportFragmentManager, "dialog")
 * </pre>
 */
open class ActionListBottomSheetFragment : BottomSheetDialogFragment() {

    @Parcelize
    data class ActionListArguments(
        val actions: List<Action>
    ) : Parcelable

    @Parcelize
    data class Action(
        val type: Type = Type.Default,
        val id: Int? = null,
        val text: String? = null,
        @StringRes val textId: Int? = null
    ) : Parcelable {
        enum class Type {
            Cancel, Default
        }
    }

    private val binding by viewBinding(FragmentActionListBottomSheetListDialogBinding::bind)

    override fun getTheme() = R.style.BottomSheetTransparentStyle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_action_list_bottom_sheet_list_dialog,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.list.layoutManager = LinearLayoutManager(context)
        val actionList = arguments?.getParcelable<ActionListArguments>(ARG_ARGUMENTS)
        actionList?.let {
            binding.list.adapter = ActionAdapter(actionList.actions)
            binding.list.addItemDecoration(
                DividerItemDecorator(
                    AppCompatResources.getDrawable(
                        view.context,
                        R.drawable.divider
                    )
                )
            )
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onActionSelected(defaultCancelAction)
    }

    open fun onActionSelected(action: Action) {
        parentFragmentManager.setFragmentResult(
            RESULT_REQUEST_KEY, bundleOf(
                RESULT_BUNDLE_KEY to action
            )
        )
        dismiss()
    }

    public fun showWithResultListener(
        manager: FragmentManager,
        tag: String? = null,
        lifecycleOwner: LifecycleOwner,
        listener: ((Action) -> Unit)
    ) {
        manager.setFragmentResultListener(RESULT_REQUEST_KEY, lifecycleOwner) { _, resultBundle ->
            resultBundle.getParcelable<Action>(RESULT_BUNDLE_KEY)?.let {
                listener.invoke(it)
            }
        }

        show(manager, tag)
    }

    private inner class ViewHolder internal constructor(binding: FragmentActionListBottomSheetListDialogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        internal val text: TextView = binding.text
    }

    private inner class ActionAdapter constructor(private val actions: List<Action>) :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            return ViewHolder(
                FragmentActionListBottomSheetListDialogItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val action = actions[position]
            with(holder.text) {
                text = action.text ?: action.textId?.let { getString(it) }
                setOnClickListener {
                    onActionSelected(action)
                }
            }
        }

        override fun getItemCount(): Int {
            return actions.size
        }
    }

    companion object {

        const val ARG_ARGUMENTS = "arg_arguments"
        const val RESULT_REQUEST_KEY = "result_request_key"
        const val RESULT_BUNDLE_KEY = "result_bundle_key"

        val defaultCancelAction = Action(
            type = Action.Type.Cancel,
            textId = R.string.action_cancel
        )

        fun newInstance(actions: ActionListArguments): ActionListBottomSheetFragment =
            ActionListBottomSheetFragment().apply {
                arguments = bundleOf(
                    ARG_ARGUMENTS to actions
                )
            }

        fun newInstance(vararg actions: Action) = newInstance(
            actions = ActionListArguments(
                actions = actions.asList()
            )
        )
    }
}
