package com.bupp.wood_spoon_eaters.features.track_your_order

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.fees_and_tax_bottom_sheet.FeesAndTaxBottomSheet
import com.bupp.wood_spoon_eaters.bottom_sheets.tool_tip_bottom_sheet.ToolTipBottomSheet
import com.bupp.wood_spoon_eaters.bottom_sheets.track_order.TrackOrderData
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.custom_views.simpler_views.SimpleMotionTransitionListener
import com.bupp.wood_spoon_eaters.databinding.ActivityTrackYourOrderBinding
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.OrderTrackDetails
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.OrderUserInfo
import com.bupp.wood_spoon_eaters.features.base.BaseActivity
import com.bupp.wood_spoon_eaters.features.restaurant.RestaurantActivity
import com.bupp.wood_spoon_eaters.features.track_your_order.menu.TrackOrderMenuBottomSheet
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.OrderState
import com.bupp.wood_spoon_eaters.utils.AnimationUtil
import com.bupp.wood_spoon_eaters.utils.Utils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import jp.wasabeef.glide.transformations.BlurTransformation
import org.koin.androidx.viewmodel.ext.android.viewModel


class TrackYourOrderActivity : BaseActivity(), TrackOrderNewAdapter.TrackOrderNewAdapterListener, OnMapReadyCallback,
    TrackOrderMenuBottomSheet.TrackOrderMenuListener {

    lateinit var binding: ActivityTrackYourOrderBinding

    private var adapter: TrackOrderNewAdapter? = null
    val viewModel by viewModel<ActiveOrderTrackerViewModel>()

    private var curOrderId: Long? = null
    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        postponeEnterTransition()

        window.sharedElementEnterTransition = TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transition)

        binding = ActivityTrackYourOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.enterTransition = null//TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transition)
//        window?.exitTransition = TransitionInflater.from(this).inflateTransition(R.transition.fade_transition)

        curOrderId = intent.getLongExtra("order_id", -1)

        initTransitionData()

        initUi()
        initObservers()
        initMap()

        viewModel.getCurrentOrder(curOrderId)
        viewModel.startSilentUpdate()

        viewModel.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_TRACK_ORDER)
        viewModel.logEvent(Constants.EVENT_CLICK_TRACK_YOUR_ORDER)
    }


    private fun initTransitionData() {
        val mapPreviewUrl = intent.getStringExtra("map_preview_url")
        val restaurantName = intent.getStringExtra("restaurant_name")
        val statusTitle = intent.getStringExtra("status_title")
        val statusSubTitle = intent.getStringExtra("status_subtitle")
        val pbState = intent.getStringExtra("pb_state") ?: ""
        val thumbnail = intent.getStringExtra("thumbnail") ?: ""

        with(binding) {
            trackOrderProgressName.text = restaurantName
            trackOrderProgressStatusTitle.text = statusTitle
            trackOrderProgressStatusSubTitle.text = statusSubTitle
            trackOrderProgressPb.setState(OrderState.valueOf(pbState))

            val multiTransformation = MultiTransformation(BlurTransformation(10, 2), CenterCrop())
            Glide.with(this@TrackYourOrderActivity)
                .load(mapPreviewUrl)
                .apply(RequestOptions.bitmapTransform(multiTransformation))
                .into(trackOrderMapPreview)

            Glide.with(this@TrackYourOrderActivity).load(thumbnail).circleCrop().into(trackOrderActThumbnail)
        }

        startPostponedEnterTransition()
    }


    private fun initUi() {
        adapter = TrackOrderNewAdapter(this, this)
        binding.trackOrderActList.layoutManager = LinearLayoutManager(this)
        binding.trackOrderActList.adapter = adapter

        binding.trackOrderActBackButton.setOnClickListener { onBackPressed() }

        binding.trackOrderActMyLocation.setOnClickListener {
            Log.d("wowTrackOrderFragment", "trackOrderActMyLocation click")
            viewModel.onMapReady(this)
        }
        binding.trackOrderSpace.setOnClickListener {
            Log.d("wowTrackOrderFragment", "trackOrderSpace click")
            expandMap()
        }
        binding.trackOrderActCollapse.setOnClickListener {
            Log.d("wowTrackOrderFragment", "trackOrderActMyLocation click")
            collapseMap()
        }
        binding.trackOrderActHelp.setOnClickListener {
            TrackOrderMenuBottomSheet().show(supportFragmentManager, Constants.TRACK_ORDER_DIALOG_TAG)
        }

        val sharedElementEnterTransition: Transition = window.sharedElementEnterTransition
        sharedElementEnterTransition.addListener(@RequiresApi(Build.VERSION_CODES.O)
        object : TransitionListenerAdapter() {
            override fun onTransitionEnd(transition: Transition?) {
                super.onTransitionEnd(transition)
                Log.d("wowTrackOrderFragment", "onTransitionEnd")
                AnimationUtil().scaleUpWithAlpha(binding.trackOrderActThumbnailLayout)
            }
        })

        binding.motionLayout.addTransitionListener(object : SimpleMotionTransitionListener() {
            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                when(currentId){
                    R.id.scrollStart -> {
                        Log.d("wowTrackOrderFragment", "onAnimationEnd scrollStart")
                        binding.motionLayout.setTransition(R.id.scrollStart, R.id.scrollEnd)
                        binding.motionLayout.progress = 0f

                        //adjust restaurant title padding
                        val curPadding = binding.trackOrderActMainList.paddingTop
                        if(curPadding <= Utils.toPx(50)){
                            val varl = ValueAnimator.ofInt(Utils.toPx(30))
                            varl.duration = 250
                            varl.addUpdateListener { animation ->
                                val padding = Utils.toDp(curPadding + animation.animatedValue as Int)
                                Log.d("wowTrackOrderFragment", "collapseMap padding $padding dp")
                                binding.trackOrderActMainList.setPadding(0, Utils.toPx(padding), 0, 0)
                            }
                            varl.start()
                        }
                    }
                    R.id.expandEnd -> {
                        Log.d("wowTrackOrderFragment", "onAnimationEnd expandEnd")
                        //adjust restaurant title padding
                        val varl = ValueAnimator.ofInt(Utils.toPx(30))
                        varl.duration = 250
                        varl.addUpdateListener { animation ->
                            val padding = Utils.toDp(Utils.toPx(80) - (animation.animatedValue as Int))
                            Log.d("wowTrackOrderFragment", "expandMap padding $padding dp")
                            binding.trackOrderActMainList.setPadding(0, Utils.toPx(padding), 0, 0)
                        }
                        varl.start()
                    }
                }

            }
        })

    }

    private fun expandMap() {
        binding.motionLayout.setTransition(binding.motionLayout.currentState, R.id.expandEnd)
        binding.motionLayout.transitionToEnd()

        mMap?.uiSettings?.setAllGesturesEnabled(true)
        binding.trackOrderSpace.elevation = 0f
        binding.trackOrderSpace.setOnClickListener(null)

        binding.trackOrderActMainList.setOnTouchListener { v, event -> true }
    }

    private fun collapseMap() {
        binding.motionLayout.transitionToStart()
        binding.motionLayout.addTransitionListener(object: SimpleMotionTransitionListener(){
            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                binding.motionLayout.setTransition(binding.motionLayout.currentState, R.id.scrollEnd)
                binding.motionLayout.progress = 0f
                binding.motionLayout.removeTransitionListener(this)
            }
        })
        binding.trackOrderSpace.elevation = 10f
        binding.trackOrderActMainList.setOnTouchListener { v, event -> false }
        binding.trackOrderSpace.setOnClickListener{
            expandMap()
        }
    }

    private fun initObservers() {
        viewModel.getCurrentOrderDetails.observe(this, { result ->
            result.order.let {
                Log.d("wowTrackOrderFragment", "updating orders")
                updateOrderUi(it, result.userInfo)
            }
        })
        viewModel.mapData.observe(this, {
            handleMapData(it)
        })
        viewModel.feeAndTaxDialogData.observe(this, {
            FeesAndTaxBottomSheet.newInstance(it.fee, it.tax, it.minFee).show(supportFragmentManager, Constants.FEES_AND_tAX_BOTTOM_SHEET)
        })
        viewModel.chefPageClick.observe(this, {
            startActivity(Intent(this, RestaurantActivity::class.java).putExtra(Constants.ARG_RESTAURANT, it))
        })
    }

    private fun handleMapData(mapData: ActiveOrderTrackerViewModel.MapData?) {
        mapData?.let {
            val builder = LatLngBounds.Builder()
            it.markers.forEach {
                mMap?.addMarker(it)
                builder.include(it.position)
            }

            val width = binding.trackOrderMap.measuredWidth
            val height = binding.trackOrderMap.measuredHeight
            val padding = ((width * 15) / 100)

            val bound = builder.build()
            try{
                mMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bound, width, height, padding))
                mMap?.setOnCameraIdleListener {
                    if (binding.trackOrderMapPreview.alpha > 0) {
                        AnimationUtil().alphaOut(binding.trackOrderMapPreview, customStartDelay = 500)
                    }
                }
            }catch (ex: Exception){
                Log.d(TAG, "error loading map")
                viewModel.onMapReady(this)
            }

        }
    }

    private fun updateOrderUi(
        order: Order,
        userInfo: OrderUserInfo?
    ) {
        val adapterDetails = OrderTrackDetails(order, userInfo)

        binding.trackOrderActTopHeaderRestaurantName.text = order.restaurant?.restaurantName
        binding.trackOrderActTopHeaderDeliveryTime.text = order.etaToDisplay

        val data = mutableListOf<TrackOrderData<Any>>(
            TrackOrderData(TrackOrderNewAdapter.VIEW_TYPE_DETAILS, adapterDetails),
        )

        with(binding){
            trackOrderProgressPb.setState(order.status)
            trackOrderProgressName.text = order.restaurant?.restaurantName
            trackOrderProgressStatusTitle.text = order.extendedStatus?.title
            trackOrderProgressStatusSubTitle.text = order.extendedStatus?.subtitle
        }

        adapter?.submitList(data)
    }

    private fun initMap() {
        val mapFragment: SupportMapFragment = SupportMapFragment.newInstance()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.trackOrderMap, mapFragment)
        fragmentTransaction.commit()

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        if (mMap == null) {
            mMap = googleMap
            mMap?.uiSettings?.setAllGesturesEnabled(false)
            viewModel.onMapReady(this)
        }
    }


    override fun onToolTipClick(type: Int) {
        var titleText = ""
        var bodyText = ""
        if (type == Constants.FEES_AND_ESTIMATED_TAX) {
            viewModel.onFeesAndTaxInfoClick()
        } else {
            when (type) {
                Constants.TOOL_TIP_SERVICE_FEE -> {
                    titleText = resources.getString(R.string.tool_tip_service_fee_title)
                    bodyText = resources.getString(R.string.tool_tip_service_fee_body)
                }
                Constants.TOOL_TIP_CHECKOUT_SERVICE_FEE -> {
                    titleText = resources.getString(R.string.tool_tip_service_fee_title)
                    bodyText = resources.getString(R.string.tool_tip_service_fee_body)
                }
                Constants.TOOL_TIP_CHECKOUT_DELIVERY_FEE -> {
                    titleText = resources.getString(R.string.tool_tip_delivery_fee_title)
                    bodyText = resources.getString(R.string.tool_tip_delivery_fee_body)
                }
                Constants.TOOL_TIP_COURIER_TIP -> {
                    titleText = resources.getString(R.string.tool_tip_courier_title)
                    bodyText = resources.getString(R.string.tool_tip_courier_body)
                }
            }
            ToolTipBottomSheet.newInstance(titleText, bodyText).show(supportFragmentManager, Constants.FREE_TEXT_BOTTOM_SHEET)
        }
    }

    override fun onChefPageClick() {
        viewModel.onChefClick()
    }

    override fun onDestroy() {
        viewModel.endUpdates()
        mMap = null
        curOrderId = null
        adapter = null
        super.onDestroy()
    }

    override fun onOrderCanceled() {
        finish()
    }

    override fun onBackPressed() {
//        finishAfterTransition()
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//        supportFinishAfterTransition()
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
//        super.onBackPressed()
    }

    companion object {
        const val TAG = "wowTrackOrder"

    }

}