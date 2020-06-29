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
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.custom_views.UserImageView
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
import org.koin.androidx.viewmodel.ext.android.viewModel

class CookProfileDialog(val listener: CookProfileDialogListener, val cook: Cook) : DialogFragment(), HeaderView.HeaderViewListener,
    SingleFeedAdapter.SearchAdapterListener, UserImageView.UserImageViewListener {

    interface CookProfileDialogListener{
        fun onDishClick(menuItemId: Long)
    }

    private var dishAdapter: SingleFeedAdapter? = null
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

        cookProfileHeader.setHeaderViewListener(this)
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
        cookProfileImageView.setUser(cook)
        cookProfileImageView.setUserImageViewListener(this)
        cookProfileFragNameAndAge.text = "${cook.getFullName()}"//, ${cook.getAge()}"

        var profession = cook.profession
        var country = ""
        cook.country?.let{
            it.name?.let{
                country = ", ${it}"
            }
            Glide.with(context!!).load(it.flagUrl).circleCrop().into(cookProfileFragFlag)
        }
        cookProfileFragProfession.text = "$profession"// $country"
        cookProfileFragRating.text = cook.rating.toString()
        cookProfileFragCuisineGrid.clear()
        cookProfileFragCuisineGrid.initStackableView(cook.cuisines as ArrayList<SelectableIcon>)
        cookProfileFragCuisineGrid.initStackableView(cook.diets as ArrayList<SelectableIcon>)
        cookProfileFragStoryName.text = "${cook.firstName}'s Story"
        cookProfileFragStory.text = "${cook.about}"
        cookProfileFragDishBy.text = "Dishes By ${cook.firstName}"

        val certificates = cook.certificates
        cookProfileFragCertificateLayout.setOnClickListener { openCertificatesDialog(certificates) }
        if (certificates.size > 0) {
            cookProfileFragCertificate.text = "Certificate in ${cook.certificates[0]}"
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
        dishAdapter = SingleFeedAdapter(context!!, cook.dishes, this)
        cookProfileFragDishList.adapter = dishAdapter

        cookProfileFragRating.setOnClickListener { onRatingClick() }
    }

    private fun openCertificatesDialog(certificates: ArrayList<String>) {
        CertificatesDialog(certificates).show(childFragmentManager, Constants.CERTIFICATES_DIALOG_TAG)
    }

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

    override fun onHeaderBackClick() {
        dismiss()
    }



}