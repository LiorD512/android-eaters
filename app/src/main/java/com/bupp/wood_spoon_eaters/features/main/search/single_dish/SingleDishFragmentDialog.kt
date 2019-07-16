package com.bupp.wood_spoon_eaters.features.main.search.single_dish

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide

import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.FavoriteBtn
import com.bupp.wood_spoon_eaters.model.Dish
import kotlinx.android.synthetic.main.cook_profile_fragment.*
import kotlinx.android.synthetic.main.single_dish_fragment_dialog_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel
import androidx.lifecycle.Observer
import com.bupp.wood_spoon.dialogs.OrderDateChooserDialog
import com.bupp.wood_spoon_eaters.custom_views.DishCounterView
import com.bupp.wood_spoon_eaters.custom_views.feed_view.SingleFeedAdapter
import com.bupp.wood_spoon_eaters.features.main.search.SearchAdapter
import com.bupp.wood_spoon_eaters.features.main.search.single_dish.sub_screen.CertificatesDialog
import com.bupp.wood_spoon_eaters.model.FullDish
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.model.SelectableIcon
import com.bupp.wood_spoon_eaters.utils.Constants


class SingleDishFragmentDialog(val menuItemId: Long, val listener: SingleDishDialogListener) : DialogFragment(), FavoriteBtn.FavoriteBtnListener,
    OrderDateChooserDialog.OrderDateChooserDialogListener, DishCounterView.DishCounterViewListener,
    SingleFeedAdapter.SearchAdapterListener {


    interface SingleDishDialogListener {
        fun onCheckout()
    }

    companion object {
        fun newInstance(menuItemId: Long, listener: SingleDishDialogListener) = SingleDishFragmentDialog(menuItemId, listener)
    }

    val INFO = 0
    val INGREDIENT = 1
    val COOK = 2

    var ingredientsAdapter: DishIngredientsAdapter? = null
    private lateinit var currentDish: FullDish
    private lateinit var dishAdapter: SingleFeedAdapter

    private var lastSelected: Int = -1

    val viewModel: SingleDishViewModel by viewModel<SingleDishViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.single_dish_fragment_dialog_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        singleDishPb.show()
        viewModel.getFullDish(menuItemId)

        viewModel.dishDetailsEvent.observe(this, Observer { event ->
            if (event != null) {
                singleDishPb.hide()
                if (event.isSuccess) {
                    if (event.dish != null) {
                        this.currentDish = event.dish
                        initUi()
                    }
                } else {

                }
            }
        })

        viewModel.postOrderEvent.observe(this, Observer { event ->
            if (event != null) {
                singleDishPb.hide()
                if (event.isSuccess) {
                    if (event.order != null) {
                        updateBottomBarUi(true)
                    }
                } else {
                    Toast.makeText(context, "Problem uploading order", Toast.LENGTH_SHORT).show()
                }
            }
        })


    }

    private fun initUi() {
        initHeader()
        initInfo()
        initIngredient()
        initCook()
        initCartBottomBar()
    }

    private fun initCartBottomBar() {
        singleDishAddToCartPrice.text = currentDish.price.formatedValue
        singleDishAddToCartLayout.setOnClickListener { addToCart() }
    }

    private fun addToCart() {
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

    private fun updateBottomBarUi(isCheckout: Boolean) {
        if(isCheckout){
            singleDishAddToCartPrice.visibility = View.GONE
            singleDishAddToCartTitle.text = "checkout"
            singleDishAddToCartLayout.setOnClickListener { checkout() }
        }else{
            singleDishAddToCartPrice.visibility = View.VISIBLE
            val count = singleDishInfoDishCounter.getDishCount()
            singleDishAddToCartTitle.text = "Add ${count} To Cart"
            val newValue = currentDish.price.value*count
            singleDishAddToCartPrice.text = "$$newValue"
            singleDishAddToCartLayout.setOnClickListener { addToCart() }
        }
    }

    private fun checkout() {
        listener.onCheckout()
    }

    private fun initHeader() {
        singleDishHeaderBack.setOnClickListener { dismiss() }
        singleDishHeaderInfo.setOnClickListener { scrollPageTo(INFO) }
        singleDishHeaderCook.setOnClickListener { scrollPageTo(COOK) }
        singleDishHeaderIngredient.setOnClickListener { scrollPageTo(INGREDIENT) }

        singleDishHeaderInfo.performClick()
    }
//        singleDishScrollView.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {
//            override fun onScrollChange(
//                v: NestedScrollView?,
//                scrollX: Int,
//                scrollY: Int,
//                oldScrollX: Int,
//                oldScrollY: Int
//            ) {
//                checkScrollPosition(scrollY + 250)
//
//            }
//
//        })

//        selectView(INFO)

//    private fun checkScrollPosition(scrollY: Int) {
////        val infoTop = singeDishCollapsingLayout.y
//        val ingredientTop = singleDishIngredientLayout.y
//        val cookTop = singleDishCookLayout.y
//
//        Log.d("wowSingle", "infoTop: $infoTop, ingredientTop: $ingredientTop, cookTop: $cookTop")
//        if (scrollY < ingredientTop) {
//            selectView(INFO)
//        } else if (scrollY > ingredientTop && scrollY < cookTop) {
//            selectView(INGREDIENT)
//        } else {
//            selectView(COOK)
//        }
//    }


    private fun scrollPageTo(scrollPos: Int) {
        unSelectAll()
        when (scrollPos) {
            INFO -> {
                singleDishHeaderInfo.isSelected = true
                singleDishInfoLayout.visibility = View.VISIBLE
//                singleDishFragAppBarLayout.setExpanded(true)
//                singleDishScrollView.smoothScrollTo(0, singleDishInfoLayout.y.toInt())
            }
            INGREDIENT -> {
                singleDishHeaderIngredient.isSelected = true
                singleDishIngredientLayout.visibility = View.VISIBLE
//                singleDishFragAppBarLayout.setExpanded(false)
//                singleDishScrollView.smoothScrollTo(0, singleDishIngredientLayout.y.toInt())
            }
            COOK -> {
                singleDishHeaderCook.isSelected = true
                singleDishCookLayout.visibility = View.VISIBLE
//                singleDishFragAppBarLayout.setExpanded(false)
//                singleDishScrollView.smoothScrollTo(0, singleDishCookLayout.y.toInt())
            }
        }
    }


    private fun selectView(selectedView: Int) {
        if (selectedView != lastSelected) {
            Log.d("wowSingleDish", "selectView: $selectedView")
            lastSelected = selectedView
            unSelectAll()
            when (selectedView) {
                INFO -> {
                    singleDishHeaderInfo.isSelected = true
                }
                INGREDIENT -> {
                    singleDishHeaderIngredient.isSelected = true
                }
                COOK -> {
                    singleDishHeaderCook.isSelected = true
                }
            }
        }
    }

    private fun unSelectAll() {
        singleDishHeaderInfo.isSelected = false
        singleDishHeaderCook.isSelected = false
        singleDishHeaderIngredient.isSelected = false
        singleDishInfoLayout.visibility = View.GONE
        singleDishCookLayout.visibility = View.GONE
        singleDishIngredientLayout.visibility = View.GONE
    }

    private fun initInfo() {
        singleDishInfoCook.setUser(currentDish.cook)
        Glide.with(context).load(currentDish.thumbnail).into(singleDishInfoImg)
        singleDishInfoFavorite.setFavListener(this)

        singleDishInfoName.text = currentDish.name
        singleDishInfoCookName.text = "by ${currentDish.cook.getFullName()}"
        if (currentDish.cook.country != null) {
            Glide.with(context).load(currentDish.cook.country.flagUrl).into(singleDishInfoCookFlag)
        }
        singleDishInfoDescription.text = currentDish.description
        singleDishInfoPrice.text = currentDish.price.formatedValue
        val menuItem = currentDish.menuItem
        if (menuItem != null) {
            singleDishInfoQuantity.text = "${menuItem.unitsSold}/${menuItem.quantity} Left"
        }

        initOrderDate()
        singleDishInfoDate.text = "${currentDish.menuItem?.eta}"
        singleDishInfoDate.setOnClickListener { openOrderTimeDialog() }
        singleDishInfoRating.text = currentDish.rating.toString()
        singleDishInfoDishCounter.setDishCounterViewListener(this)
    }

    private fun initOrderDate() {
        val userLastChosenDate = viewModel.getUserChosenDeliveryDate()
    }

    override fun onDishCounterChanged(count: Int) {
        //update order manager
        singleDishAddToCartTitle.text = "Add $count To Cart"
        val newValue = currentDish.price.value*count
        singleDishAddToCartPrice.text = "$$newValue"
    }

    private fun openOrderTimeDialog() {
        val currentDateSelected = currentDish.menuItem
        val availableMenuItems = currentDish.availableMenuItems
        OrderDateChooserDialog(currentDateSelected, availableMenuItems, this).show(childFragmentManager, Constants.ORDER_DATE_CHOOSER_DIALOG_TAG)
    }

    override fun onDateChoose(menuItem: MenuItem) {
        if (menuItem != null) {
            //update order manager
            currentDish.menuItem = menuItem
            singleDishInfoDate.text = "${currentDish.menuItem?.eta}"
        }
    }

    private fun initIngredient() {
        singleDishIngredientCalories.text = "${currentDish.calorificValue} Calories"
        singleDishIngredientProtein.text = "${currentDish.proteins}g Protein"
        singleDishIngredientCarbohydrate.text = "${currentDish.proteins}g Carbohydrate"
        singleDishIngredientSauteing.text = currentDish.cookingMethods[0].name

        singleDishIngredientList.setLayoutManager(LinearLayoutManager(context))
        var divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.chooser_divider, null))
        singleDishIngredientList.addItemDecoration(divider)
        ingredientsAdapter = DishIngredientsAdapter(context!!, currentDish.dishIngredients)
        singleDishIngredientList.adapter = ingredientsAdapter
    }


    private fun initCook() {
        cookProfileImageView.setUser(currentDish.cook)
        cookProfileFragNameAndAge.text = "${currentDish.cook.getFullName()}, ${currentDish.cook.getAge()}"

        var profession = "${currentDish.cook.profession},"
        var country = "${currentDish.cook.country?.name}"
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
    }

    override fun onDishClick(dish: Dish) {
        SingleDishFragmentDialog.newInstance(dish.menuItem.id, listener).show(fragmentManager, Constants.SINGLE_DISH_DIALOG)
    }

    private fun openCertificatesDialog(certificates: ArrayList<String>) {
        CertificatesDialog(certificates).show(childFragmentManager, Constants.CERTIFICATES_DIALOG_TAG)
    }


//interfaces -

    override fun onFavClick(isChecked: Boolean) {

    }

}
