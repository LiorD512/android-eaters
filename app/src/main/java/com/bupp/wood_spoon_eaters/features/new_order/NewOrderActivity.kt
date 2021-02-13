package com.bupp.wood_spoon_eaters.features.new_order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.bupp.wood_spoon.dialogs.AddressChooserDialog
import com.bupp.wood_spoon_eaters.dialogs.address_chooser.sub_screen.AddressMenuDialog
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.dialogs.ClearCartDialog
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.checkout.CheckoutFragment
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.promo_code.PromoCodeFragment
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.SingleDishFragment
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.network.google.models.GoogleAddressResponse
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.SingleDishHeader
import com.bupp.wood_spoon_eaters.dialogs.rating_dialog.RatingsDialog
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressActivity
import com.stripe.android.view.PaymentMethodsActivityStarter
import kotlinx.android.synthetic.main.activity_new_order.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.ArrayList


class NewOrderActivity : AppCompatActivity(),
    CheckoutFragment.CheckoutDialogListener, AddressChooserDialog.AddressChooserDialogListener,
    AddressMenuDialog.EditAddressDialogListener, ClearCartDialog.ClearCartDialogListener,
    SingleDishHeader.SingleDishHeaderListener {

    companion object{
        const val TAG = "wowNewOrderAct"
    }

    //activityLauncher Results
    private val startAddressChooserForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        Log.d(TAG,"Activity For Result")
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
        }
    }

    private val currentDisplayingFragment: ArrayList<String> = arrayListOf()
    val viewModel by viewModel<NewOrderMainViewModel>()

    private val BACK_STACK_ROOT_TAG = "first_frag_tag"
    private var subscreensOnTheStack: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_order)

        initUi()
        initObservers()
//        checkActivityIntent()

        viewModel.initNewOrderActivity(intent)

    }

    private fun initUi() {
        newOrderHeader.setSingleDishHeaderListener(this)
    }

    override fun onBackClick() {

    }

    override fun onPageClick(page: Int) {
        when (page) {
            SingleDishHeader.INFO -> {
                findNavController(R.id.newOrderContainer).navigate(R.id.singleDishInfoFragment)
//                singleDishStatusBar.handleBottomBar(true)
            }
            SingleDishHeader.COOK -> {
                findNavController(R.id.newOrderContainer).navigate(R.id.singleDishCookFragment)
            }
            SingleDishHeader.INGREDIENT -> {
                findNavController(R.id.newOrderContainer).navigate(R.id.singleDishIngredientsFragment)
            }
        }
    }

    private fun initObservers() {
        viewModel.ephemeralKeyProvider.observe(this, { event ->
            if (!event.isSuccess) {
                Toast.makeText(this, "Error while loading payments method", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.orderStatusEvent.observe(this, Observer { event ->
            if (event.hasActiveOrder) {
//                ClearCartDialog(this).show(supportFragmentManager, Constants.CLEAR_CART_DIALOG_TAG)
            } else {
                viewModel.initNewOrder()
            }
        })

        viewModel.navigationEvent.observe(this, Observer { event ->
            event?.let {
                if(event.menuItemId != (-1).toLong()) {
                    viewModel.checkOrderStatus()
                    loadSingleDish(event.menuItemId)
                }
                if (event.isCheckout) {
//                    onCheckout()
                } else if (event.menuItemId == (-1).toLong() && !event.isCheckout) {
                    finish()
                }
            }
        })









        viewModel.actionEvent.observe(this, Observer{
            when(it){
                NewOrderMainViewModel.NewOrderActionEvent.OPEN_ADDRESS_CHOOSER -> {
                    Log.d(TAG, "OPEN_ADDRESS_CHOOSER")
                    startAddressChooserForResult.launch(Intent(this, LocationAndAddressActivity::class.java)
                        .putExtra(Constants.START_WITH, Constants.START_WITH_ADDRESS_CHOOSER))
                }
            }
        })

        viewModel.getReviewsEvent.observe(this, Observer{
            it?.let{
                RatingsDialog(it).show(supportFragmentManager, Constants.RATINGS_DIALOG_TAG)
            }
        })
    }






















    override fun onClearCart() {
//        viewModel.initNewOrder()
    }

    private fun checkActivityIntent() {
        val menuItemId = intent.getLongExtra("menuItemId", -1)
        val isCheckout = intent.getBooleanExtra("isCheckout", false)
        val isEvent = intent.getBooleanExtra("isEvent", false)
        viewModel.setIntentParams(menuItemId, isCheckout, isEvent)
    }

    fun startPaymentMethodActivity() {
        PaymentMethodsActivityStarter(this).startForResult(PaymentMethodsActivityStarter.Args.Builder().build())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PaymentMethodsActivityStarter.REQUEST_CODE -> {
                    Log.d("wowNewOrder", "Stripe")
                    val result = PaymentMethodsActivityStarter.Result.fromIntent(data)
                    result?.let {
                        Log.d("wowNewOrder", "payment method success")
                        if (getFragmentByTag(Constants.CHECKOUT_TAG) != null) {
                            result.paymentMethod?.let{
                                (getFragmentByTag(Constants.CHECKOUT_TAG) as CheckoutFragment).updateCustomerPaymentMethod(it)
                            }
                        }
                    }
                }
                Constants.ADDRESS_CHOOSER_REQUEST_CODE ->{
                    if (getFragmentByTag(Constants.CHECKOUT_TAG) as CheckoutFragment? != null) {
                        (getFragmentByTag(Constants.CHECKOUT_TAG) as CheckoutFragment).onAddressChooserSelected()
                    }
                }
            }
        }
    }


    //Single Dish
    fun loadSingleDish(menuItemId: Long) {
//        loadFragment(SingleDishFragment.newInstance(menuItemId, viewModel.isEvent), Constants.SINGLE_DISH_TAG)
    }

//    override fun onDishClick(itemId: Long) {
////        loadFragment(SingleDishFragment.newInstance(itemId, viewModel.isEvent), Constants.SINGLE_DISH_TAG)
//    }

    fun loadPromoCode() {
        loadFragment(PromoCodeFragment(), Constants.PROMO_CODE_TAG)
    }

    //Checkout
//    override fun onCheckout() {
////        loadFragment(CheckoutFragment(this), Constants.CHECKOUT_TAG)
//    }

    override fun onCheckoutDone() {
        intent.putExtra("isAfterPurchase", true)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onCheckoutCanceled() {
        viewModel.loadPreviousDish()
//        loadSingleDish()
//        setResult(Activity.RESULT_CANCELED)
//        finish()
    }


    private fun loadFragment(fragment: Fragment, tag: String) {
        val fragManger = supportFragmentManager
        fragManger.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        currentDisplayingFragment.add(tag)
        fragManger.beginTransaction()
            .setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_right,
                R.anim.enter_from_right,
                R.anim.exit_to_right
            )
            .replace(R.id.newOrderContainer, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is SingleDishFragment) {
//            fragment.setSingleDishDialogListener(this)
        }
    }

    private fun getFragmentByTag(tag: String): Fragment? {
        val fragmentManager = this@NewOrderActivity.supportFragmentManager
        val fragments = fragmentManager.fragments
        for (fragment in fragments) {
            if (fragment.tag == tag)
                return fragment
        }
        return null
    }

    override fun onBackPressed() {
        val count = currentDisplayingFragment.size
        Log.d("wowNewOrder", "onBackPress num of frag: $count")
        if (count > 1) {
            super.onBackPressed()
            currentDisplayingFragment.removeAt(count - 1)
            Log.d("wowNewOrder", "onBackPress num of frag: $count")
        } else {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
//        popOffSubscreens()
//        supportFragmentManager.popBackStack()
    }

    fun popOffSubscreens() {
        while (subscreensOnTheStack > 0) {
            supportFragmentManager.popBackStackImmediate()
        }
    }


//    fun loadAddressesDialog() {
//        startActivityForResult(Intent(this, AddressChooserActivity::class.java), Constants.ADDRESS_CHOOSER_REQUEST_CODE)
////        AddressChooserDialog(this, viewModel.getListOfAddresses(), viewModel.getChosenAddress()).show(
////            supportFragmentManager,
////            Constants.ADDRESS_DIALOG_TAG
////        )
//    }

    override fun onAddressMenuClick(address: Address) {
        AddressMenuDialog(address, this).show(supportFragmentManager, Constants.EDIT_ADDRESS_DIALOG)
    }

//    override fun onLocationSelected(selected: GoogleAddressResponse?) {
//        if (getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) != null && selected != null) {
////            (getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) as AddOrEditAddressFragment).onLocationSelected(selected) //ny
//        }
//    }
//
//    fun loadLocationChooser(input: String?) {
//        LocationChooserFragment(this, input)
//            .show(supportFragmentManager, Constants.LOCATION_CHOOSER_TAG)
//    }

    override fun onAddressChoose(address: Address) {
        viewModel.setChosenAddress(address)
        if (getFragmentByTag(Constants.CHECKOUT_TAG) as CheckoutFragment? != null) {
            (getFragmentByTag(Constants.CHECKOUT_TAG) as CheckoutFragment).onAddressChooserSelected()
        }
    }

    override fun onEditAddress(address: Address) {
//        loadFragment(AddOrEditAddressFragment.newInstance(address), Constants.ADD_NEW_ADDRESS_TAG) // nynynynyn
//        if (getFragmentByTag(Constants.CHECKOUT_TAG) as CheckoutFragment? != null) {
//            (getFragmentByTag(Constants.CHECKOUT_TAG) as CheckoutFragment).onAddressChooserSelected()
//        }
    }

    override fun onAddAddress() {
        loadAddNewAddress()
    }

    private fun loadAddNewAddress() {
//        loadFragment(AddOrEditAddressFragment.newInstance(), Constants.ADD_NEW_ADDRESS_TAG) // nynynynyn
    }

    override fun onAddressDeleted() {

    }

    fun finishWithCookId(curCookId: Long) {
        val intent: Intent = getIntent()
        intent.putExtra("curCookId", curCookId)
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }




}
