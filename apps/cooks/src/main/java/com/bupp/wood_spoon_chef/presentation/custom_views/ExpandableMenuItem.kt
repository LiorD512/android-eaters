package com.bupp.wood_spoon_chef.presentation.custom_views

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.ExpandableMenuItemBinding
import com.bupp.wood_spoon_chef.presentation.features.main.calendar.sub_screen.calendar_menu_adapter.MenuAdapterModel
import com.bupp.wood_spoon_chef.utils.Utils

class ExpandableMenuItem @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: ExpandableMenuItemBinding = ExpandableMenuItemBinding.inflate(
            LayoutInflater.from(context), this, true
    )

    interface ExpandableMenuItemListener {
        fun onExpandableMenuItemChange(id: Long, isExpanded: Boolean)
        fun onExpandableMenuQuantityChange(id: Long, quantity: Int = 0)
    }

    var listener: ExpandableMenuItemListener? = null
    var curDish: MenuAdapterModel? = null

    private var currentAnimator: Animator? = null

    private val textWatcher = object : SimpleTextWatcher() {
        override fun afterTextChanged(s: Editable) {
            curDish?.dishId?.let { dishId ->
                val value = s.toString().takeIf { it.isNotEmpty() }?.toIntOrNull() ?: 0
                listener?.onExpandableMenuQuantityChange(dishId, value)
            }
        }
    }

    init {
        initUi()
    }

    private fun initUi() {
        with(binding) {
            menuItemTopLayout.setOnClickListener {
                expand(animated = true)
                curDish?.dishId?.let {
                    listener?.onExpandableMenuItemChange(it, true)
                }
            }
            menuItemClose.setOnClickListener {
                collapse(animated = true)
                curDish?.dishId?.let {
                    listener?.onExpandableMenuItemChange(it, false)
                }
            }

            menuItemQut.addTextChangedListener(textWatcher)

            menuItemUnlimited.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    menuItemQut.setText("$defaultQuantity")
                    menuItemQut.alpha = 0.5f
                    curDish?.dishId?.let {
                        listener?.onExpandableMenuQuantityChange(it, defaultQuantity)
                    }
                } else {
                    menuItemQut.setText("")
                    menuItemQut.alpha = 1f
                    curDish?.dishId?.let {
                        listener?.onExpandableMenuQuantityChange(it, 0)
                    }
                }
            }
        }
    }

    private fun animateExpanding(expand: Boolean, animated: Boolean) {

        currentAnimator?.cancel()
        binding.menuItemQut.clearFocus()

        val startHeight = binding.menuItemMainLayout.measuredHeight

        binding.menuItemBottomLayout.isVisible = expand
        binding.menuItemClose.isVisible = expand

        binding.menuItemMainLayout.measure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
        )
        val targetHeight = binding.menuItemMainLayout.measuredHeight
        if (animated) {
            currentAnimator = ValueAnimator.ofInt(startHeight, targetHeight).apply {
                duration = expandAnimationDuration
                addUpdateListener {
                    binding.menuItemMainLayout.layoutParams = binding.menuItemMainLayout.layoutParams.apply {
                        height = it.animatedValue as Int
                    }
                    requestLayout()
                }
                addListener(
                        onStart = {
                            (context as? Activity)?.let {
                                Utils.hideKeyBoard(it)
                            }
                        },
                        onEnd = {
                            binding.menuItemMainLayout.layoutParams = binding.menuItemMainLayout.layoutParams.apply {
                                height = ViewGroup.LayoutParams.WRAP_CONTENT
                            }
                            requestLayout()
                            if (expand) {
                                binding.menuItemQut.requestFocus()
                            }
                        }
                )
                start()
            }
        } else {
            requestLayout()
        }
    }

    private fun collapse(animated: Boolean) {
        animateExpanding(false, animated)
    }

    private fun expand(animated: Boolean) {
        animateExpanding(true, animated)
    }

    fun initDish(dish: MenuAdapterModel, listener: ExpandableMenuItemListener?, isExpanded: Boolean) {
        this.listener = listener
        this.curDish = dish

        Glide.with(context).load(dish.img).placeholder(R.mipmap.ic_launcher_round)
                .apply(RequestOptions.circleCropTransform()).into(binding.menuItemImg)
        binding.menuItemTitle.text = dish.name

        updateQuantity(dish.quantity ?: 0)


        if (isExpanded) {
            expand(animated = false)
        } else {
            collapse(animated = false)
        }
    }

    private fun updateQuantity(quantity: Int) {
        if (quantity == defaultQuantity) {
            binding.menuItemUnlimited.isChecked = true
        } else {
            binding.menuItemUnlimited.isChecked = false

            binding.menuItemQut.removeTextChangedListener(textWatcher)
            if (quantity == 0) {
                binding.menuItemQut.setText("")
            } else {
                binding.menuItemQut.setText("$quantity")
            }
            binding.menuItemQut.addTextChangedListener(textWatcher)
        }
    }

    companion object {
        private const val expandAnimationDuration = 200L
        private const val defaultQuantity = 100
    }
}
