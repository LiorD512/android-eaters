package com.bupp.wood_spoon_chef.presentation.features.new_dish.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.presentation.custom_views.SimpleTextWatcher
import com.bupp.wood_spoon_chef.databinding.FragmentNewDishPriceBinding
import com.bupp.wood_spoon_chef.presentation.dialogs.TooltipDialog
import com.bupp.wood_spoon_chef.presentation.features.base.BaseFragment
import com.bupp.wood_spoon_chef.presentation.features.new_dish.NewDishViewModel
import com.bupp.wood_spoon_chef.data.remote.model.DishRequest
import com.bupp.wood_spoon_chef.utils.AnimationUtil
import com.bupp.wood_spoon_chef.utils.Utils
import com.bupp.wood_spoon_chef.utils.getLocal
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.DecimalFormat
import java.util.*


class NewDishPriceFragment : BaseFragment(R.layout.fragment_new_dish_price){


    var binding: FragmentNewDishPriceBinding? = null
    val viewModel by sharedViewModel<NewDishViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentNewDishPriceBinding.bind(view)

        initUi()
        initObservers()
    }



    private fun initUi() {
        with(binding!!) {
            chefFeeText.setOnClickListener {
                TooltipDialog.newInstance(getString(R.string.tooltip_chef_service_fee)).show(childFragmentManager, Constants.TOOL_TIP_DIALOG)
            }

            //RESTORE THIS TO AUTO LOCALE
//            val myLocale = Utils.getCurrentLocale(requireContext())
//            Log.d("wowLocale","$myLocale")
//            val myLocaleCurrency = Currency.getInstance("en_US") //myLocale - add to set device locale
//            val symbol = myLocaleCurrency.getSymbol(myLocale)

            newDishPriceInput.locale = getLocal()
            newDishPriceInput.configureViewForLocale(getLocal())
            newDishPriceInput.addTextChangedListener(object : SimpleTextWatcher() {

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    super.onTextChanged(s, start, before, count)
                    newDishPriceInput.removeTextChangedListener(this)
                    val numberString = newDishPriceInput.text.toString()

                    updatePriceParam(numberString)
                    binding!!.newDishPriceInput.addTextChangedListener(this)

                }
            })

            newDishPriceNext.setOnClickListener {
                if (allFieldsValid()) {
                    saveCurrentStep()
                }
            }
            newDishPriceBack.setOnClickListener {
                activity?.onBackPressed()
            }

        }
    }

    private fun updatePriceParam(numberString: String) {
        Log.d(TAG, "updatePriceParam: $numberString start")
        val clearPrice = Utils.parsePriceStringToNumber(numberString)

        val defaultFeePercentage = viewModel.getDefaultServiceFee()

        val feePrice = clearPrice * (defaultFeePercentage / 100)
        val totalProfit = (clearPrice - feePrice)

        val df = DecimalFormat("#.##")

        with(binding!!) {
            newDishPriceDishPrice.text = numberString
            newDishPriceDishChefFee.text = "$${df.format(feePrice)}"
            newDishPriceDishTotal.text = "$${df.format(totalProfit)}"
        }
        Log.d(TAG, "updatePriceParam: $numberString end")

    }

    private fun allFieldsValid(): Boolean {
        with(binding!!) {
            val priceValid = !newDishPriceInput.text.isNullOrEmpty() && newDishPriceInput.text.toString() != "$0.00"
            if (!priceValid) {
                AnimationUtil().shakeView(newDishPriceInput, requireContext())
            }
            return priceValid
        }
    }

    private fun initObservers() {
        viewModel.curDishLiveData.observe(viewLifecycleOwner, {
            loadUnSavedData(it)
        })
        viewModel.saveDraftEvent.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let{
                saveCurrentStep(true)
            }
        })
    }

    private fun saveCurrentStep(isDraft: Boolean = false) {
        with(binding!!){
//            val price = newDishPriceInput.cleanDoubleValue * 100
            val price = newDishPriceInput.rawValue.toString()
            viewModel.saveDishPrice(price.toInt(), isDraft)
            Log.d(TAG, "wowPriceSent: $price")
        }
    }

    private fun loadUnSavedData(dishRequest: DishRequest?) {
        with(binding!!) {
            dishRequest?.let { it ->
                it.price?.let {
                    newDishPriceInput.setText(it.toString()) // fix this when server is ready for this
                }
            }
        }
    }


    override fun clearClassVariables() {
        binding = null
    }

    companion object{
        const val TAG = "wowNewDishPrice"
    }


}