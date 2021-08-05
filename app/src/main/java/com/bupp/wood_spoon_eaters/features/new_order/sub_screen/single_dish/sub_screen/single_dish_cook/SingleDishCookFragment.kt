package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_cook

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.views.UserImageVideoView
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.databinding.CookProfileFragmentBinding
import com.bupp.wood_spoon_eaters.features.main.cook_profile.CooksDishesAdapter
import com.bupp.wood_spoon_eaters.features.main.profile.video_view.VideoViewDialog
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderMainViewModel
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.SelectableIcon
import com.segment.analytics.Analytics
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SingleDishCookFragment : Fragment(R.layout.cook_profile_fragment), CooksDishesAdapter.CooksProfileDishesListener,
    UserImageVideoView.UserImageViewListener {

    val binding: CookProfileFragmentBinding by viewBinding()
    val mainViewModel by sharedViewModel<NewOrderMainViewModel>()
    private lateinit var dishAdapter: CooksDishesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Analytics.with(requireContext()).screen("dishHomeChef")

        initUi()
        initObservers()
    }

    private fun initUi() {
        with(binding){
            cookProfileImageView.setUserImageViewListener(this@SingleDishCookFragment)

            cookProfileFragDishList.layoutManager = LinearLayoutManager(context)
            dishAdapter = CooksDishesAdapter(this@SingleDishCookFragment)
            cookProfileFragDishList.adapter = dishAdapter
            val divider = DividerItemDecorator(ContextCompat.getDrawable(requireContext(), R.drawable.divider))
            cookProfileFragDishList.addItemDecoration(divider)
        }
    }

    private fun initObservers() {
        mainViewModel.dishCookEvent.observe(viewLifecycleOwner, Observer{
            initCook(it)
        })
    }

    @SuppressLint("SetTextI18n")
    fun initCook(cook: Cook) {
        with(binding){
            cookProfileImageView.setUser(cook)
            cookProfileFragNameAndAge.text = cook.getFullName()

            dishAdapter.submitList(cook.dishes)

            val profession = cook.profession
            cook.country?.let {
                Glide.with(requireContext()).load(it.flagUrl).into(cookProfileFragFlag)
            }
//        cookProfileFragProfession.text = profession
            cookProfileFragRating.text = cook.rating.toString()

            //cuisine
            if(!cook.cuisines.isNullOrEmpty()){
                cookProfileFragCuisineLayout.visibility = View.VISIBLE
//            cookProfileFragCuisineGrid.clear()
                cookProfileFragCuisineGrid.initStackableView(cook.cuisines as ArrayList<SelectableIcon>)
            }else{
                cookProfileFragCuisineLayout.visibility = View.GONE
            }

            //dietary
            if(!cook.diets.isNullOrEmpty()){
                cookProfileFragDietaryLayout.visibility = View.VISIBLE
//            cookProfileFragDietryGrid.clear()
                cookProfileFragDietryGrid.initStackableView(cook.diets as ArrayList<SelectableIcon>)
            }else{
                cookProfileFragDietaryLayout.visibility = View.GONE
            }

            //Certificates
            val certificates = cook.certificates
            if (!certificates.isNullOrEmpty()) {
                cookProfileFragCertificateLayout.visibility = View.VISIBLE
                cookProfileFragCertificateGrid.initStackableViewWith(certificates)
            } else {
                cookProfileFragCertificateLayout.visibility = View.GONE
            }

            cookProfileFragStoryName.text = "${cook.firstName}'s Story"
            cookProfileFragStory.text = cook.about
            cookProfileFragDishBy.text = "Dishes By ${cook.firstName}"


            cookProfileFragRating.setOnClickListener { mainViewModel.getDishReview(cookId = cook.id) }

            cookProfileFragReviews.text = "${cook.reviewCount} Reviews"
        }

    }

    override fun onUserImageClick(cook: Cook?) {
        cook?.video?.let{
            VideoViewDialog(cook).show(childFragmentManager, Constants.VIDEO_VIEW_DIALOG)
        }
    }

    override fun onDishClick(dish: Dish) {
        mainViewModel.onCooksProfileDishClick(dish.menuItem?.id)
    }
}