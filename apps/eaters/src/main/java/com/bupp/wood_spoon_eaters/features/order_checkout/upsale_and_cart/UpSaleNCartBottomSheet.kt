package com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart;


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.UpSaleNCartBottomSheetBinding
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressActivity
import com.bupp.wood_spoon_eaters.features.upsale.CartFragment
import com.bupp.wood_spoon_eaters.features.upsale.UpSaleFragment
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.eatwoodspoon.analytics.events.UpsaleEvent
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UpSaleNCartBottomSheet() : BottomSheetDialogFragment(), UpSaleFragment.UpSaleListener,
    CartFragment.CartListener {

    lateinit var listener: UpsaleNCartBSListener

    interface UpsaleNCartBSListener {
        fun refreshParentOnCartCleared() {}
        fun onCartDishCLick(customOrderItem: CustomOrderItem)
        fun onCartDishCLick(menuItem: MenuItem)
        fun onGoToCheckoutClicked()
    }

    private var binding: UpSaleNCartBottomSheetBinding? = null
    private val viewModel by sharedViewModel<UpSaleNCartViewModel>()


    private val addLocationResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.updateAddressAndProceedToCheckout()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.up_sale_n_cart_bottom_sheet, container, false)
        binding = UpSaleNCartBottomSheetBinding.bind(view)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        return dialog
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.top_cornered_30_bkg)
        observeEvents()
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.upSaleAndCartFragmentContainer, CartFragment())
                .commit()
        }
    }


    private fun observeEvents() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        UpSaleAndCartEvents.OpenUpSaleDialog -> openUpSaleFragment()
                        UpSaleAndCartEvents.GoToCheckout -> {
                            listener.onGoToCheckoutClicked()
                            dismiss()
                        }
                        UpSaleAndCartEvents.GoToSelectAddress -> {
                            addLocationResult.launch(
                                Intent(
                                    requireContext(),
                                    LocationAndAddressActivity::class.java
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun openUpSaleFragment() {
        childFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_right_enter,
                R.anim.slide_right_exit
            )
            replace(R.id.upSaleAndCartFragmentContainer, UpSaleFragment())
            addToBackStack(null)
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is UpsaleNCartBSListener) {
            listener = context
        } else if (parentFragment is UpsaleNCartBSListener) {
            this.listener = parentFragment as UpsaleNCartBSListener
        } else {
            throw ClassCastException("$context must implement UpsaleNCartBSListener")
        }
    }

    override fun onBackButtonClick() {
        childFragmentManager.popBackStack()
    }

    override fun onUpSaleButtonClick() {
        listener.onGoToCheckoutClicked()
        dismiss()
    }

    override fun onCartItemClick(menuItem: MenuItem) {
        listener.onCartDishCLick(menuItem)
    }

    override fun onDishCartClicked(customCartItem: CustomOrderItem) {
        listener.onCartDishCLick(customCartItem)
    }
}