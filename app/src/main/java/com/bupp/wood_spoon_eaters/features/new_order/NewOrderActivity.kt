package com.bupp.wood_spoon_eaters.features.new_order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.bupp.wood_spoon.dialogs.AddressChooserDialog
import com.bupp.wood_spoon.dialogs.EditAddressDialog
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.main.delivery_details.sub_screens.add_new_address.AddAddressFragment
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.checkout.CheckoutFragment
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.SingleDishFragment
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.utils.Constants
import com.stripe.android.model.PaymentMethod
import com.stripe.android.view.PaymentMethodsActivity
import com.stripe.android.view.PaymentMethodsActivityStarter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.ArrayList


class NewOrderActivity : AppCompatActivity(), SingleDishFragment.SingleDishDialogListener,
    CheckoutFragment.CheckoutDialogListener, AddressChooserDialog.AddressChooserDialogListener,
    EditAddressDialog.EditAddressDialogListener {


    private val currentDesplayingFragment: ArrayList<String> = arrayListOf()
    val viewModel by viewModel<NewOrderViewModel>()

    private val BACK_STACK_ROOT_TAG = "first_frag_tag"
    private var subscreensOnTheStack: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_order)

        val menuItemId = intent.getLongExtra("menuItemId", -1)
        if(menuItemId < 0){
            finish()
        }else{
            loadSingleDish(menuItemId)
        }

        viewModel.initNewOrder()
        viewModel.initStripe(this)

        viewModel.ephemeralKeyProvider.observe(this, Observer { event ->
            if(!event.isSuccess){
                Toast.makeText(this, "Error while loading payments method", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun startPaymentMethodActivity() {
        PaymentMethodsActivityStarter(this).startForResult()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PaymentMethodsActivityStarter.REQUEST_CODE -> {
                    val paymentMethod: PaymentMethod = (data?.getParcelableExtra(PaymentMethodsActivity.EXTRA_SELECTED_PAYMENT) as PaymentMethod)

                    if (paymentMethod != null && paymentMethod.card != null) {
                        Log.d("wowNewOrder","payment method success")
                        if(getFragmentByTag(Constants.CHECKOUT_TAG) != null){
                            (getFragmentByTag(Constants.CHECKOUT_TAG) as CheckoutFragment).updateCustomerPaymentMethod(paymentMethod)
                        }
                    }
                }
            }
        }
    }



    //Single Dish

    fun loadSingleDish(menuItemId: Long){
        loadFragment(SingleDishFragment(menuItemId, this), menuItemId.toString())
    }

    override fun onDishClick(itemId: Long) {
        loadFragment(SingleDishFragment(itemId, this), itemId.toString())
    }

    //Checkout
    override fun onCheckout() {
        loadFragment(CheckoutFragment(this), Constants.CHECKOUT_TAG)
    }

    override fun onCheckoutDone() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onCheckoutCanceled() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }


    private fun loadFragment(fragment: Fragment, tag: String) {
        val fragManger = supportFragmentManager
        fragManger.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        currentDesplayingFragment.add(tag)
        fragManger.beginTransaction()
            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
            .replace(R.id.newOrderContainer, fragment, tag)
            .addToBackStack(tag)
            .commit()
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

        val count = currentDesplayingFragment.size
        Log.d("wowNewOrder", "onBackPress num of frag: $count")
        if(count > 1){
            super.onBackPressed()
            currentDesplayingFragment.removeAt(count-1)
            Log.d("wowNewOrder", "onBackPress num of frag: $count")
        }else{
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

    fun loadPromoCode() {
        Log.d("wow","load Promo code")
    }

    fun loadAddressesDialog() {
        AddressChooserDialog(this, viewModel.getListOfAddresses(), viewModel.getChosenAddress()).show(
            supportFragmentManager,
            Constants.ADDRESS_DIALOG_TAG
        )
    }

    override fun onAddressMenuClick(address: Address) {
        EditAddressDialog(address, this).show(supportFragmentManager, Constants.EDIT_ADDRESS_DIALOG)
    }

    override fun onAddressChoose(address: Address) {
        viewModel.setChosenAddress(address)
    }

    override fun onEditAddress(address: Address) {
        loadFragment(AddAddressFragment(address), Constants.ADD_NEW_ADDRESS_TAG)
        if (getFragmentByTag(Constants.CHECKOUT_TAG) as CheckoutFragment? != null) {
            (getFragmentByTag(Constants.CHECKOUT_TAG) as CheckoutFragment).onAddressChooserSelected()
        }
//        .setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE_SAVE, "Select Your Delivery Address")
    }

    override fun onAddAddress() {
        loadAddNewAddress()
    }

    fun loadAddNewAddress() {
        loadFragment(AddAddressFragment(null), Constants.ADD_NEW_ADDRESS_TAG)
//        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE_SAVE, "Select Your Delivery Address")
    }


}
