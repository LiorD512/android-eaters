package com.bupp.wood_spoon_chef.presentation.features.main.earnings


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.FragmentEarningsBinding
import com.bupp.wood_spoon_chef.data.remote.model.Earnings
import org.koin.androidx.viewmodel.ext.android.viewModel

class EarningsFragment : Fragment(R.layout.fragment_earnings) {

    private lateinit var binding: FragmentEarningsBinding
    val viewModel by viewModel<EarningsViewModel>()

    companion object{
        fun newInstance() = EarningsFragment()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentEarningsBinding.bind(view)

        viewModel.getEarningsStats()
        viewModel.getEarnings.observe(viewLifecycleOwner, Observer { earningsEvent ->
            if(earningsEvent.isSuccess){
                initStats(earningsEvent.stats!!)
            }
        })
    }

    private fun initStats(stats: Earnings) {
        with(binding){
            singleOrderFragProfit.text = stats.dailyEarnings?.formattedValue
            earningsFragTotal.text = stats.totalEarnings?.formattedValue
            earningsFragWeek.text = stats.weeklyEarnings?.formattedValue
            earningsFragAvg.text = stats.avgEarningsPerDish?.formattedValue


            earningsFragRank.text = "${viewModel.getCookAngRating()}"
            earningsFragRankTop.text = "You’re in the top ${stats.ratingPercentile}%"

            earningsFragOrdersCount.text = "${stats.totalOrdersCount}"
            earningsFragOrdersCountWeek.text = "${stats.weeklyOrdersCount} this week"

            if(!stats.yearlyEarnings.isNullOrEmpty())
                earningsFragChartView.initChartView(stats.yearlyEarnings)
        }

    }
}
