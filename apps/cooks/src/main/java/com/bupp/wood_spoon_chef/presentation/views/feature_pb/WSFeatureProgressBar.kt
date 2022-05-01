package com.bupp.wood_spoon_chef.presentation.views.feature_pb

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.WsFeaturePbBinding


class WSFeatureProgressBar @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private val showingPbs: MutableList<WSSingleFeatureProgressBar> = mutableListOf()
    private var stepsCount: Int = 0
    private var currentStep: Int = 0
    private var binding: WsFeaturePbBinding = WsFeaturePbBinding.inflate(LayoutInflater.from(context), this, true)

    init{
        initUi(attrs)
    }

    private fun initUi(attrs: AttributeSet?) {
        attrs?.let{

            val attr = context.obtainStyledAttributes(attrs, R.styleable.WSFeatureProgressBar)

            stepsCount = attr.getInt(R.styleable.WSFeatureProgressBar_stepsCount, 0)
            initSteps()

            attr.recycle()
        }
    }

    private fun initSteps() {
        if(stepsCount > 0){
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

    fun nextStep() {
        if(currentStep < stepsCount){
            showingPbs[currentStep].setUpObserver()
            currentStep++
        }
    }

    fun previousStep() {
        if(currentStep > 0){
            currentStep--
            showingPbs[currentStep].startAnimationOff()
        }
    }


}
