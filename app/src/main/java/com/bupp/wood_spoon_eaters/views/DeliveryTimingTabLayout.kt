package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.common.Constants
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bupp.wood_spoon_eaters.databinding.*
import com.bupp.wood_spoon_eaters.utils.Utils
import com.google.android.material.tabs.TabLayout
import com.trading212.stickyheader.dpToPx


class DeliveryTimingTabLayout @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr){

    private var binding: DeliveryTimingTabLayoutBinding = DeliveryTimingTabLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        initUi()
    }

    private fun initUi() {
        with(binding){
            tabLayout.addTab(tabLayout.newTab())
            tabLayout.addTab(tabLayout.newTab())
            tabLayout.addTab(tabLayout.newTab())
            tabLayout.addTab(tabLayout.newTab())
            tabLayout.addTab(tabLayout.newTab())
            tabLayout.addTab(tabLayout.newTab())
            tabLayout.addTab(tabLayout.newTab())
            tabLayout.addTab(tabLayout.newTab())
            tabLayout.addTab(tabLayout.newTab())
            tabLayout.addTab(tabLayout.newTab())
            tabLayout.addTab(tabLayout.newTab())
            tabLayout.addTab(tabLayout.newTab())

            tabLayout.getTabAt(0)!!.text = "Today"
            tabLayout.getTabAt(1)!!.text = "Wed 26/12"
            tabLayout.getTabAt(2)!!.text = "Fri 28/12"
            tabLayout.getTabAt(3)!!.text = "Sat 28/12"
            tabLayout.getTabAt(4)!!.text = "Wed 26/12"
            tabLayout.getTabAt(5)!!.text = "Fri 28/12"
            tabLayout.getTabAt(6)!!.text = "Sat 28/12"
            tabLayout.getTabAt(7)!!.text = "Wed 26/12"
            tabLayout.getTabAt(8)!!.text = "Fri 28/12"
            tabLayout.getTabAt(9)!!.text = "Sat 28/12"
            tabLayout.getTabAt(10)!!.text = "Wed 26/12"
            tabLayout.getTabAt(11)!!.text = "Sat 28/12"

            val startTab = tabLayout.getTabAt(0)!!.view
            val endTab = tabLayout.getTabAt(11)!!.view
            updateTabUi(tabLayout.getTabAt(0)!!, true)

            val paramsStart = startTab.layoutParams as ViewGroup.MarginLayoutParams
            paramsStart.setMargins(dpToPx(21F), 0, 0, 0)
            startTab.requestLayout()


            val paramsEnd = endTab.layoutParams as ViewGroup.MarginLayoutParams
            paramsEnd.setMargins(0, 0, dpToPx(21F), 0)
            endTab.requestLayout()

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let {
//                        updateTabUi(it, true)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    tab?.let {
//                        updateTabUi(it, false)
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

            })
        }
    }

    fun updateTabUi(tab: TabLayout.Tab, isSelected: Boolean) {
        val tabLayout = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(tab.position) as LinearLayout
        val tabTextView = tabLayout.getChildAt(1) as TextView
        val face = ResourcesCompat.getFont(
            binding.root.context, if (isSelected) {
                R.font.lato_bold
            } else {
                R.font.lato_reg
            }
        )
        tabTextView.typeface = face
    }


}