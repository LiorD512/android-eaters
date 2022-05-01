package com.bupp.wood_spoon_chef.presentation.features.main.account

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.databinding.FragmentAccountBinding
import com.bupp.wood_spoon_chef.databinding.ProfileMenuViewBinding
import com.bupp.wood_spoon_chef.presentation.dialogs.web_docs.WebDocsDialog
import com.bupp.wood_spoon_chef.presentation.features.base.BaseFragment
import com.bupp.wood_spoon_chef.presentation.features.main.account.dialogs.LogoutDialog
import com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.*
import com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.about.AboutBottomSheet
import com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.edit_account_and_kitchen.EditAccountBottomSheet
import com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.edit_account_and_kitchen.EditKitchenBottomSheet
import com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.payment.PaymentBottomSheet
import com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.reviews.ReviewsBottomSheet
import com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.support_center.SupportBottomSheet
import com.bupp.wood_spoon_chef.data.remote.model.Cook
import com.bupp.wood_spoon_chef.utils.DateUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountFragment : BaseFragment(R.layout.fragment_account), LogoutDialog.LogoutDialogListener {

    private val viewModel: AccountViewModel by viewModel()
    private var binding: FragmentAccountBinding? = null
    private var buttonsBinding: ProfileMenuViewBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAccountBinding.bind(view)
        buttonsBinding = ProfileMenuViewBinding.bind(binding!!.root)

        viewModel.initData()
        initUi()
        initObservers()
    }

    private fun initUi() {
        initMenuListeners()
    }

    private fun initMenuListeners() {
        with(buttonsBinding!!) {
            profileMenuMyAddress.setOnClickListener {
                val address = viewModel.getAdminMailAddress()
                val selectorIntent = Intent(Intent.ACTION_SENDTO)
                selectorIntent.data = Uri.parse("mailto:")

                val emailIntent = Intent(Intent.ACTION_SEND)
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Address Change")
                emailIntent.selector = selectorIntent

                startActivity(Intent.createChooser(emailIntent, "Send email..."))
            }
            profileMenuEditAccount.setOnClickListener {
                EditAccountBottomSheet().show(
                    childFragmentManager,
                    Constants.EDIT_ACCOUNT_DIALOG
                )
            }
            profileMenuEditKitchen.setOnClickListener {
                EditKitchenBottomSheet().show(
                    childFragmentManager,
                    Constants.EDIT_KITCHEN_DIALOG
                )
            }
            profileMenuPayment.setOnClickListener {
                PaymentBottomSheet().show(childFragmentManager, Constants.SUPPORT_BOTTOM_SHEET)
            }
//            profileMenuNotifications.setOnClickListener {
//
//            }
            profileMenuPrivacy.setOnClickListener {
                WebDocsDialog.newInstance(Constants.WEB_DOCS_PRIVACY).show(
                    childFragmentManager,
                    Constants.WEB_DOCS_DIALOG
                )
            }
            profileMenuHelp.setOnClickListener {
                SupportBottomSheet().show(childFragmentManager, Constants.SUPPORT_BOTTOM_SHEET)
            }
            profileMenuAbout.setOnClickListener {
                AboutBottomSheet.newInstance()
                    .show(childFragmentManager, Constants.CUSTOM_STRING_CHOOSER_BOTTOM_SHEET)
            }
            profileMenuSignOut.setOnClickListener {
                LogoutDialog(this@AccountFragment).show(
                    childFragmentManager,
                    Constants.LOGOUT_DIALOG_TAG
                )
            }
        }
    }

    private fun initObservers() {
        viewModel.cookData.observe(viewLifecycleOwner, {
            it?.let {
                handleCookData(it)
            }
        })
        viewModel.progressData.observe(viewLifecycleOwner, {
            handleProgressBar(it)
        })
    }

    private fun handleCookData(cook: Cook) {
        with(binding!!) {
            Glide.with(root.context).load(cook.cover?.url).into(profileFragCover)
            Glide.with(root.context).load(cook.thumbnail?.url)
                .apply(RequestOptions.circleCropTransform()).into(profileFragThumbnail)
            profileFragResName.text = cook.getFullName()
            cook.joinDate?.let {
                profileFragJoinedDate.text = "Joined on ${DateUtils.parseDateToMonthAndYear(it)}"
            }
            profileFragDeliveries.text = cook.ordersCount.toString()
            profileFragSatisfaction.text = cook.getRating()
            profileFragReviews.text = cook.reviewCount.toString()
            if (cook.reviewCount > 0) {
                profileFragReviewsHeader.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                profileFragReviewsLayout.setOnClickListener {
                    ReviewsBottomSheet().show(
                        childFragmentManager,
                        Constants.REVIEWS_DIALOG
                    )
                }
            }
        }
    }

    override fun clearClassVariables() {
        binding = null
        buttonsBinding = null
    }

    override fun logout() {
        viewModel.logout(requireActivity())
    }
}