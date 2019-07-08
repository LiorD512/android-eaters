package com.bupp.wood_spoon_eaters.features.main.search.single_dish

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide

import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.FavoriteBtn
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.cook_profile_fragment.*
import kotlinx.android.synthetic.main.single_dish_fragment_dialog_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel
import android.graphics.Rect
import com.bupp.wood_spoon_eaters.features.main.search.SearchAdapter
import com.bupp.wood_spoon_eaters.model.SelectableIcon
import com.bupp.wood_spoon_eaters.utils.Constants


class SingleDishFragmentDialog(val curDish: Dish) : DialogFragment(), FavoriteBtn.FavoriteBtnListener,
    SearchAdapter.SearchAdapterListener {

    val INFO = 0
    val INGREDIENT = 1
    val COOK = 2
    var adapter: DishIngredientsAdapter? = null
    private lateinit var dishAdapter: SearchAdapter

    private var lastSelected: Int = -1

    companion object {
        fun newInstance(dish: Dish) = SingleDishFragmentDialog(dish)
    }

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

        initUi()
    }

    private fun initUi() {
        initHeader()
        initInfo()
        initIngredient()
        initCook()
    }

    private fun initHeader() {
        singleDishHeaderBack.setOnClickListener { dismiss() }
        singleDishHeaderInfo.setOnClickListener { scrollPageTo(INFO) }
        singleDishHeaderCook.setOnClickListener { scrollPageTo(COOK) }
        singleDishHeaderIngredient.setOnClickListener { scrollPageTo(INGREDIENT) }

        singleDishScrollView.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {
            override fun onScrollChange(
                v: NestedScrollView?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int) {
                checkScrollPosition(scrollY+250)

            }

        })

        selectView(INFO)
    }

    private fun checkScrollPosition(scrollY: Int) {
        val infoTop = singeDishCollapsingLayout.y
        val ingredientTop = singleDishIngredientLayout.y
        val cookTop = singleDishCookLayout.y

        Log.d("wowSingle", "infoTop: $infoTop, ingredientTop: $ingredientTop, cookTop: $cookTop")
        if (scrollY < ingredientTop) {
            selectView(INFO)
        } else if (scrollY > ingredientTop && scrollY < cookTop) {
            selectView(INGREDIENT)
        } else {
            selectView(COOK)
        }
    }

    private fun isViewVisible(view: View): Boolean {
        val scrollBounds = Rect()
        singleDishScrollView.getDrawingRect(scrollBounds)

        val top = view.y
        val bottom = top + 200
        return if (scrollBounds.contains(top.toInt(),
                bottom.toInt())) {//.top < top && (scrollBounds.top + 200) > bottom) {
//            Log.d("wowSingle","top: $top, bottom: $bottom, scrollBoundsTop: ${scrollBounds.top}, scrollBoundsBottom: ${scrollBounds.bottom} ")
            true
        } else {
            false
        }
    }

    private fun scrollPageTo(scrollPos: Int) {
        unSelectAll()
        when (scrollPos) {
            INFO -> {
                singleDishHeaderInfo.isSelected = true
                singleDishFragAppBarLayout.setExpanded(true)
                singleDishScrollView.smoothScrollTo(0, singleDishInfoLayout.y.toInt())
            }
            INGREDIENT -> {
                singleDishHeaderIngredient.isSelected = true
                singleDishFragAppBarLayout.setExpanded(false)
                singleDishScrollView.smoothScrollTo(0, singleDishIngredientLayout.y.toInt())
            }
            COOK -> {
                singleDishHeaderCook.isSelected = true
                singleDishFragAppBarLayout.setExpanded(false)
                singleDishScrollView.smoothScrollTo(0, singleDishCookLayout.y.toInt())
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
    }

    private fun initInfo() {
        singleDishInfoCook.setUser(curDish.cook)
        Glide.with(context).load(curDish.thumbnail).into(singleDishInfoImg)
        singleDishInfoFavorite.setFavListener(this)

        singleDishInfoName.text = curDish.name
        singleDishInfoCookName.text = "by ${curDish.cook.getFullName()}"
        if (curDish.cook.country != null) {
            Glide.with(context).load(curDish.cook.country.flagUrl).into(singleDishInfoCookFlag)
        }
        singleDishInfoDescription.text = curDish.description
        singleDishInfoPrice.text = curDish.price.formatedValue
        val upcomingSlot = curDish.upcomingSlot
        var date = "ASAP"
        var timeRange = "${curDish.prepTimeRange.minTime}-${curDish.prepTimeRange.maxTime} min"
        if (upcomingSlot != null) {
            date = Utils.parseDateToDayDateHour(upcomingSlot?.startsAt)
            singleDishInfoQuantity.text = "${upcomingSlot?.unitSold} / ${upcomingSlot?.quantity}"
        }
        singleDishInfoDate.text = "$date, $timeRange"
        singleDishInfoRating.text = curDish.rating.toString()
    }

    private fun initIngredient() {
        singleDishIngredientCalories.text = "${curDish.calorificValue} Calories"
        singleDishIngredientProtein.text = "${curDish.proteins}g Protein"
        singleDishIngredientCarbohydrate.text = "${curDish.proteins}g Carbohydrate"
        singleDishIngredientSauteing.text = curDish.cookingMethods[0].name

        singleDishIngredientList.setLayoutManager(LinearLayoutManager(context))
        var divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.chooser_divider, null))
        singleDishIngredientList.addItemDecoration(divider)
        adapter = DishIngredientsAdapter(context!!, curDish.dishIngredients)
        singleDishIngredientList.adapter = adapter
    }


    private fun initCook() {
        cookProfileImageView.setUser(curDish.cook)
        cookProfileFragNameAndAge.text = "${curDish.cook.getFullName()}, ${curDish.cook.getAge()}"

        var profession = "${curDish.cook.profession},"
        var country = "${curDish.cook.country?.name}"
        cookProfileFragProfession.text = "$profession $country"
        cookProfileFragRating.text = curDish.cook.rating.toString()
        cookProfileFragCuisineGrid.initStackableView(curDish.cook.cuisines as ArrayList<SelectableIcon>)
        cookProfileFragStoryName.text = "${curDish.cook.firstName}'s Story"
        cookProfileFragStory.text = "${curDish.cook.about}"
        cookProfileFragDishBy.text = "Other Available Dishes By ${curDish.cook.firstName}"

        val certificates = curDish.cook.certificates
        cookProfileFragCertificateLayout.setOnClickListener { openCertificatesDialog(certificates) }
        if (certificates.size > 0) {
            cookProfileFragCertificate.text = "Certificate in ${curDish.cook.certificates[0]}"
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
        var divider = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
        divider.setDrawable(resources.getDrawable(R.drawable.horizontal_trans_divider, null))
        cookProfileFragDishList.addItemDecoration(divider)
        dishAdapter = SearchAdapter(context!!, arrayListOf(), this)
        dishAdapter.updateDishes(curDish.cook.dishes)
        cookProfileFragDishList.adapter = dishAdapter
//        cookProfileFragDishList.adapter //todo
    }

    override fun onDishClick(dish: Dish) {
        SingleDishFragmentDialog.newInstance(dish).show(fragmentManager, Constants.SINGLE_DISH_DIALOG)
    }

    private fun openCertificatesDialog(certificates: ArrayList<String>) {

    }


//interfaces -

    override fun onFavClick(isChecked: Boolean) {

    }

}
