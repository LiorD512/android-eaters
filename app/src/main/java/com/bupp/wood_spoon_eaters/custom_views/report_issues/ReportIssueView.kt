package com.bupp.wood_spoon_eaters.custom_views.report_issues

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import com.bupp.wood_spoon_eaters.model.ReportRequest
import com.bupp.wood_spoon_eaters.model.ReportTopic
import kotlinx.android.synthetic.main.report_issue_view.view.*


class ReportIssueView : FrameLayout, CompoundButton.OnCheckedChangeListener, InputTitleView.InputTitleViewListener {


    private lateinit var reportTopic: ReportTopic
    var listener: ReportIssueViewListener? = null
    interface ReportIssueViewListener{
        fun onReportIssueChange(reportRequest: ReportRequest)
        fun onReportIssueTextChange(reportRequest: ReportRequest)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.report_issue_view, this, true)
    }

    fun initReportIssue(reportTopic: ReportTopic, listener: ReportIssueViewListener){
//        this.reportTopic = reportTopic
        topicId = reportTopic.id
        this.listener = listener
        reportIssueTitle.text = reportTopic.name
        fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

        for(item in reportTopic.reportIssues){
            val radioButton = RadioButton(context)
            val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.setMargins(0, 16.toPx(), 0, 16.toPx())
            radioButton.layoutParams = params
            radioButton.gravity = Gravity.CENTER or Gravity.LEFT
            radioButton.setButtonDrawable(R.drawable.circle_checkbox_selector)
            radioButton.setPadding(8.toPx(),0,0,0)
            radioButton.setText(item.name)
            radioButton.id = item.id.toInt()
            radioButton.setOnCheckedChangeListener(this)

            reportIssueRadioGroup.addView(radioButton)

            val sep = View(context)
            val sepParams: ViewGroup.LayoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1.toPx())
            sep.setPadding(0, 16.toPx(),0, 12.toPx())
            sep.layoutParams = sepParams
            sep.setBackgroundColor(ContextCompat.getColor(context, R.color.silver))

            reportIssueRadioGroup.addView(sep)
        }

        reportIssueNotes.setInputTitleViewListener(this)
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
        }
    }

    private var firstName: String? = null
    private var lastName: String? = null
    var name: String
        get() = firstName + " " + lastName
        set(value) {
            val nameArray = value.split(" ".toRegex())
            firstName = nameArray[0]
            lastName = nameArray[1]
        }


    private var topicId: Long? = null
        get() = field
        set(value) {
            field = value
        }

    private var issueId: Long? = null
        get() = field
        set(value) {
            field = value
        }

    private var body: String? = null
        get() = field
        set(value) {
            field = value
        }

    val reportRequest: ReportRequest
        get() = ReportRequest(topicId, issueId, body)
}