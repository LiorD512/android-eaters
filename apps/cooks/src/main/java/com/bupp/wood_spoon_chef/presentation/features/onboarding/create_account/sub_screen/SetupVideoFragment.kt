package com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account.sub_screen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.presentation.features.base.BaseFragment
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.databinding.FragmentSetupVideoBinding
import com.bupp.wood_spoon_chef.presentation.dialogs.MediaChooserDialog
import com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account.CreateAccountViewModel
import com.bupp.wood_spoon_chef.data.remote.model.CookRequest
import com.bupp.wood_spoon_chef.utils.AnimationUtil
import com.bupp.wood_spoon_chef.utils.DateUtils
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SetupVideoFragment : BaseFragment(R.layout.fragment_setup_video),
    MediaChooserDialog.MediaChooserListener {

    var binding: FragmentSetupVideoBinding? = null
    val viewModel by sharedViewModel<CreateAccountViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSetupVideoBinding.bind(view)

        initUi()
        initObservers()

    }

    private fun initUi() {
        with(binding!!) {
            setupVideoEmpty.setOnClickListener { viewModel.onChangeVideoClick() }
            setupVideoAddBtn.setOnClickListener { viewModel.onChangeVideoClick() }
            setupCoverEmpty.setOnClickListener { viewModel.onChangeCoverClick() }
            setupCoverAddBtn.setOnClickListener { viewModel.onChangeCoverClick() }

            setupVideoNext.setOnClickListener {
                if (allFieldsValid()) {
                    viewModel.saveCooksDataAndFinish()
                }
            }
        }
    }

    private fun initObservers() {
        viewModel.currentUserLiveData.observe(viewLifecycleOwner, {
            loadUnSavedData(it)
        })
    }

    private fun loadUnSavedData(cookRequest: CookRequest?) {
        cookRequest?.let { it ->
            it.tempVideoUri?.let {
                handleVideoUri(it)
            }
            it.tempCover?.let {
                handleCoverUri(it)
            }
        }
    }

    private fun allFieldsValid(): Boolean {
        with(binding!!) {
            //restaurant name validation
            val hasCover = viewModel.hasCover()
            if (!hasCover) {
                AnimationUtil().shakeView(coverLayout, requireContext())
            }
            return hasCover
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
                Log.d(TAG, "openCameraResult: $fileUri")
                fileUri?.let {
                    handleVideoUri(it)
                }
            }
        }

    private fun handleCoverUri(videoUri: Uri) {
        with(binding!!) {
            Glide.with(requireContext()).asBitmap().load(videoUri)
                .transform(CenterCrop(), RoundedCorners(14))
                .into(setupCoverPreview)
            setupCoverAddBtn.text = "Edit cover"
            setupCoverPreview.visibility = View.VISIBLE
            setupCoverEmpty.visibility = View.GONE
        }
    }

    private fun handleVideoUri(videoUri: Uri) {
        Glide.with(this).asBitmap().load(videoUri).transform(CenterCrop(), RoundedCorners(14))
            .into(binding!!.setupVideoPreview)
        binding!!.setupVideoAddBtn.text = "Edit video"
        binding!!.setupVideoPreview.visibility = View.VISIBLE
        binding!!.setupVideoEmpty.visibility = View.GONE

        val videoLength = getVideoLength(videoUri)
        binding!!.setupVideoLength.text = videoLength
        binding!!.setupVideoLength.visibility = View.VISIBLE
    }

    private fun getVideoLength(uri: Uri): String {
        val retriever = MediaMetadataRetriever()
        //use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(requireContext(), uri)
        val time: String = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) ?: ""
        val timeInMillisecond = time.toLong()
        retriever.release()
        return DateUtils.parseMilliToLength(timeInMillisecond)
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
    }

    companion object {
        const val TAG = "wowSetupVid"
    }

}