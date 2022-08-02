package com.bupp.wood_spoon_chef.presentation.custom_viewsimport android.content.Contextimport android.util.AttributeSetimport android.view.LayoutInflaterimport android.widget.LinearLayoutimport androidx.core.content.ContextCompatimport com.bupp.wood_spoon_chef.Rimport com.bupp.wood_spoon_chef.databinding.WowCalendarViewBindingimport com.bupp.wood_spoon_chef.data.remote.model.CookingSlotSlimimport com.bupp.wood_spoon_chef.utils.DateUtilsimport com.github.sundeepk.compactcalendarview.CompactCalendarViewimport java.util.*import com.github.sundeepk.compactcalendarview.domain.Eventimport org.joda.time.DateTimeimport java.util.concurrent.ConcurrentHashMapclass WowCalendarView @JvmOverloads constructor(    context: Context,    attrs: AttributeSet? = null,    defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {    interface CalendarViewListener {        fun onDateSelected(dateClicked: Date?)        fun onMonthScroll(firstDayOfMonth: Date?)    }    private var binding: WowCalendarViewBinding = WowCalendarViewBinding.inflate(        LayoutInflater.from(context), this, true    )    private var listener: CalendarViewListener? = null    private val eventsByMonth = ConcurrentHashMap<String, List<Event>>()    init {        inflate(context, R.layout.wow_calendar_view, this)        initUi()        initAttrs(attrs)    }    fun setCalendarViewListener(listener: CalendarViewListener) {        this.listener = listener    }    private fun initUi() {        with(binding) {            setSelectedDate()            calendarViewLeftArrow.setOnClickListener { calendarView.showPreviousMonth() }            calendarViewRightArrow.setOnClickListener { calendarView.showNextMonth() }            calendarView.apply {                setFirstDayOfWeek(Calendar.SUNDAY)                setUseThreeLetterAbbreviation(true)                shouldDrawIndicatorsBelowSelectedDays(false)            }            calendarView.setListener(object : CompactCalendarView.CompactCalendarViewListener {                override fun onMonthScroll(firstDayOfNewMonth: Date?) {                    setTitleText(firstDayOfNewMonth)                    listener?.onMonthScroll(firstDayOfNewMonth)                }                override fun onDayClick(dateClicked: Date?) {                    listener?.onDateSelected(dateClicked)                }            })        }    }    fun setSelectedDate(date: Date = Date()) {        binding.calendarView.setCurrentDate(date)        binding.calendarViewTitleDate.text = DateUtils.parseDateToMonthYear(date)    }    fun addEvents(cookingSlots: List<CookingSlotSlim>, monthOfYearAsAShortText: String) {        eventsByMonth[monthOfYearAsAShortText] = cookingSlots.map {            val color = ContextCompat.getColor(context, R.color.pinkish)            Event(color, it.startsAt.time, it.id)        }        binding.apply {            calendarView.removeAllEvents()            addSlotsToCalendar(eventsByMonth.flatMap { it.value })        }    }    fun setTitleText(date: Date?) {        binding.calendarViewTitleDate.text = DateUtils.parseDateToMonthYear(date ?: Date())    }    fun hasEvent(dateClicked: Date?): Boolean {        return binding.calendarView.getEvents(dateClicked) != null && binding.calendarView.getEvents(            dateClicked        ).size > 0    }    private fun WowCalendarViewBinding.addSlotsToCalendar(        cookingSlots: Collection<Event>    ) {        cookingSlots.forEach {            calendarView.addEvent(it, true)        }    }    private fun setMainLayoutBkg(color: Int) {        binding.calendarViewMainLayout.setBackgroundColor(color)    }    private fun initAttrs(attrs: AttributeSet?) {        if (attrs != null) {            val attrSet =                context.obtainStyledAttributes(attrs, R.styleable.WowCalendarView)            if (attrSet.hasValue(R.styleable.WowCalendarView_mainBkgColor)) {                val color = attrSet.getColor(                    R.styleable.WowCalendarView_mainBkgColor,                    0                )                setMainLayoutBkg(color)            }            attrSet.recycle()        }    }}