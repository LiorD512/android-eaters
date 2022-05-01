package com.bupp.wood_spoon_chef.presentation.custom_views

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.bupp.wood_spoon_chef.R
import android.widget.ProgressBar
import android.view.animation.Animation
import android.view.animation.Transformation
import com.bupp.wood_spoon_chef.utils.Utils
import com.bupp.wood_spoon_chef.databinding.CounterProgressViewBinding


class CounterProgressView : LinearLayout{

    var binding : CounterProgressViewBinding
    var timer: CountDownTimer? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.counter_progress_view, this, true)
        binding = CounterProgressViewBinding.bind(rootView)
    }

    fun init(time: Long){
        with(binding) {
            timer = object : CountDownTimer(time, 1000) {
                override fun onFinish() {
                    timer?.cancel()
                    counterProgressViewTime.text = "00:00:00"
                }

                override fun onTick(millisUntilFinished: Long) {
                    val min = Utils.parseCountDown(millisUntilFinished)
                    counterProgressViewTime.text = "$min min"
                }
            }
            timer?.start()

            val anim = ProgressBarAnimation(counterProgressViewProgress, 0f, time.toFloat()) //time - in milliseconds
            Log.d("wowCounterProgressView", "from: " + 0 + ", to: " + time.toFloat())
            anim.duration = time
            counterProgressViewProgress.startAnimation(anim)
        }
    }

    inner class ProgressBarAnimation(private val progressBar: ProgressBar, private val from: Float, private val to: Float) : Animation() {

        override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
            super.initialize(width, height, parentWidth, parentHeight)
            progressBar.max = to.toInt()
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            super.applyTransformation(interpolatedTime, t)
            val value4 = (interpolatedTime * from)/(from - to)
            val value =  from
//            Log.d("wowCounterProgressView","from: " + from + ", to: " + to)
            Log.d("wowCounterProgressView","from*interpolatedTime: " + to*interpolatedTime)
            Log.d("wowCounterProgressView", "values time: $value4")
            progressBar.progress = (to*interpolatedTime).toInt()
        }
    }

    fun cancelTimer(){
        timer?.cancel()
        timer = null
    }

}