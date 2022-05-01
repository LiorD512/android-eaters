package com.bupp.wood_spoon_chef.presentation.features.new_dish

import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.databinding.ActivityNewDishBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseActivity
import com.bupp.wood_spoon_chef.presentation.features.main.my_dishes.dialogs.DishUpdatedDialog
import com.bupp.wood_spoon_chef.presentation.features.new_dish.bottom_sheets.DiscardDishBottomSheet
import com.bupp.wood_spoon_chef.presentation.features.new_dish.bottom_sheets.SaveAsDraftBottomSheet
import com.bupp.wood_spoon_chef.presentation.features.new_dish.dialogs.NewDishDialog
import com.bupp.wood_spoon_chef.data.remote.network.base.CustomError
import com.bupp.wood_spoon_chef.utils.media_utils.MediaUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewDishActivity : BaseActivity(), SaveAsDraftBottomSheet.SaveAsDraftListener,
    DiscardDishBottomSheet.DiscardDishListener, DishUpdatedDialog.DishUpdateListener,
    MediaUtils.MediaUtilListener {

    val viewModel by viewModel<NewDishViewModel>()
    var binding: ActivityNewDishBinding? = null

    private val mediaUtil = MediaUtils(this, this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewDishBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        viewModel.checkDishStatus(intent)
        initUi()
        initObservers()

    }

    private fun initUi() {
        with(binding!!) {
            newDishActFeaturePb.nextStep()

            newDishActDraft.setOnClickListener {
                SaveAsDraftBottomSheet(this@NewDishActivity).show(
                    supportFragmentManager,
                    Constants.SAVE_AS_DRAFT_BOTTOM_SHEET
                )
            }
            newDishActClose.setOnClickListener {
                DiscardDishBottomSheet(this@NewDishActivity).show(
                    supportFragmentManager,
                    Constants.SAVE_AS_DRAFT_BOTTOM_SHEET
                )
            }
        }
    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(this, {
            when (it) {
                NewDishViewModel.NavigationEventType.NAME_TO_DETAILS -> {
                    binding!!.newDishActFeaturePb.nextStep()
                    binding!!.newDishActDraft.visibility = View.VISIBLE
                    findNavController(R.id.newDishActContainer).navigate(R.id.action_newDishName_to_newDishDetails)
                }
                NewDishViewModel.NavigationEventType.DETAILS_TO_INSTRUCTIONS -> {
                    binding!!.newDishActFeaturePb.nextStep()
                    findNavController(R.id.newDishActContainer).navigate(R.id.action_newDishDetails_to_newDishInstructions)
                }
                NewDishViewModel.NavigationEventType.INSTRUCTIONS_TO_DIETARY -> {
                    binding!!.newDishActFeaturePb.nextStep()
                    findNavController(R.id.newDishActContainer).navigate(R.id.action_newDishInstructions_to_newDishDietary)
                }
                NewDishViewModel.NavigationEventType.DIETARY_TO_PRICE -> {
                    binding!!.newDishActFeaturePb.nextStep()
                    findNavController(R.id.newDishActContainer).navigate(R.id.action_newDishDietary_to_newDishPrice)
                }
                NewDishViewModel.NavigationEventType.PRICE_TO_MEDIA -> {
                    binding!!.newDishActFeaturePb.nextStep()
                    findNavController(R.id.newDishActContainer).navigate(R.id.action_newDishPrice_to_newDishMedia)
                }
                else -> {}
            }
        })
        viewModel.newDishDoneDialogEvent.observe(this, {
            it?.let {
                val dialog = DishUpdatedDialog.newInstance(it.title, it.body)
                dialog.show(supportFragmentManager, Constants.NEW_DISH_DONE_DIALOG)
            }
        })
        viewModel.dishStatusEvent.observe(this, {
            if (it.isEdit) {
                if (!it.isDraft) {
                    binding!!.newDishHeaderLayout.removeView(binding!!.newDishActDraft)
                }
            } else {
                NewDishDialog().show(supportFragmentManager, Constants.NEW_DISH_DIALOG)
            }
        })
        viewModel.progressData.observe(this, {
            handleProgressBar(it)
        })
        viewModel.errorEvent.observe(this, {
            handleErrorEvent(it, binding?.root)
        })
        viewModel.navigationEvent.observe(this, {
            when (it) {
                NewDishViewModel.NavigationEventType.OPEN_VIDEO_CHOOSER_DIALOG -> {
                    mediaUtil.startVideoFetcher()
                }
                NewDishViewModel.NavigationEventType.OPEN_CAMERA_UTIL -> {
                    mediaUtil.startPhotoFetcher()
                }
                else -> {}
            }
        })
    }

    override fun onBackPressed() {
        binding!!.newDishActFeaturePb.previousStep()
        super.onBackPressed()
    }

    override fun fileToBigError() {
        val error = CustomError("Sorry, your file is too large to upload. It needs to be 100 MB or smaller in size")
        handleErrorEvent(error, binding?.root)
    }

    companion object {
        const val EDIT_DISH_PARAM = "dishId"
    }

    override fun onSaveAsDraftClicked() {
        viewModel.saveCurrentDraft()
    }

    override fun onDiscardDishClicked() {
        finish()
    }

    override fun onDishUpdateDialogDismiss() {
        setResult(RESULT_OK)
        finish()
    }

    override fun onMediaUtilResult(result: MediaUtils.MediaUtilResult) {
        when (result.type) {
            MediaUtils.MediaUtilsType.MEDIA_UTILS_PHOTO -> {
                viewModel.onCameraUtilResult(result)
            }
            MediaUtils.MediaUtilsType.MEDIA_UTILS_VIDEO -> {
                result.fileUri?.let { viewModel.onVideoCaptureDone(it) }
            }
        }
    }
}