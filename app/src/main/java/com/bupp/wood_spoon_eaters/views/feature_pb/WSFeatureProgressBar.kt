package com.bupp.wood_spoon_eaters.views.feature_pb

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.bupp.wood_spoon_eaters.databinding.WsFeaturePbBinding


class WSFeatureProgressBar @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private val showingPbs: MutableList<WSSingleFeatureProgressBar> = mutableListOf()
    private var stepsCount: Int = 0
    private var currentStep: Int = 0
    private var binding: WsFeaturePbBinding = WsFeaturePbBinding.inflate(LayoutInflater.from(context), this, true)

//    init{
//        initUi(attrs)
//    }

    var firstTime = true
    private fun showFirst() {
        if(firstTime){
            firstTime = false
            showingPbs[0].setUpObserver()
        }
    }

//    private fun initUi(attrs: AttributeSet?) {
//        attrs?.let{
//
//            with(binding){
//                val attr = context.obtainStyledAttributes(attrs, R.styleable.WSFeatureProgressBar)
//
//                stepsCount = attr.getInt(R.styleable.WSFeatureProgressBar_stepsCount, 0)
////                initSteps()
//
//                attr.recycle()
//            }
//        }
//    }

    private fun initSteps() {
        Log.d(TAG, "initSteps")
            if(stepsCount > 0){
            showingPbs.clear()
            binding.featurePbLayout.weightSum = stepsCount.toFloat()
            for(i in 0 until stepsCount){
                val pb = WSSingleFeatureProgressBar(context)

                val p = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                p.weight = 1f
                pb.layoutParams = p

                binding.featurePbLayout.addView(pb)
                showingPbs.add(pb)

            }
        }
    }

    fun toggleSteps(nextStep: Int) {
        Log.d(TAG, "toggleSteps: nextStep: $nextStep currentStep: $currentStep")
        currentStep = nextStep
        if(nextStep < currentStep){
            previousStep()
//            hideNext()
        }else{
            nextStep()
//            hidePrevious()
        }
    }

    private fun hidePrevious() {
        showingPbs[currentStep].startAnimation()
    }

    private fun nextStep() {
        Log.d(TAG, "nextStep ($currentStep)")
        showingPbs[currentStep].startAnimation()
    }

    private fun previousStep() {
        Log.d(TAG, "previousStep ($currentStep)")
        showingPbs[currentStep].startAnimationOff()
    }

    fun setStepsCount(size: Int) {
        stepsCount = size
        initSteps()
        showFirst()
    }

    companion object{
        const val TAG = "wowWSFeatureProgressBar"
    }

}
