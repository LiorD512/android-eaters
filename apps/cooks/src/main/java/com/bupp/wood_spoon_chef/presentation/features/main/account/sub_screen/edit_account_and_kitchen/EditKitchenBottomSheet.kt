package com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.edit_account_and_kitchen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.databinding.BottomSheetEditKitchenBinding
import com.bupp.wood_spoon_chef.presentation.dialogs.MediaChooserDialog
import com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account.sub_screen.SetupVideoFragment
import com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account.sub_screen.country_chooser.CountryChooserBottomSheet
import com.bupp.wood_spoon_chef.data.remote.model.Cook
import com.bupp.wood_spoon_chef.data.remote.model.Country
import com.bupp.wood_spoon_chef.data.remote.model.MediaRequests
import com.bupp.wood_spoon_chef.utils.AnimationUtil
import com.bupp.wood_spoon_chef.utils.DateUtils
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class EditKitchenBottomSheet : TopCorneredBottomSheet(),
    MediaChooserDialog.MediaChooserListener,
    CountryChooserBottomSheet.CountyChooserBottomSheetListener {

    private var binding: BottomSheetEditKitchenBinding? = null
    private val viewModel: UpdateAccountViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_edit_kitchen, container, false)
        binding = BottomSheetEditKitchenBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFullScreenDialog()
        viewModel.initCookData()
        initUi()
        initObservers()
    }


    private fun initUi() {
        with(binding!!) {
            editKitchenFlagLayout.setOnClickListener {
                openSelectedCountryDialog()
            }
            editKitchenVideoEmpty.setOnClickListener { viewModel.onChangeVideoClick() }
            editKitchenVideoAddBtn.setOnClickListener { viewModel.onChangeVideoClick() }
            editKitchenCoverEmpty.setOnClickListener { viewModel.onChangeCoverClick() }
            editKitchenCoverAddBtn.setOnClickListener { viewModel.onChangeCoverClick() }

            editKitchenSaveBtn.setOnClickListener {
                validateAndContinue()
            }
            editKitchenHeader.setOnIconClickListener {
                dismiss()
            }
            editKitchenVideoDelete.setOnClickListener {
                deleteVideo()
            }
        }
    }

    private fun initObservers() {
        viewModel.cookData.observe(viewLifecycleOwner, {
            loadCookData(it)
        })
        viewModel.mediaRequestData.observe(viewLifecycleOwner, {
            loadMediaRequestData(it)
        })
        viewModel.postAccountSuccessEvent.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let {
                dismiss()
            }
        })
        viewModel.progressData.observe(viewLifecycleOwner, {
            handleProgressBar(it)
        })
        viewModel.errorEvent.observe(viewLifecycleOwner, {
            handleErrorEvent(it, binding?.root)
        })
    }

    private fun loadCookData(cook: Cook?) {
        with(binding!!) {
            cook?.let { it ->
                it.about.let {
                    editKitchenAbout.setText(it)
                }
                it.restaurantName.let {
                    editKitchenRestaurantName.setText(it)
                }
                it.countryId?.let { countryId ->
                    viewModel.getCountries().find { it.id == countryId }?.let { country ->
                        selectCountry(country)
                    }
                }
                it.video?.let {
                    editKitchenVideoPreview.visibility = View.VISIBLE
                    editKitchenVideoDelete.visibility = View.VISIBLE
                    Glide.with(requireContext()).load(it)
                        .transform(CenterCrop(), RoundedCorners(14))
                        .into(editKitchenVideoPreview)
                }
                it.cover?.let {
                    editKitchenCoverPreview.visibility = View.VISIBLE
                    Glide.with(requireContext()).load(it.url)
                        .transform(CenterCrop(), RoundedCorners(14))
                        .into(editKitchenCoverPreview)
                }

            }
        }
    }

    private fun loadMediaRequestData(mediaRequests: MediaRequests?) {
        mediaRequests?.let { it ->
            it.tempVideoUri?.let {
                handleVideoUri(it)
            }
            it.tempCover?.let {
                handleCoverUri(it)
            }
        }
    }

    private fun validateAndContinue() {
        if (allFieldsValid()) {
            with(binding!!) {
                val restaurantName = editKitchenRestaurantName.getTextOrNull() ?: ""
                val about = editKitchenAbout.text ?: ""
                viewModel.updateHomeKitchen(restaurantName, about.toString())
            }
        }
    }

    private fun allFieldsValid(): Boolean {
        with(binding!!) {
            //restaurant name validation
            val hasName = editKitchenRestaurantName.checkIfValidAndSHowError()
            // restaurant about validation
            val hasAbout = editKitchenAbout.text.isNotEmpty()
            if (!hasAbout) {
                AnimationUtil().shakeView(editKitchenAbout, requireContext())
            }
            // country validation
            if (!hasCountry) {
                AnimationUtil().shakeView(editKitchenFlagLayout, requireContext())
            }
            val hasCover = viewModel.hasCover()
            return hasName && hasAbout && hasCountry
        }
    }

    private fun openSelectedCountryDialog() {
        val countryChooser = CountryChooserBottomSheet(
            viewModel.getCountries(),
            getString(R.string.country_chooser_title),
            this
        )
        countryChooser.show(childFragmentManager, Constants.CHOOSER_FRAG_TAG)
    }

    override fun onCountrySelected(country: Country) {
        selectCountry(country)
    }

    private var hasCountry = false
    private fun selectCountry(country: Country) {
        with(binding!!) {
            hasCountry = true
            Glide.with(root).load(country.flagUrl).into(editKitchenFlag)
            viewModel.onCountrySelected(country)
        }
    }

    override fun onMediaChoose(mediaType: Int) {
        when (mediaType) {
            Constants.MEDIA_TYPE_VIDEO -> {
                val hasPermission = ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
                if (hasPermission) {
                    openVideoCamera()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
            Constants.MEDIA_TYPE_GALLERY -> {
                openVideoGallery()
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                openVideoCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "In order to capture video you must enable camera permissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val openCameraResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val fileUri: Uri? = intent?.data
                Log.d(SetupVideoFragment.TAG, "openCameraResult: $fileUri")
                fileUri?.let {
                    handleVideoUri(it)
                }
            }
        }

    private fun handleCoverUri(videoUri: Uri) {
        with(binding!!) {
            Glide.with(requireContext()).asBitmap().load(videoUri)
                .transform(CenterCrop(), RoundedCorners(14))
                .into(editKitchenCoverPreview)
            editKitchenCoverAddBtn.text = "Edit cover"
            editKitchenCoverPreview.visibility = View.VISIBLE
            editKitchenCoverEmpty.visibility = View.GONE
        }
    }

    private fun handleVideoUri(videoUri: Uri) {
        Glide.with(this).asBitmap().load(videoUri).transform(CenterCrop(), RoundedCorners(14))
            .into(binding!!.editKitchenVideoPreview)
        binding!!.editKitchenVideoAddBtn.text = "Edit video"
        binding!!.editKitchenVideoPreview.visibility = View.VISIBLE
        binding!!.editKitchenVideoEmpty.visibility = View.GONE

        val videoLength = getVideoLength(videoUri)
        binding!!.editKitchenVideoLength.text = videoLength
        binding!!.editKitchenVideoLength.visibility = View.VISIBLE
        binding!!.editKitchenVideoDelete.visibility = View.VISIBLE
    }


    private fun deleteVideo() {
        viewModel.deleteKitchenVideo()
        binding!!.editKitchenVideoPreview.visibility = View.INVISIBLE
        binding!!.editKitchenVideoEmpty.visibility = View.VISIBLE
        binding!!.editKitchenVideoLength.visibility = View.INVISIBLE
        binding!!.editKitchenVideoDelete.visibility = View.INVISIBLE
    }

    private fun getVideoLength(uri: Uri): String {
        val retriever = MediaMetadataRetriever()
        //use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(requireContext(), uri)
        val time: String = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) ?:""
        val timeInMilliseconds = time.toLong()
        retriever.release()
        return DateUtils.parseMilliToLength(timeInMilliseconds)
    }

    private fun openVideoCamera() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
        openCameraResult.launch(intent)
    }

    private fun openVideoGallery() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        openCameraResult.launch(intent)
    }


    override fun clearClassVariables() {
        binding = null
        viewModel.resetData()
    }

}