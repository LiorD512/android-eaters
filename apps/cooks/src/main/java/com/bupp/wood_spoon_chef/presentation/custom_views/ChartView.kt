package com.bupp.wood_spoon_chef.presentation.custom_views

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.ChartViewBinding
import com.bupp.wood_spoon_chef.data.remote.model.EarningAggregator
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.util.*


class ChartView: FrameLayout{

    var binding : ChartViewBinding

    val MAX_PROFIT = 1000f
    fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    private val count = 12

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.chart_view, this, true)

        binding = ChartViewBinding.bind(rootView)


//        initChartView()
    }

    fun initChartView(stats: List<EarningAggregator>) {
        with(binding) {

            chart.setBackgroundColor(Color.WHITE);
            chart.setDrawGridBackground(false);
            chart.setDrawBarShadow(false);
            chart.isHighlightFullBarEnabled = false;
            chart.setDrawValueAboveBar(false)
            chart.description.isEnabled = false;
            chart.axisLeft.setDrawGridLines(false);
            chart.xAxis.setDrawGridLines(false);
            chart.axisRight.setDrawGridLines(false);
            chart.setScaleEnabled(false);
            chart.drawOrder = arrayOf(CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE)
            chart.setVisibleXRangeMaximum(12f)
            chart.setVisibleXRangeMinimum(13f)
            val legend = chart.legend
            legend.formSize = 0f
            val xAxis = chart.xAxis
            xAxis.position = XAxisPosition.BOTTOM
            xAxis.axisMinimum = -0.5f
            xAxis.axisMaximum = 11.5f
            xAxis.textColor = ContextCompat.getColor(context, R.color.dark_50)
            val months = resources.getStringArray(R.array.custom_months)
            var curMonthAsInt = Calendar.getInstance().get(Calendar.MONTH)
            Log.d("wowChart", "months $months, curMonthAsInt $curMonthAsInt")

//        var startingMonth = stats[0].month-2 //-1 because months arr starting with index 0
            xAxis.valueFormatter = object : IAxisValueFormatter {
                override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                    //                curMonthAsInt += values.toInt()
                    //                if(curMonthAsInt >=13){
                    //                    curMonthAsInt = 1
                    //                }
                    Log.d("wowChart", "curMonthAsInt $curMonthAsInt")
                    return months[value.toInt()]
                    //                Log.d("wowChart","starting mointh $startingMonth")
                    //                    Log.d("wowChart","xAxis values: " + values.toInt())
                    //                try{
                    ////                        Log.d("wowChart","xAxis val: " + months[stats[values.toInt()].month-1])
                    //                    startingMonth++
                    //                    return months[startingMonth]
                    //                }catch (e: Exception){
                    //                    Log.d("wowChartView","exception: ${e.message}")
                    //                    return ""
                    //                }
                }
            }
            val rightAxis = chart.axisRight
            rightAxis.isEnabled = false
            val leftAxis = chart.axisLeft
            leftAxis.setDrawGridLines(false)
            leftAxis.axisMinimum = 1f
            leftAxis.axisMaximum = 1000f //todo - set maximum profit
            leftAxis.textColor = ContextCompat.getColor(context, R.color.dark_50)
            leftAxis.valueFormatter = object : IAxisValueFormatter {
                override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                    Log.d("wowChart", "leftAxis val: $$value")
                    return "$$value"
                }
            }

            val data = CombinedData()
            data.setData(generateBarData(MAX_PROFIT))
            data.setData(generateLineData(stats))
            chart.data = data
            chart.invalidate()
        }
    }

    private fun generateBarData(maxProfit: Float): BarData {

        val entries1: ArrayList<BarEntry> = arrayListOf()

        entries1.add(BarEntry(0f, maxProfit))
        entries1.add(BarEntry(1f, maxProfit))
        entries1.add(BarEntry(2f, maxProfit))
        entries1.add(BarEntry(3f, maxProfit))
        entries1.add(BarEntry(4f, maxProfit))
        entries1.add(BarEntry(5f, maxProfit))

        entries1.add(BarEntry(6f, maxProfit))
        entries1.add(BarEntry(7f, maxProfit))
        entries1.add(BarEntry(8f, maxProfit))
        entries1.add(BarEntry(9f, maxProfit))
        entries1.add(BarEntry(10f, maxProfit))
        entries1.add(BarEntry(11f, maxProfit))

        val set1 = BarDataSet(entries1, "")
        set1.color = ContextCompat.getColor(context, R.color.silver_50)
        set1.valueTextColor = ContextCompat.getColor(context, R.color.silver_50)
        set1.valueTextSize = 10f
        set1.setDrawValues(false)
        set1.formLineWidth = 114.toPx().toFloat()

        val d = BarData(set1)
        return d
    }

    private fun generateLineData(stats: List<EarningAggregator>): LineData {

        val d = LineData()

        val entries: MutableList<Entry> = mutableListOf()

        for (index in 0 until stats.size) {
            //index - (0.5 first bar gap + 0.5 left margin) = bar middle point
//            entries.add(Entry(index - 0                                                .5f + 0.5f, getRandomFlaot(1000, 0)))
            val curMonthEarning = stats[index].totalEarnings.value
            if (curMonthEarning != null)
                entries.add(Entry(index - 0.5f + 0.5f, curMonthEarning.toFloat()))
        }


        val set = LineDataSet(entries, "")
        set.color = ContextCompat.getColor(context, R.color.orangeish)
        set.lineWidth = 1.5f
        set.mode = LineDataSet.Mode.LINEAR
        if (stats.size == 1) {
            set.setCircleColor(ContextCompat.getColor(context, R.color.orangeish))
            set.setDrawCircles(true)
        } else {
            set.setDrawCircles(false)
        }
        set.setDrawValues(false)
        set.setDrawHighlightIndicators(false)

        set.axisDependency = YAxis.AxisDependency.LEFT
        d.addDataSet(set)

        return d
    }
//    private fun getRandomFlaot(max: Int, min: Int): Float {
//        val random = min + Math.random() * (max - min)
//        return random.toFloat()
//    }

}