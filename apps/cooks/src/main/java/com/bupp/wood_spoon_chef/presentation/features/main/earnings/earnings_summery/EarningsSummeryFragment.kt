package com.bupp.wood_spoon_chef.presentation.features.main.earnings.earnings_summery

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.FragmentEarningsBinding
import com.bupp.wood_spoon_chef.databinding.FragmentEarningsSummeryBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseDialogFragment
import com.bupp.wood_spoon_chef.presentation.features.main.earnings.EarningsFragment
import com.bupp.wood_spoon_chef.presentation.features.main.earnings.EarningsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class EarningsSummeryFragment: BaseDialogFragment(R.layout.fragment_earnings_summery) {

    private var binding: FragmentEarningsSummeryBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEarningsSummeryBinding.bind(view)
        initUi()
    }

    private fun initUi() {
        val today = Calendar.getInstance()
        val date2 = Calendar.getInstance()
        date2.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH) + 1)
        val date3 = Calendar.getInstance()
        date3.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH) + 2)
        val date4 = Calendar.getInstance()
        date4.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH) + 3)
        val date5 = Calendar.getInstance()
        date5.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH) + 4)
        val date6 = Calendar.getInstance()
        date6.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH) + 5)
        val date7 = Calendar.getInstance()
        date7.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH) + 6)
        val dateList = listOf<Date>(today.time,date2.time,date3.time,date4.time,date5.time,date6.time,date7.time)
        binding?.apply{
            summeryFragDateBar.initDates(dateList)
        }
    }

    override fun clearClassVariables() {
        binding = null
    }

}