package com.bupp.wood_spoon_eaters.custom_views.report_issues

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import com.bupp.wood_spoon_eaters.databinding.ReportIssueViewBinding
import com.bupp.wood_spoon_eaters.model.ReportRequest
import com.bupp.wood_spoon_eaters.model.ReportTopic
import com.bupp.wood_spoon_eaters.views.WSCounterEditText


class ReportIssueView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr), CompoundButton.OnCheckedChangeListener, InputTitleView.InputTitleViewListener,
    WSCounterEditText.WSCounterListener {

    private var binding: ReportIssueViewBinding = ReportIssueViewBinding.inflate(LayoutInflater.from(context), this, true)

    var listener: ReportIssueViewListener? = null
    interface ReportIssueViewListener{
        fun onReportIssueChange(reportRequest: ReportRequest)
        fun onReportIssueTextChange(reportRequest: ReportRequest)
    }


    fun initReportIssue(reportTopic: ReportTopic, listener: ReportIssueViewListener){
        this.listener = listener
        with(binding){
            topicId = reportTopic.id
            reportIssueTitle.text = reportTopic.name
            fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

            for(item in reportTopic.reportIssues.sortedBy { it.id }){
                val radioButton = RadioButton(context)
                val params = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params.setMargins(0, 16.toPx(), 0, 16.toPx())
                radioButton.layoutParams = params
                radioButton.gravity = Gravity.CENTER or Gravity.LEFT
                radioButton.setButtonDrawable(R.drawable.circle_checkbox_selector)
                radioButton.setPadding(8.toPx(),0,0,0)
                radioButton.setText(item.name)
                radioButton.id = item.id.toInt()
                radioButton.setOnCheckedChangeListener(this@ReportIssueView)

                binding.reportIssueRadioGroup.addView(radioButton)

                val sep = View(context)
                val sepParams: ViewGroup.LayoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1.toPx())
                sep.setPadding(0, 16.toPx(),0, 12.toPx())
                sep.layoutParams = sepParams
                sep.setBackgroundColor(ContextCompat.getColor(context, R.color.silver))

                reportIssueRadioGroup.addView(sep)
            }

            reportIssueNotes.setWSCounterListener(this@ReportIssueView)
        }
    }

    override fun onInputTitleChange(str: String?) {
        if(str != null){
            body = str
            listener?.onReportIssueTextChange(reportRequest)
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if(isChecked){
            issueId = buttonView!!.id.toLong()
            listener?.onReportIssueChange(reportRequest)

            if(buttonView.text == "something else..."){
                binding.reportIssueNotes.visibility = View.VISIBLE
                binding.reportIssueNotes.requestFocus()
            }else{
                binding.reportIssueNotes.visibility = View.GONE
            }
        }
    }

    private var topicId: Long? = null

    private var issueId: Long? = null

    private var body: String? = null

    private val reportRequest: ReportRequest
        get() = ReportRequest(topicId, issueId, body)
}