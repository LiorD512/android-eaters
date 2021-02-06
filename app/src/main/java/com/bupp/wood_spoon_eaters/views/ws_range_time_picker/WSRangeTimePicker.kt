package com.bupp.wood_spoon_eaters.views.ws_range_time_picker

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.WsRangeTimePickerBinding
import java.util.*


class WSRangeTimePicker @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    var wsRangeTimePickerDateAdapter: WSRangeTimePickerListAdapter? = null
    var wsRangeTimePickerHoursAdapter: WSRangeTimePickerListAdapter? = null
    private var binding: WsRangeTimePickerBinding = WsRangeTimePickerBinding.inflate(LayoutInflater.from(context), this, true)

    init {
         initUi(attrs)
    }

    private fun initUi(attrs: AttributeSet?) {
        attrs?.let{

            val attr = context.obtainStyledAttributes(attrs, R.styleable.WSEditText)

//            val hint = attr.getString(R.styleable.WSEditText_hint)
//            binding.wsEditTextInput.setHint(hint)
//
//            val error = attr.getString(R.styleable.WSEditText_error)
//            error?.let { setError(error) }
//
//            inputType = attr.getInt(R.styleable.WSEditText_inputType, 0)
//            setInputType(inputType)
//
//            val isEditable = attr.getBoolean(R.styleable.WSEditText_isEditable, true)
//            binding.wsEditTextInput.isFocusable = isEditable
//            binding.wsEditTextInput.isClickable = isEditable
//            this.isEditable = isEditable


            attr.recycle()

        }

        binding.wsRangeTimePickerMainLayout.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                wsRangeTimePickerDateAdapter = WSRangeTimePickerListAdapter()
                binding.wsRangeTimePickerDateList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.wsRangeTimePickerDateList.adapter = wsRangeTimePickerDateAdapter

                val layoutHeihgt = binding.wsRangeTimePickerMainLayout.measuredHeight
                val uiHeight = binding.wsRangeTimePickerUiLayout.measuredHeight / 4
                Log.d(TAG, "layoutHeihgt: $layoutHeihgt")
                Log.d(TAG, "uiHeight: $uiHeight")
//                binding.wsRangeTimePickerDateList.addItemDecoration(WSRangeTimePickerItemDecorator(layoutHeihgt-uiHeight))

                val snapHelper: SnapHelper = LinearSnapHelper()
                snapHelper.attachToRecyclerView(binding.wsRangeTimePickerDateList)
//                val layoutManager = binding.wsRangeTimePickerDateList.layoutManager
//                val snapView = snapHelper.findSnapView(layoutManager)

                val dates = getDaysFromNow(7)
                wsRangeTimePickerDateAdapter?.submitList(dates)

                binding.wsRangeTimePickerMainLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })


        binding.wsRangeTimePickerMainLayout.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
//                wsRangeTimePickerHoursAdapter = WSRangeTimePickerListAdapter()
//                binding.wsRangeTimePickerHoursList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//                binding.wsRangeTimePickerHoursList.adapter = wsRangeTimePickerHoursAdapter
//
//                val layoutHeihgt = binding.wsRangeTimePickerMainLayout.measuredHeight
//                val uiHeight = binding.wsRangeTimePickerUiLayout.measuredHeight / 4
//                Log.d(TAG, "layoutHeihgt: $layoutHeihgt")
//                Log.d(TAG, "uiHeight: $uiHeight")
//                binding.wsRangeTimePickerHoursList.addItemDecoration(WSRangeTimePickerItemDecorator(layoutHeihgt-uiHeight))
//
//                val snapHelper: SnapHelper = LinearSnapHelper()
//                snapHelper.attachToRecyclerView(binding.wsRangeTimePickerHoursList)
//
//                val hours = getHoursForDates(wsRangeTimePickerDateAdapter?.currentList)
//                wsRangeTimePickerDateAdapter?.submitList(dates)
//
//                binding.wsRangeTimePickerMainLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun getDaysFromNow(daysFromNow: Int): List<Date> {
        val dates = mutableListOf<Date>()
        for(i in -1..daysFromNow){
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, i)
            dates.add(calendar.time)
        }
        return dates
    }

//    private fun getHoursForDates(dates: MutableList<Date>?): List<Date> {
//        val hours = mutableListOf<Date>()
//        dates?.let{
//            for(date in dates){
//                val calendar = Calendar.getInstance()
//                calendar.add(Calendar.DAY_OF_YEAR, i)
//                dates.add(calendar.time)
//            }
//        }
//        return hours
//    }

    companion object{
        const val TAG = "wowWSRangeTimePicker"
    }


}
