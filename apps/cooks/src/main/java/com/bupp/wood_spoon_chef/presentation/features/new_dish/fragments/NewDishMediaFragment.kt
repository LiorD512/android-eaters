package com.bupp.wood_spoon_chef.presentation.features.new_dish.fragments

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.presentation.custom_views.adapters.HorizontalPaddingItemDecoration
import com.bupp.wood_spoon_chef.databinding.FragmentNewDishMediaBinding
import com.bupp.wood_spoon_chef.presentation.dialogs.VideoViewDialog
import com.bupp.wood_spoon_chef.presentation.features.base.BaseFragment
import com.bupp.wood_spoon_chef.presentation.features.new_dish.NewDishViewModel
import com.bupp.wood_spoon_chef.presentation.features.new_dish.fragments.adapters.NewDishMedia
import com.bupp.wood_spoon_chef.presentation.features.new_dish.fragments.adapters.NewDishMediaAdapter
import com.bupp.wood_spoon_chef.presentation.features.new_dish.fragments.adapters.NewDishMediaAdapter.Companion.VIEW_TYPE_PHOTO
import com.bupp.wood_spoon_chef.presentation.features.new_dish.fragments.adapters.NewDishMediaAdapter.Companion.VIEW_TYPE_VIDEO
import com.bupp.wood_spoon_chef.data.remote.model.DishRequest
import com.bupp.wood_spoon_chef.utils.AnimationUtil
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class NewDishMediaFragment : BaseFragment(R.layout.fragment_new_dish_media), NewDishMediaAdapter.NewDishMediaAdapterListener {

    private var binding: FragmentNewDishMediaBinding ?= null
    val viewModel by sharedViewModel<NewDishViewModel>()
    lateinit var adapter: NewDishMediaAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentNewDishMediaBinding.bind(view)

        initUi()
        initObservers()
    }

    private fun initUi() {
        binding?.apply{
            adapter = NewDishMediaAdapter(requireContext(), this@NewDishMediaFragment)
            newDishMediaList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            newDishMediaList.addItemDecoration(HorizontalPaddingItemDecoration())
            val pagerSnapHelper = PagerSnapHelper()
            pagerSnapHelper.attachToRecyclerView(newDishMediaList)
            newDishMediaList.adapter = adapter

            newDishMainPhoto.setOnClickListener { viewModel.onAddNewMediaClick(NewDishViewModel.MediaUploadArgs.IMG_1) }
            newDishMainPhotoBtn.setOnClickListener { viewModel.onAddNewMediaClick(NewDishViewModel.MediaUploadArgs.IMG_1) }

            newDishMediaPublish.setOnClickListener {
                if (allFieldsValid()) {
                    viewModel.startDishPublishing()
                }
            }

            newDishMediaBack.setOnClickListener {
                activity?.onBackPressed()
            }

            val emptyMediaList = mutableListOf(NewDishMedia(VIEW_TYPE_PHOTO, null), NewDishMedia(VIEW_TYPE_PHOTO, null), NewDishMedia(VIEW_TYPE_VIDEO, null))
            adapter.submitList(emptyMediaList)
        }
    }

    private fun allFieldsValid(): Boolean {
        binding?.apply{
            val mainPhotoValid = viewModel.hasMainPhoto()
            if(!mainPhotoValid){
                AnimationUtil().shakeView(newDishMainPhotoIcon, requireContext())
            }
            return mainPhotoValid
        }
        return false
    }

    private fun initObservers() {
        viewModel.curDishLiveData.observe(viewLifecycleOwner, {
            loadUnSavedData(it)
        })
        viewModel.saveDraftEvent.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let{
                viewModel.startDishPublishing(true)
            }
        })
        viewModel.dishStatusEvent.observe(viewLifecycleOwner, {
            if(it.isEdit){
                binding?.newDishMediaPublish?.setText("Update changes")
            }
        })
    }

    private fun loadUnSavedData(dishRequest: DishRequest?) {
        binding?.apply {
            dishRequest?.let { request ->
                request.tempThumbnail?.let {
                    Glide.with(requireContext()).load(it)
                        .transform(CenterCrop(), RoundedCorners(26))
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(newDishMainPhoto)
                    newDishMainPhotoBtn.text = "Edit main photo"
                }
                val mediaList = mutableListOf(
                    NewDishMedia(VIEW_TYPE_PHOTO, null),
                    NewDishMedia(VIEW_TYPE_PHOTO, null),
                    NewDishMedia(VIEW_TYPE_VIDEO, null)
                )

                request.imageGallery?.let { it ->
                    it.forEachIndexed { index, uri ->
                        uri?.let {
                            when (index) {
                                0 -> {
                                    Glide.with(requireContext()).load(it)
                                        .transform(CenterCrop(), RoundedCorners(26))
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .into(newDishMainPhoto)
                                    newDishMainPhotoBtn.text = "Edit main photo"
                                }
                                else -> {
                                    request.tempImageGallery.add(Uri.parse(uri))
                                }
                            }
                        }
                    }
                }
                if (request.tempVideo != null) {
                    mediaList.last().uri = request.tempVideo
                } else {
                    mediaList.last().uri = null

                    if (request.video != null) {
                        mediaList.last().uri = Uri.parse(request.video)
                    } else {
                        mediaList.last().uri = null
                    }
                }

                request.tempImageGallery.let { it ->
                    it.forEachIndexed { index, uri ->
                        uri?.let {
                            request.imageGallery?.remove(uri.toString())
                            mediaList[index].uri = it
                        }
                    }
                }

                adapter.submitList(mediaList)

            }
        }
    }

    override fun onAddMediaClick(type: NewDishViewModel.MediaUploadArgs) {
        viewModel.onAddNewMediaClick(type)
    }

    override fun onPlayMediaClick(uri: Uri?) {
        uri?.let{
            VideoViewDialog(it.toString(), "New dish video").show(childFragmentManager, Constants.VIDEO_PLAYER_DIALOG)
        }
    }

    override fun onVideoDelete() {
        viewModel.onVideoDelete()
    }

    override fun onMediaDelete(uri: Uri?) {
        viewModel.onMediaDelete(uri)
    }

    override fun clearClassVariables() {
        binding = null
    }

    companion object{
        const val TAG = "wowNewDishMedia"
    }

}