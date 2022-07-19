package com.bupp.wood_spoon_chef.presentation.features.main.calendar.cookingSlotDetails

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.annotation.Keep
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.databinding.FragmentCookingSlotDetailsBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseFragment
import com.bupp.wood_spoon_chef.presentation.features.main.calendar.adapters.CookingSlotMenuAdapter
import com.bupp.wood_spoon_chef.presentation.features.main.calendar.create_cooking_slot.ArgumentModelCreateCookingSlot
import com.bupp.wood_spoon_chef.presentation.features.main.dialogs.CookingSlotChooserDialog
import com.bupp.wood_spoon_chef.presentation.features.main.dialogs.NationwideShipmentInfoDialog
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.utils.Utils
import com.bupp.wood_spoon_chef.utils.extensions.prepareFormattedDate
import kotlinx.parcelize.Parcelize
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.koin.androidx.viewmodel.ext.android.viewModel

@Parcelize
@Keep
data class ArgumentModelCookingSlotDetails(
    var cookingSlitsIds: List<Long> = emptyList(),
    var cookingSlitsDateMillis: Long,
) : Parcelable

@Deprecated("new design CookingSlotDetailsFragment screen. Delete this screen after implementing new one.")
class CookingSlotDetailsFragment : BaseFragment(R.layout.fragment_cooking_slot_details) {

    private var binding: FragmentCookingSlotDetailsBinding? = null
    private val viewModel: CookingSlotDetailsViewModel by viewModel()
    val args: CookingSlotDetailsFragmentArgs by navArgs()
    private var adapter: CookingSlotMenuAdapter? = null

    private lateinit var selectedCookingSlitsDate: DateTime
    private var cookingSlitsIds: MutableSet<Long> = mutableSetOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCookingSlotDetailsBinding.bind(view)
        parsNavArguments()

        setupUi()

        viewModel.allCookingSlotLiveData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                adapter?.updateList(it)
            } else {
                findNavController().popBackStack()
            }
        }
        viewModel.progressData.observe(viewLifecycleOwner) {
            handleProgressBar(it)
        }
        viewModel.errorEvent.observe(viewLifecycleOwner) {
            handleErrorEvent(it, binding?.root)
        }
    }

    private fun parsNavArguments() {
        args.cookingSlot.let { args ->
            selectedCookingSlitsDate = DateTime(args.cookingSlitsDateMillis)
            cookingSlitsIds.addAll(args.cookingSlitsIds)

            viewModel.fetchCookingSlotsByDate(selectedCookingSlitsDate)
        }
    }

    private fun setupUi() {
        binding?.let {
            context?.let { context ->
                adapter = CookingSlotMenuAdapter(context, mutableListOf(),
                    object : CookingSlotMenuAdapter.CalendarCookingSlotAdapterListener {
                        override fun onCookingSlotMenuClick(curCookingSlot: CookingSlot) {
                            onItemClick(curCookingSlot)
                        }

                        override fun onCookingSlotAddClick() {
                            findNavController().apply {
                                val action = CookingSlotDetailsFragmentDirections.
                                actionCookingSlotDetailsFragmentToCreateCookingSlotFragment(
                                    ArgumentModelCreateCookingSlot(
                                        selectedDateMillis = selectedCookingSlitsDate.millis
                                    )
                                )
                                navigate(action)
                            }
                        }

                        override fun onWorldwideInfoClick() {
                            NationwideShipmentInfoDialog().show(
                                childFragmentManager,
                                Constants.NATIONWIDE_SHIPPING_INFO_DIALOG
                            )
                        }

                        override fun onCookingSlotShareClick(cookingSlot: CookingSlot) {
                            val text = viewModel.getShareText(cookingSlot)
                            Utils.shareText(requireActivity(), text)
                        }

                    })
                it.rvSlots.adapter = adapter
                it.rvSlots.layoutManager = LinearLayoutManager(context)
            }
            it.calendarFragCalBack.setOnClickListener {
                findNavController().popBackStack()
            }
            it.calendarFragCalTitle.apply {
                text = selectedCookingSlitsDate.prepareFormattedDate()
            }
        }
    }

    fun onItemClick(cookingSlot: CookingSlot) {
        val cookingSlotChooser = CookingSlotChooserDialog(cookingSlot,
            object : CookingSlotChooserDialog.CookingSlotChooserListener {
                override fun onCancelCookingSlot(cookingSlot: CookingSlot) {
                    cookingSlot.id?.let { id ->
                        viewModel.cancelCookingSlot(id)
                    }
                }

                override fun onEditCookingSlot(cookingSlot: CookingSlot) {
                    findNavController().apply {
                        val action = CookingSlotDetailsFragmentDirections.
                        actionCookingSlotDetailsFragmentToCreateCookingSlotFragment(
                            ArgumentModelCreateCookingSlot(
                                selectedDateMillis = cookingSlot.startsAt.time,
                                editableCookingSlotId = cookingSlot.id
                            )
                        )
                        navigate(action)
                    }
                }

            }
        )
        cookingSlotChooser.show(childFragmentManager, Constants.COOKING_SLOT_CHOOSER_TAG)
    }

    override fun clearClassVariables() {
        binding = null
    }
}

