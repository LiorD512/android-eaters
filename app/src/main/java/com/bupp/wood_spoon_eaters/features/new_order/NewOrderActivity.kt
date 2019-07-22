package com.bupp.wood_spoon_eaters.features.new_order

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.StatusBottomBar
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.checkout.CheckoutFragment
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.SingleDishFragment
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.activity_new_order.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.ArrayList


class NewOrderActivity : AppCompatActivity(), SingleDishFragment.SingleDishDialogListener,
    CheckoutFragment.CheckoutDialogListener{//}, StatusBottomBar.StatusBottomBarListener {

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

        initStatusBar()
        initObserers()
    }

    private fun initObserers() {

    }

    private fun initStatusBar() {
//        newOrderStatusBar.setStatusBottomBarListener(this)
    }



//    override fun onStatusBarClicked(type: Int?) {
//        when(type){
//            Constants.STATUS_BAR_TYPE_CART -> {
//                (getFragmentByTag(currentDesplayingFragment.last()) as SingleDishFragment).addToCart()
//            }
//            Constants.STATUS_BAR_TYPE_CHECKOUT -> {
//                updateStatusBottomBar(type = Constants.STATUS_BAR_TYPE_CHECKOUT)
//                onCheckout()
//            }
//        }
//    }





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

    private fun loadSubFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.newOrderContainer, fragment, tag)
            .addToBackStack(null)
            .commit()
//        subscreensOnTheStack++;
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


}
