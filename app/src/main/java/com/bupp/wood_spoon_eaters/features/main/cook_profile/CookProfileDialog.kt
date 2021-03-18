package com.bupp.wood_spoon_eaters.features.main.cook_profile

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.custom_views.UserImageView
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.custom_views.feed_view.SingleFeedAdapter
import com.bupp.wood_spoon_eaters.bottom_sheets.rating_dialog.RatingsDialog
import com.bupp.wood_spoon_eaters.features.main.profile.video_view.VideoViewDialog
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.SelectableIcon
import com.bupp.wood_spoon_eaters.common.Constants
import kotlinx.android.synthetic.main.cook_profile_dialog.*
import kotlinx.android.synthetic.main.cook_profile_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CookProfileDialog(val listener: CookProfileDialogListener) : DialogFragment(), HeaderView.HeaderViewListener,
     UserImageView.UserImageViewListener, CooksProfileDishesAdapter.CooksProfileDishesListener {

    interface CookProfileDialogListener{
        fun onDishClick(menuItemId: Long)
    }

    private var dishAdapter: CooksProfileDishesAdapter? = null
    val viewModel by viewModel<CookProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)

        viewModel.initCookData(arguments?.getLong(Constants.ARG_COOK_ID, -1))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.cook_profile_dialog, null)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    private fun initObservers() {
        viewModel.getReviewsEvent.observe(viewLifecycleOwner,  { reviews ->
            cookProfilePb.hide()
                reviews?.let{
                    RatingsDialog(reviews).show(childFragmentManager, Constants.RATINGS_DIALOG_TAG)
                }
        })
        viewModel.getCookEvent.observe(viewLifecycleOwner,{
            it?.let{
                initCook(it)
            }
        })
        viewModel.progressData.observe(viewLifecycleOwner, {
            if(it){
                cookProfilePb.show()
            }else{
                cookProfilePb.hide()
            }
        })
    }

    private fun initCook(cook: Cook) {
        cookProfileFragBackBtn.visibility = View.VISIBLE
        cookProfileFragBackBtn.setOnClickListener { dismiss() }

        cookProfileImageView.setUser(cook)
        cookProfileImageView.setUserImageViewListener(this)
        cookProfileFragNameAndAge.text = "${cook.getFullName()}"//, ${cook.getAge()}"

        var profession = cook.profession
        var country = ""
        cook.country?.let{
            it.name?.let{
                country = ", ${it}"
            }
            Glide.with(requireContext()).load(it.flagUrl).into(cookProfileFragFlag)
        }
        cookProfileFragProfession.text = "$profession"// $country"
        cookProfileFragRating.text = cook.rating.toString()

        //cuisine
        if(cook?.cuisines.isNotEmpty()){
            cookProfileFragCuisineLayout.visibility = View.VISIBLE
            cookProfileFragCuisineGrid.clear()
            cookProfileFragCuisineGrid.initStackableView(cook.cuisines as ArrayList<SelectableIcon>)
        }else{
            cookProfileFragCuisineLayout.visibility = View.GONE
        }

        //dietry
        if(cook.diets.isNotEmpty()){
            cookProfileFragDietaryLayout.visibility = View.VISIBLE
            cookProfileFragDietryGrid.clear()
            cookProfileFragDietryGrid.initStackableView(cook.diets as ArrayList<SelectableIcon>)
        }else{
            cookProfileFragDietaryLayout.visibility = View.GONE
        }

        //Certificates
        val certificates = cook.certificates
        if (certificates.isNotEmpty()) {
            cookProfileFragCertificateLayout.visibility = View.VISIBLE
            cookProfileFragCertificateGrid.initStackableViewWith(certificates)
        } else {
            cookProfileFragCertificateLayout.visibility = View.GONE
        }

        cookProfileFragStoryName.text = "${cook.firstName}'s Story"
        cookProfileFragStory.text = "${cook.about}"
        cookProfileFragDishBy.text = "Dishes By ${cook.firstName}"

        cookProfileFragDishList.layoutManager = LinearLayoutManager(context)
        dishAdapter = CooksProfileDishesAdapter(requireContext(), cook.dishes, this)
        cookProfileFragDishList.adapter = dishAdapter
        val divider = DividerItemDecorator(ContextCompat.getDrawable(requireContext(), R.drawable.divider))
        cookProfileFragDishList.addItemDecoration(divider)

        cookProfileFragRating.setOnClickListener { onRatingClick() }

        cookProfileFragReviews.text = "${cook.reviewCount} Reviews"
    }

//    private fun openCertificatesDialog(certificates: ArrayList<String>) {
//        CertificatesDialog(certificates).show(childFragmentManager, Constants.CERTIFICATES_DIALOG_TAG)
//    }

    override fun onDishClick(dish: Dish){
        dish.menuItem?.let{
            listener.onDishClick(it.id)
        }
    }

    private fun onRatingClick() {
        cookProfilePb.show()
        viewModel.getDishReview()
    }

    override fun onUserImageClick(cook: Cook?) {
        if(cook != null && !cook.video.isNullOrEmpty()){
            VideoViewDialog(cook).show(childFragmentManager, Constants.VIDEO_VIEW_DIALOG)
        }
    }

}