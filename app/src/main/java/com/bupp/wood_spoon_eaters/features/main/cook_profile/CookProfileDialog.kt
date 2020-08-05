package com.bupp.wood_spoon_eaters.features.main.cook_profile

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.custom_views.UserImageView
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.custom_views.feed_view.SingleFeedAdapter
import com.bupp.wood_spoon_eaters.dialogs.rating_dialog.RatingsDialog
import com.bupp.wood_spoon_eaters.dialogs.web_docs.CookProfileViewModel
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.features.main.profile.video_view.VideoViewDialog
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.CertificatesDialog
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.SelectableIcon
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.cook_profile_dialog.*
import kotlinx.android.synthetic.main.cook_profile_fragment.*
import kotlinx.android.synthetic.main.order_items_view.view.*
import kotlinx.android.synthetic.main.single_feed_list_view.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CookProfileDialog(val listener: CookProfileDialogListener, val cook: Cook) : DialogFragment(), HeaderView.HeaderViewListener,
    SingleFeedAdapter.SearchAdapterListener, UserImageView.UserImageViewListener, CooksProfileDishesAdapter.CooksProfileDishesListener {

    interface CookProfileDialogListener{
        fun onDishClick(menuItemId: Long)
    }

    private var dishAdapter: CooksProfileDishesAdapter? = null
    val viewModel by viewModel<CookProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.cook_profile_dialog, null)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCook()
        initObservers()
    }

    private fun initObservers() {
        viewModel.getReviewsEvent.observe(this, Observer { event ->
            if (event != null) {
                cookProfilePb.hide()
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

    private fun initCook() {
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

        cookProfileFragReviews.text = "${cook.reviewCount} Reviews"
    }

//    private fun openCertificatesDialog(certificates: ArrayList<String>) {
//        CertificatesDialog(certificates).show(childFragmentManager, Constants.CERTIFICATES_DIALOG_TAG)
//    }

    override fun onDishClick(dish: Dish){
        dish.menuItem?.let{
            listener.onDishClick(it.id)
        }
//        (activity as MainActivity).loadNewOrderActivity()
    }

    private fun onRatingClick() {
        cookProfilePb.show()
        viewModel.getDishReview(cook.id)
    }

    override fun onUserImageClick(cook: Cook?) {
        if(cook != null && !cook.video.isNullOrEmpty()){
            VideoViewDialog(cook).show(childFragmentManager, Constants.VIDEO_VIEW_DIALOG)
        }
    }

}