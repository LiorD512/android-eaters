package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide

import com.bupp.wood_spoon_eaters.R
import kotlinx.android.synthetic.main.cook_profile_fragment.*
import kotlinx.android.synthetic.main.single_dish_fragment_dialog_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.dialogs.OrderDateChooserDialog
import com.bupp.wood_spoon_eaters.custom_views.DishCounterView
import com.bupp.wood_spoon_eaters.custom_views.SingleDishHeader
import com.bupp.wood_spoon_eaters.custom_views.StatusBottomBar
import com.bupp.wood_spoon_eaters.custom_views.UserImageView
import com.bupp.wood_spoon_eaters.custom_views.feed_view.SingleFeedAdapter
import com.bupp.wood_spoon_eaters.dialogs.DishUnAvailableDialog
import com.bupp.wood_spoon_eaters.dialogs.StartNewCartDialog
import com.bupp.wood_spoon_eaters.dialogs.rating_dialog.RatingsDialog
import com.bupp.wood_spoon_eaters.features.main.profile.video_view.VideoViewDialog
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.CertificatesDialog
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderActivity
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.utils.Constants
import com.bupp.wood_spoon_eaters.utils.Utils
import java.util.*
import kotlin.collections.ArrayList


class SingleDishFragment(val menuItemId: Long, val listener: SingleDishDialogListener) : Fragment(),
    OrderDateChooserDialog.OrderDateChooserDialogListener, DishCounterView.DishCounterViewListener,
    SingleFeedAdapter.SearchAdapterListener, StatusBottomBar.StatusBottomBarListener,
    StartNewCartDialog.StartNewCartDialogListener, SingleDishHeader.SingleDishHeaderListener,
    UserImageView.UserImageViewListener {

    interface SingleDishDialogListener {
        fun onCheckout()
        fun onDishClick(itemId: Long)
    }

    companion object {
        fun newInstance(menuItemId: Long, listener: SingleDishDialogListener) = SingleDishFragment(menuItemId, listener)
    }



    var ingredientsAdapter: DishIngredientsAdapter? = null
    private lateinit var currentDish: FullDish
    private lateinit var dishAdapter: SingleFeedAdapter

    private var lastSelected: Int = -1

    val viewModel: SingleDishViewModel by viewModel<SingleDishViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.single_dish_fragment_dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        singleDishPb.show()
        viewModel.getFullDish(menuItemId)
        initObservers()

    }

    private fun checkForOpenOrder(currentDish: FullDish) {
        viewModel.checkForOpenOrder(currentDish.cook)
    }

    private fun initObservers() {
       viewModel.dishDetailsEvent.observe(this, Observer { event ->
            if (event != null) {
                singleDishPb.hide()
                if (event.isSuccess) {
                    if (event.dish != null) {
                        this.currentDish = event.dish
                        initUi()
                        checkForOpenOrder(currentDish)
                        if(!event.isAvailable){
                            handleUnAvailableCookingSlot()

                        }
                    }
                }
            }
        })

        viewModel.hasOpenOrder.observe(this, Observer { event ->
            if (event.hasOpenOrder) {
                StartNewCartDialog(event.cookInCart!!, event.currentShowingCook!!, this).show(childFragmentManager, Constants.START_NEW_CART_DIALOG_TAG)
            }
        })

        viewModel.postOrderEvent.observe(this, Observer { event ->
            if (event != null) {
                singleDishPb.hide()
                if (event.isSuccess) {
                    if (event.order != null) {
                        singleDishHeader.updateUi(SingleDishHeader.COOK)
                        singleDishScrollView.fullScroll(View.FOCUS_DOWN)
                        updateStatusBottomBar(type = Constants.STATUS_BAR_TYPE_CHECKOUT)
                    }
                } else {
                    Toast.makeText(context, "Problem uploading order", Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.getReviewsEvent.observe(this, Observer { event ->
            if (event != null) {
                singleDishPb.hide()
                if (event.isSuccess) {
                    if (event.reviews != null) {
                        RatingsDialog(event.reviews).show(childFragmentManager, Constants.RATINGS_DIALOG_TAG)
                    }
                } else {
                    Toast.makeText(context, "Problem uploading order", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onUserImageClick(cook: Cook?) {
        if(cook != null && !cook.video.isNullOrEmpty()){
            VideoViewDialog(cook).show(childFragmentManager, Constants.VIDEO_VIEW_DIALOG)
        }
    }

    private fun handleUnAvailableCookingSlot() {
        DishUnAvailableDialog().show(childFragmentManager, Constants.UNAVAILABLE_DISH_DIALOG_TAG)
        val cookingSlotStartingTime = currentDish.menuItem?.cookingSlot?.startsAt
        viewModel.updateChosenDeliveryDate(cookingSlotStartingTime!!)
    }


    override fun onNewCartClick() {
        viewModel.clearCurrentOpenOrder()
    }

    private fun initUi() {
        initHeader()
        initInfo()
        initIngredient()
        initCook()
        initCartBottomBar()
    }

    private fun initHeader() {
        singleDishHeader.setSingleDishHeaderListener(this)
    }

    override fun onBackClick() {
        (activity as NewOrderActivity).finish()
    }

    override fun onPageClick(page: Int) {
        hidePages()
        when(page){
            SingleDishHeader.INFO -> {singleDishInfoLayout.visibility = View.VISIBLE}
            SingleDishHeader.COOK -> {singleDishCookLayout.visibility = View.VISIBLE}
            SingleDishHeader.INGREDIENT -> {singleDishIngredientLayout.visibility = View.VISIBLE}
        }
    }


    private fun hidePages(){
        singleDishInfoLayout.visibility = View.GONE
        singleDishCookLayout.visibility = View.GONE
        singleDishIngredientLayout.visibility = View.GONE
    }

    private fun initCartBottomBar() {
        singleDishStatusBar.setStatusBottomBarListener(this)
        updateStatusBottomBar(type = Constants.STATUS_BAR_TYPE_CART, price = currentDish.price.value, itemCount = 1)
    }

    fun updateStatusBottomBar(type: Int? = null, price: Double? = null, itemCount: Int? = null) {
        singleDishStatusBar.updateStatusBottomBar(type = type, price = price, itemCount = itemCount)
    }

    override fun onStatusBarClicked(type: Int?) {
        when(type){
            Constants.STATUS_BAR_TYPE_CART -> {
                addToCart()
            }
            Constants.STATUS_BAR_TYPE_CHECKOUT -> {
                (activity as NewOrderActivity).onCheckout()
            }
        }
    }

    fun addToCart() {
        singleDishPb.show()
        val cookingSlotId = currentDish.menuItem?.cookingSlot?.id
        val quantity = singleDishInfoDishCounter.getDishCount()
        val removedIngredients = ingredientsAdapter?.ingredientsRemoved
        val note = singleDishIngredientInstructions.getText()
        viewModel.addToCart(
            cookingSlotId = cookingSlotId,
            dishId = currentDish.id,
            quantity = quantity,
            removedIngredients = removedIngredients,
            note = note)
    }




    private fun initInfo() {
        singleDishInfoCook.setUser(currentDish.cook)
        singleDishInfoCook.setUserImageViewListener(this)
        Glide.with(context!!).load(currentDish.thumbnail).into(singleDishInfoImg)
        singleDishInfoFavorite.setIsFav(currentDish.isFavorite)
        singleDishInfoFavorite.setDishId(currentDish.id)

        singleDishInfoName.text = currentDish.name
        singleDishInfoCookName.text = "By ${currentDish.cook.getFullName()}"
        if (currentDish.cook.country != null) {
            Glide.with(context!!).load(currentDish.cook.country.flagUrl).into(singleDishInfoCookFlag)
        }


        singleDishInfoDescription.text = currentDish.description
        singleDishInfoPrice.text = currentDish.price.formatedValue
        val menuItem = currentDish.menuItem
        if (menuItem != null) {
            singleDishQuantityView.initQuantityView(menuItem)
            val quantityLeft = menuItem.quantity - menuItem.unitsSold
            singleDishInfoDishCounter.setDishCounterViewListener(this, quantityLeft)
//            singleDishInfoQuantity.text = "$quantityLeft Left"
        }

        initOrderDate()
        singleDishInfoRatingVal.text = currentDish.rating.toString()
        singleDishInfoRating.setOnClickListener { onRatingClick() }
    }



    private fun onRatingClick() {
        viewModel.getDishReview(currentDish.cook.id)
    }

    private fun initOrderDate() {
        val orderAtDate = currentDish.menuItem?.orderAt
        if(orderAtDate != null){
            singleDishInfoDate.text = "${Utils.parseDateToDayDateHour(orderAtDate)}"
        }else if(currentDish.doorToDoorTime != null){
            singleDishInfoDate.text = "ASAP, ${currentDish.doorToDoorTime}"
        }
        singleDishInfoDate.setOnClickListener { openOrderTimeDialog() }
        singleDishInfoDelivery.text = "${viewModel.getDropoffLocation()}"

        singleDishInfoDate.setPaintFlags(singleDishInfoDate.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG);
    }

    override fun onDishCounterChanged(count: Int) {
        //update order manager
        val newValue = currentDish.price.value*count
        updateStatusBottomBar(price = newValue, itemCount = count)
    }

    private fun openOrderTimeDialog() {
        val currentDateSelected = currentDish.menuItem
        val availableMenuItems = currentDish.availableMenuItems
        OrderDateChooserDialog(currentDateSelected, availableMenuItems, this).show(childFragmentManager, Constants.ORDER_DATE_CHOOSER_DIALOG_TAG)
    }

    override fun onDateChoose(selectedMenuItem: MenuItem, newChosenDate: Date) {
        if (selectedMenuItem != null) {
            //update order manager
            currentDish.menuItem = selectedMenuItem // update menuItem to update ui in the next visit in openOrderTimeDialog()
            viewModel.updateChosenDeliveryDate(newChosenDate)
            singleDishInfoDate.text = "${Utils.parseDateToDayDateHour(newChosenDate)}"
//            singleDishInfoDate.text = "${currentDish.menuItem?.eta}"
        }
    }

    private fun initIngredient() {
        singleDishIngredientCalories.text = "${currentDish.calorificValue} Calories"
        singleDishIngredientProtein.text = "${currentDish.proteins}g Protein"
        singleDishIngredientCarbohydrate.text = "${currentDish.proteins}g Carbohydrate"

        if(currentDish.cookingMethods.size > 0){
            singleDishIngredientSauteing.text = currentDish.cookingMethods[0].name
        }


        singleDishIngredientList.setLayoutManager(LinearLayoutManager(context))
        var divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.chooser_divider, null))
        singleDishIngredientList.addItemDecoration(divider)
        ingredientsAdapter = DishIngredientsAdapter(context!!, currentDish.dishIngredients)
        singleDishIngredientList.adapter = ingredientsAdapter
    }


    private fun initCook() {
        cookProfileImageView.setUser(currentDish.cook)
        cookProfileImageView.setUserImageViewListener(this)
        cookProfileFragNameAndAge.text = "${currentDish.cook.getFullName()}, ${currentDish.cook.getAge()}"

        var profession = currentDish.cook.profession
        var country = ""
        currentDish.cook.country?.let{
            country = ", ${it.name}"
            Glide.with(context!!).load(it.flagUrl).into(cookProfileFragFlag)
        }
        cookProfileFragProfession.text = "$profession $country"
        cookProfileFragRating.text = currentDish.cook.rating.toString()
        cookProfileFragCuisineGrid.initStackableView(currentDish.cook.cuisines as ArrayList<SelectableIcon>)
        cookProfileFragStoryName.text = "${currentDish.cook.firstName}'s Story"
        cookProfileFragStory.text = "${currentDish.cook.about}"
        cookProfileFragDishBy.text = "Other Available Dishes By ${currentDish.cook.firstName}"

        val certificates = currentDish.cook.certificates
        cookProfileFragCertificateLayout.setOnClickListener { openCertificatesDialog(certificates) }
        if (certificates.size > 0) {
            cookProfileFragCertificate.text = "Certificate in ${currentDish.cook.certificates[0]}"
            if (certificates.size > 1) {
                cookProfileFragCertificateCount.visibility = View.VISIBLE
                cookProfileFragCertificateCount.text = "+ ${certificates.size - 1} More"
            } else {
                cookProfileFragCertificateCount.visibility = View.GONE
            }
        } else {
            cookProfileFragCertificateLayout.visibility = View.GONE
        }

        cookProfileFragDishList.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false))
        dishAdapter = SingleFeedAdapter(context!!, currentDish.cook.dishes, this)
        cookProfileFragDishList.adapter = dishAdapter

        cookProfileFragRating.setOnClickListener { onRatingClick() }
    }

    override fun onDishClick(dish: Dish) {
        listener.onDishClick(dish.menuItem.id)
//        SingleDishFragment.newInstance(dish.menuItem.id, listener).show(fragmentManager, Constants.SINGLE_DISH_TAG)
    }

    private fun openCertificatesDialog(certificates: ArrayList<String>) {
        CertificatesDialog(certificates).show(childFragmentManager, Constants.CERTIFICATES_DIALOG_TAG)
    }


//interfaces -

//    override fun onFavClick(isChecked: Boolean) {
//        if(isChecked){
//            viewModel.likeDish(currentDish.id)
//        }else{
//            viewModel.unlikeDish(currentDish.id)
//        }
//    }


}
