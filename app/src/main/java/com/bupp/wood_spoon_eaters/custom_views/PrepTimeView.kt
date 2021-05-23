//package com.bupp.wood_spoon_eaters.custom_views
//
//import android.content.Context
//import android.util.AttributeSet
//import android.view.LayoutInflater
//import android.widget.LinearLayout
//import com.bupp.wood_spoon_eaters.R
//import com.bupp.wood_spoon_eaters.model.PrepTimeRange
//
//
//class PrepTimeView : LinearLayout, SelectableBtn.SelectableBtnListener {
//
//    private lateinit var ranges: ArrayList<PrepTimeRange>
//    private lateinit var listener: PrepTimeViewListener
//
//    interface PrepTimeViewListener {
//        fun onPrepTimeSelected(prepTime: PrepTimeRange)
//    }
//
//    constructor(context: Context) : this(context, null)
//    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
//    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
//        LayoutInflater.from(context).inflate(R.layout.prep_time_view, this, true)
//
//        initUi()
//    }
//
//    private fun initUi() {
//        prepTimeView0.setSelectableBtnListener(this)
//        prepTimeView1.setSelectableBtnListener(this)
//        prepTimeView2.setSelectableBtnListener(this)
//        prepTimeView3.setSelectableBtnListener(this)
//    }
//
//    override fun onSelectableBtnClicked(btn: SelectableBtn) {
//        btn.setBtnEnabled(true)
//        when (btn.id) {
//            R.id.prepTimeView0 -> {
//                prepTimeView1.setBtnEnabled(false)
//                prepTimeView2.setBtnEnabled(false)
//                prepTimeView3.setBtnEnabled(false)
//                listener.onPrepTimeSelected(ranges[0])
//            }
//            R.id.prepTimeView1 -> {
//                prepTimeView0.setBtnEnabled(false)
//                prepTimeView2.setBtnEnabled(false)
//                prepTimeView3.setBtnEnabled(false)
//                listener.onPrepTimeSelected(ranges[1])
//            }
//            R.id.prepTimeView2 -> {
//                prepTimeView0.setBtnEnabled(false)
//                prepTimeView1.setBtnEnabled(false)
//                prepTimeView3.setBtnEnabled(false)
//                listener.onPrepTimeSelected(ranges[2])
//            }
//            R.id.prepTimeView3 -> {
//                prepTimeView0.setBtnEnabled(false)
//                prepTimeView1.setBtnEnabled(false)
//                prepTimeView2.setBtnEnabled(false)
//                listener.onPrepTimeSelected(ranges[3])
//            }
//        }
//
//    }
//
//    fun initPrepTime(ranges: ArrayList<PrepTimeRange>, listener: PrepTimeViewListener) {
//        this.listener = listener
//        this.ranges = ranges
//        if (ranges.size == 4) {
//            prepTimeView0.initBtn(ranges[0].icon, getRangeStr(ranges[0]))
//            prepTimeView1.initBtn(ranges[1].icon, getRangeStr(ranges[1]))
//            prepTimeView2.initBtn(ranges[2].icon, getRangeStr(ranges[2]))
//            prepTimeView3.initBtn(ranges[3].icon, getRangeStr(ranges[3]))
//        }
//    }
//
//    private fun getRangeStr(prepTimeRange: PrepTimeRange): String {
//        return "${prepTimeRange.minTime} - ${prepTimeRange.maxTime}"
//    }
//
//}