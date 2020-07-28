package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish

//import com.tapadoo.alerter.Alerter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidadvance.topsnackbar.TSnackbar
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.PlusMinusView
import com.bupp.wood_spoon_eaters.custom_views.SingleDishHeader
import com.bupp.wood_spoon_eaters.custom_views.UserImageView
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.custom_views.feed_view.SingleFeedAdapter
import com.bupp.wood_spoon_eaters.custom_views.orders_bottom_bar.OrdersBottomBar
import com.bupp.wood_spoon_eaters.dialogs.*
import com.bupp.wood_spoon_eaters.dialogs.additional_dishes.AdditionalDishesDialog
import com.bupp.wood_spoon_eaters.dialogs.order_date_chooser.NationwideShippingChooserDialog
import com.bupp.wood_spoon_eaters.dialogs.order_date_chooser.OrderDateChooserDialog
import com.bupp.wood_spoon_eaters.dialogs.rating_dialog.RatingsDialog
import com.bupp.wood_spoon_eaters.features.main.cook_profile.CooksProfileDishesAdapter
import com.bupp.wood_spoon_eaters.features.main.profile.video_view.VideoViewDialog
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderActivity
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderSharedViewModel
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.CertificatesDialog
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.utils.Constants
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.cook_profile_fragment.*
import kotlinx.android.synthetic.main.single_dish_fragment_dialog_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class SingleDishFragment() : Fragment(),
    OrderDateChooserDialog.OrderDateChooserDialogListener, //DishCounterView.DishCounterViewListener,
    SingleFeedAdapter.SearchAdapterListener,
    StartNewCartDialog.StartNewCartDialogListener, SingleDishHeader.SingleDishHeaderListener,
    UserImageView.UserImageViewListener, AddressMissingDialog.AddressMissingDialogListener,
    DishMediaAdapter.DishMediaAdapterListener, AdditionalDishesDialog.AdditionalDishesDialogListener, OrdersBottomBar.OrderBottomBatListener,
    PlusMinusView.PlusMinusInterface, CooksProfileDishesAdapter.CooksProfileDishesListener {


    var listener: SingleDishDialogListener? = null

    fun setSingleDishDialogListener(listener: SingleDishDialogListener) {
        this.listener = listener
    }

    interface SingleDishDialogListener {
        fun onCheckout()
        fun onDishClick(itemId: Long)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.single_dish_fragment_dialog_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            viewModel.menuItemId = arguments!!.getLong(ARG_PARAM)
            viewModel.isEvent = arguments!!.getBoolean(EVENT_PARAM)
        }
    }

    companion object {
        private val ARG_PARAM = "menuItemId"
        private val EVENT_PARAM = "isEvent"

        fun newInstance(menuItemId: Long, isEvent: Boolean = false): SingleDishFragment {
            val fragment = SingleDishFragment()
            try {
                val args = Bundle()
                args.putLong(ARG_PARAM, menuItemId)
                args.putBoolean(EVENT_PARAM, isEvent)
                fragment.arguments = args
            } catch (e: Exception) {
                Log.d("wowSingle", "newInstance exception")
            }
            return fragment
        }
    }

    var ingredientsAdapter: DishIngredientsAdapter? = null
    //    private lateinit var currentDish: FullDish
    private lateinit var dishAdapter: CooksProfileDishesAdapter

    val viewModel by viewModel<SingleDishViewModel>()
    val ordersViewModel by sharedViewModel<NewOrderSharedViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getFullDish()
        ordersViewModel.checkCartStatus()
        initObservers()
    }


    private fun checkForOpenOrder(currentDish: FullDish) {
        ordersViewModel.checkForOpenOrderAndShowClearCartDialog(currentDish.menuItem?.cookingSlot?.id, currentDish.cook.getFullName())
    }

    private fun initObservers() {
        ordersViewModel.checkCartStatus.observe(this, Observer { pendingOrderEvent ->
            if (pendingOrderEvent.hasPendingOrder) {
//                updateStatusBottomBar(type = Constants.STATUS_BAR_TYPE_CHECKOUT, checkoutPrice = ordersViewModel.calcTotalDishesPrice())
                ordersViewModel.getLastOrderDetails()
                singleDishStatusBar.handleBottomBar(showCheckout = true)
            } else {
                singleDishStatusBar.handleBottomBar(showCheckout = false)
            }
        })

        viewModel.fullDish.observe(this, Observer { fullDish ->
            fullDish?.let{
                initUi(it)
                checkForOpenOrder(it)
                ordersViewModel.initAdditionalDishDialog(it)
            }
        })

        viewModel.availability.observe(this, Observer { availabilityEvent ->
            availabilityEvent.startingTime?.let {
                if (!availabilityEvent.isAvailable) {
                    handleUnAvailableCookingSlot(it)
                }
                if (availabilityEvent.isSoldOut) {
                    handleSoldoutCookingSlot(it)
                }
            }

        })

        ordersViewModel.orderData.observe(this, Observer { orderDataEvent ->
            handleOrderData(orderDataEvent)
        })

        ordersViewModel.orderRequestData.observe(this, Observer { orderRequestData ->
            handlerRequestOrderData(orderRequestData)
        })

        viewModel.progressData.observe(this, Observer { shouldShow ->
            if (shouldShow) {
                singleDishPb.show()
            } else {
                singleDishPb.hide()
            }
        })

        ordersViewModel.hasOpenOrder.observe(this, Observer { event ->
            if (event.hasOpenOrder) {
                StartNewCartDialog(event.cookInCartName!!, event.currentShowingCookName!!, this).show(childFragmentManager, Constants.START_NEW_CART_DIALOG_TAG)
            }
        })

        ordersViewModel.showDialogEvent.observe(this, Observer { showDialog ->
            if (showDialog) {
                AdditionalDishesDialog(this).show(childFragmentManager, Constants.ADDITIONAL_DISHES_DIALOG)
            }
        })

        ordersViewModel.procceedToCheckoutEvent.observe(this, Observer { procceedToCheckout ->
            if(procceedToCheckout){
                onProceedToCheckout()
            }
        })

        viewModel.getReviewsEvent.observe(this, Observer { event ->
            event?.let {
                if (event.isSuccess) {
                    event.reviews?.let {
                        RatingsDialog(it).show(childFragmentManager, Constants.RATINGS_DIALOG_TAG)
                    }
                } else {
                    Toast.makeText(context, "Problem uploading order", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onAdditionalDialogDismiss() {
        ordersViewModel.checkCartStatus()
    }

    override fun onProceedToCheckout() {
        (activity as NewOrderActivity).onCheckout()
    }

    private fun handlerRequestOrderData(orderRequestData: OrderRequest?) {

    }

    private fun handleOrderData(orderDataEvent: Order?) {
        if (orderDataEvent != null) {
            updateStatusBottomBar(type = Constants.STATUS_BAR_TYPE_CHECKOUT, checkoutPrice = ordersViewModel.calcTotalDishesPrice())
            singleDishHeader.updateUi(SingleDishHeader.COOK)
            singleDishScrollView.fullScroll(View.FOCUS_DOWN)
            singleDishStatusBar.handleBottomBar(false)
        } else {
            Toast.makeText(context, "Problem uploading order", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onUserImageClick(cook: Cook?) {
        if (cook != null && !cook.video.isNullOrEmpty()) {
            VideoViewDialog(cook).show(childFragmentManager, Constants.VIDEO_VIEW_DIALOG)
        }
    }


    private fun handleUnAvailableCookingSlot(startsAt: Date) {
//        DishUnAvailableDialog().show(childFragmentManager, Constants.UNAVAILABLE_DISH_DIALOG_TAG)
        Log.d("wowSingleDish", "handleUnAvailableCookingSlot startsAt: $startsAt")
//        showUnavailableDishAlerter()
        startsAt?.let {
            viewModel.updateChosenDeliveryDate(newChosenDate = it)
        }
    }

//    private fun showUnavailableDishAlerter() {
//        val snackbar = TSnackbar.make(
//            singleDishMainLayout,
//            R.string.un_available_dish_alerter_body,
//            TSnackbar.LENGTH_LONG
//        )
//        val snackBarView = snackbar.view
//        snackBarView.setBackgroundColor(ContextCompat.getColor(context!!, R.color.teal_blue))
//        val textView = snackBarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text) as TextView
//        textView.setGravity(Gravity.CENTER_HORIZONTAL)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            textView.setTextAppearance(R.style.SemiBold13Dark)
//        }
//        textView.setTextColor(ContextCompat.getColor(context!!, R.color.white))
//        snackbar.show()
//    }


    private fun handleSoldoutCookingSlot(startsAt: Date) {
        DishSoldOutDialog().show(childFragmentManager, Constants.UNAVAILABLE_DISH_DIALOG_TAG)
        singleDishStatusBar.handleBottomBar(showActiveOrders = false)
        singleDishPlusMinus.setViewEnabled(false)
    }


    override fun onNewCartClick() {
        ordersViewModel.initNewOrder()
        ordersViewModel.checkCartStatus()
    }

    private fun initUi(fullDish: FullDish) {
        initHeader()
        initInfo(fullDish)
        initIngredient(fullDish)
        initCook(fullDish)
        initCartBottomBar(fullDish)
    }

    private fun initHeader() {
        singleDishHeader.setSingleDishHeaderListener(this)
    }

    override fun onBackClick() {
        viewModel.getCurrentDish()?.let {
            (activity as NewOrderActivity).finishWithCookId(it.cook.id)
        }
    }

    override fun onPageClick(page: Int) {
        hidePages()
        when (page) {
            SingleDishHeader.INFO -> {
                singleDishStatusBar.handleBottomBar(true)
                singleDishScrollView.smoothScrollTo(0, singleDishInfoLayout.top)
                singleDishInfoLayout.visibility = View.VISIBLE
            }
            SingleDishHeader.COOK -> {
                singleDishScrollView.smoothScrollTo(0, singleDishCookLayout.top)
                singleDishCookLayout.visibility = View.VISIBLE
            }
            SingleDishHeader.INGREDIENT -> {
                singleDishScrollView.smoothScrollTo(0, singleDishIngredientLayout.top)
                singleDishIngredientLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun hidePages() {
        singleDishInfoLayout.visibility = View.GONE
        singleDishCookLayout.visibility = View.GONE
        singleDishIngredientLayout.visibility = View.GONE
    }

    private fun initCartBottomBar(currentDish: FullDish) {
//        singleDishStatusBar.setStatusBottomBarListener(this)
        singleDishStatusBar.setOrdersBottomBarListener(this)
        updateStatusBottomBar(type = Constants.STATUS_BAR_TYPE_CART, price = currentDish.getPriceObj().value, itemCount = 1)
    }

    override fun onBottomBarOrdersClick(type: Int) {
        when (type) {
            Constants.STATUS_BAR_TYPE_CART -> {
                if (viewModel.hasValidDeliveryAddress()) {
                    addToCart()
                } else {
                    openAddressMissingDialog()
                }
            }
            Constants.STATUS_BAR_TYPE_CHECKOUT -> {
                (activity as NewOrderActivity).onCheckout()
            }
        }
    }

    override fun onBottomBarCheckoutClick() {
        (activity as NewOrderActivity).onCheckout()
    }

    fun updateStatusBottomBar(type: Int? = null, price: Double? = null, checkoutPrice: Double? = null, itemCount: Int? = null) {
        Log.d("wowSingleDish", "updateStatusBottomBar type: $type")
        singleDishStatusBar.updateStatusBottomBar(type = type, price = price, checkoutPrice = checkoutPrice, itemCount = itemCount)
    }

//    override fun onStatusBarClicked(type: Int?) {
//        when (type) {
//            Constants.STATUS_BAR_TYPE_CART -> {
//                if (viewModel.hasValidDeliveryAddress()) {
//                    addToCart()
//                } else {
//                    openAddressMissingDialog()
//                }
//            }
//            Constants.STATUS_BAR_TYPE_CHECKOUT -> {
//                (activity as NewOrderActivity).onCheckout()
//            }
//        }
//    }

    private fun openAddressMissingDialog() {
        AddressMissingDialog(this).show(childFragmentManager, Constants.ADDRESS_MISSING_DIALOG)
    }

    override fun openUpdateAddress() {
        (activity as NewOrderActivity).openAddressChooser()
    }

    fun addToCart() {
//        singleDishPb.show()
        val quantity = singleDishPlusMinus.counter
        val removedIngredients = ingredientsAdapter?.ingredientsRemoved
        val note = singleDishIngredientInstructions.getText()
        ordersViewModel.addToCart(
            fullDish = viewModel.fullDish.value,
            quantity = quantity,
            removedIngredients = removedIngredients,
            note = note
        )
    }


    private fun initInfo(currentDish: FullDish) {
        singleDishPlusMinus.setViewEnabled(true)
        singleDishInfoCook.setUser(currentDish.cook)
        singleDishInfoCook.setUserImageViewListener(this)
//        Glide.with(context!!).load(currentDish.thumbnail).into(singleDishInfoImg)
        singleDishInfoFavorite.setIsFav(currentDish.isFavorite)
        singleDishInfoFavorite.setDishId(currentDish.id)

        val pagerAdapter = DishMediaAdapter(this)
        singleDishInfoImagePager.adapter = pagerAdapter

        pagerAdapter.setItem(currentDish.getMediaList())

        if (currentDish.getMediaList().size > 1) {
            singleDishInfoCircleIndicator.setViewPager(singleDishInfoImagePager)
        }

        singleDishInfoName.text = currentDish.name
        singleDishInfoCookName.text = "By ${currentDish.cook.getFullName()}"
        if (currentDish.cook.country != null) {
            Glide.with(context!!).load(currentDish.cook.country.flagUrl).into(singleDishInfoCookFlag)
        }

        if (currentDish.cook.diets.size > 0) {
            singleDishInfoDietryList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val dietaryAdapter = CooksDietaryAdapter(context!!, currentDish.cook.diets)
            singleDishInfoDietryList.adapter = dietaryAdapter
        } else {
            singleDishInfoDietryList.visibility = View.GONE
        }


        singleDishInfoDescription.text = currentDish.description
        singleDishInfoPrice.text = currentDish.getPriceObj().formatedValue

        val menuItem = currentDish.menuItem
        if (menuItem != null) {
            singleDishQuantityView.initQuantityView(menuItem)
            val quantityLeft = menuItem.quantity - menuItem.unitsSold
            val currentCounter = singleDishCount.text.toString()
            singleDishPlusMinus.setPlusMinusListener(this, initialCounter = currentCounter.toInt(), quantityLeft = quantityLeft)
        }

        initOrderDate(currentDish)
        singleDishInfoRatingVal.text = currentDish.rating.toString()
        singleDishInfoRating.setOnClickListener { onRatingClick() }

        if (currentDish.cooksInstructions != null && currentDish.cooksInstructions.isNotEmpty()) {
            singleDishInstructionsLayout.visibility = View.VISIBLE
            singleDishInstructionsBody.text = currentDish.cooksInstructions
        } else {
            singleDishInstructionsLayout.visibility = View.GONE
        }

        currentDish.menuItem?.cookingSlot?.isNationwide?.let{
            if(it){

            }
        }
    }

    override fun onPlayClick(url: String) {
        val uri = Uri.parse(url)
        VideoPlayerDialog(uri).show(childFragmentManager, Constants.VIDEO_PLAYER_DIALOG)
    }

    private fun onRatingClick() {
        viewModel.getDishReview()
    }

    private fun initOrderDate(currentDish: FullDish) {
        if(currentDish.isNationwide){
            //todo : add this ui after design is ready
//            singleDishInfoNationwideLayout.visibility = View.VISIBLE
            singleDishInfoDeliveryTimeLayout.visibility = View.GONE

//            singleDishNationwideBtn.setOnClickListener { viewModel.onShippingMethodSelectClick() }
        }else {
//            singleDishInfoNationwideLayout.visibility = View.GONE
            singleDishInfoDeliveryTimeLayout.visibility = View.VISIBLE

            val orderAtDate = currentDish.menuItem?.orderAt
            if (orderAtDate != null) {
                currentDish.menuItem?.cookingSlot?.orderFrom?.let {
                    singleDishInfoDate.text = Utils.parseDateToDayDateHour(it)
                }

            } else if (currentDish.doorToDoorTime != null) {
                singleDishInfoDate.text = "ASAP, ${currentDish.doorToDoorTime}"
            }
            if (viewModel.isEvent) {
//            singleDishInfoDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            } else {
//            singleDishInfoDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icons_edit, 0, 0, 0);
                singleDishChangeTimeBtn.setOnClickListener { openOrderTimeDialog() }
            }
            singleDishInfoDelivery.text = "${viewModel.getDropoffLocation()}"
        }
    }

    private fun openNationWideChooser() {

    }


    override fun onPlusMinusChange(counter: Int, position: Int) {
        //updating ui only
        viewModel.getCurrentDish()?.let {
            val newValue = it.price.value * counter
            updateStatusBottomBar(price = newValue, itemCount = counter)
        }
        singleDishCount.text = "$counter"
    }

    private fun openOrderTimeDialog() {
        viewModel.getCurrentDish()?.let {
            val currentDateSelected = it.menuItem
            val availableMenuItems = it.availableMenuItems
            OrderDateChooserDialog(currentDateSelected, availableMenuItems, this)
                .show(childFragmentManager, Constants.ORDER_DATE_CHOOSER_DIALOG_TAG)
        }
    }

    override fun onDateChoose(selectedMenuItem: MenuItem, newChosenDate: Date) {
        if (selectedMenuItem != null) {
            //update order manager
//            currentDish.menuItem = selectedMenuItem // update menuItem to update ui in the next visit in openOrderTimeDialog()
            viewModel.updateChosenDeliveryDate(selectedMenuItem, newChosenDate)
            singleDishInfoDate.text = "${Utils.parseDateToDayDateHour(newChosenDate)}"
            viewModel.fetchDishForNewDate(selectedMenuItem.id)
//            singleDishInfoDate.text = "${currentDish.menuItem?.eta}"
        }
    }

    private fun initIngredient(currentDish: FullDish) {
        singleDishIngredientCalories.text = "${currentDish.calorificValue.toInt()} Calories"
        singleDishIngredientProtein.text = "${currentDish.proteins.toInt()}g Protein"
        singleDishIngredientCarbohydrate.text = "${currentDish.carbohydrates.toInt()}g Carbohydrate"

        if (currentDish.cookingMethods.size > 0) {
            singleDishIngredientSauteing.text = currentDish.cookingMethods[0].name
        }


        singleDishIngredientList.setLayoutManager(LinearLayoutManager(context))
        var divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.chooser_divider, null))
        singleDishIngredientList.addItemDecoration(divider)
        ingredientsAdapter = DishIngredientsAdapter(context!!, currentDish.dishIngredients)
        singleDishIngredientList.adapter = ingredientsAdapter
    }


    private fun initCook(currentDish: FullDish) {
        val cook = currentDish.cook
        cookProfileImageView.setUser(cook)
        cookProfileImageView.setUserImageViewListener(this)
        cookProfileFragNameAndAge.text = "${cook.getFullName()}"//, ${currentDish.cook.getAge()}"

        var profession = cook.profession
        var country = ""
        cook.country?.let {
            country = ", ${it.name}"
            Glide.with(context!!).load(it.flagUrl).into(cookProfileFragFlag)
        }
        cookProfileFragProfession.text = "$profession"// $country"
        cookProfileFragRating.text = cook.rating.toString()
        //cuisine
        if(cook.cuisines != null && cook.cuisines.size > 0){
            cookProfileFragCuisineLayout.visibility = View.VISIBLE
            cookProfileFragCuisineGrid.clear()
            cookProfileFragCuisineGrid.initStackableView(cook.cuisines as ArrayList<SelectableIcon>)
        }else{
            cookProfileFragCuisineLayout.visibility = View.GONE
        }

        //dietry
        if(cook.diets != null && cook.diets.size > 0){
            cookProfileFragDietaryLayout.visibility = View.VISIBLE
            cookProfileFragDietryGrid.clear()
            cookProfileFragDietryGrid.initStackableView(cook.diets as ArrayList<SelectableIcon>)
        }else{
            cookProfileFragDietaryLayout.visibility = View.GONE
        }

        //Certificates
        val certificates = cook.certificates
//        cookProfileFragCertificateLayout.setOnClickListener { openCertificatesDialog(certificates) }
        if (certificates != null && certificates.size > 0) {
            cookProfileFragCertificateLayout.visibility = View.VISIBLE
            cookProfileFragCertificateGrid.clear()
            cookProfileFragCertificateGrid.initStackableViewWith(certificates)
        } else {
            cookProfileFragCertificateLayout.visibility = View.GONE
        }

        cookProfileFragStoryName.text = "${cook.firstName}'s Story"
        cookProfileFragStory.text = "${cook.about}"
        cookProfileFragDishBy.text = "Dishes By ${cook.firstName}"

        cookProfileFragDishList.layoutManager = LinearLayoutManager(context)
        dishAdapter = CooksProfileDishesAdapter(context!!, cook.dishes, this)
        cookProfileFragDishList.adapter = dishAdapter
        val divider = DividerItemDecorator(ContextCompat.getDrawable(context!!, R.drawable.divider))
        cookProfileFragDishList.addItemDecoration(divider)

        cookProfileFragRating.setOnClickListener { onRatingClick() }
    }

    override fun onDishClick(dish: Dish) {
        dish.menuItem?.let {
            listener?.onDishClick(it.id)
        }
//        SingleDishFragment.newInstance(dish.menuItem.id, listener).show(fragmentManager, Constants.SINGLE_DISH_TAG)
    }

    private fun openCertificatesDialog(certificates: ArrayList<String>) {
        CertificatesDialog(certificates).show(childFragmentManager, Constants.CERTIFICATES_DIALOG_TAG)
    }


}
