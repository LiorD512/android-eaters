package com.bupp.wood_spoon_eaters.features.locations_and_address

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FragmentLocationPermissionBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LocationPermissionFragment : BottomSheetDialogFragment() {

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        Log.d(TAG,"requestPermissionLauncher: $it")
        askLocationPermission()
    }

    private var binding: FragmentLocationPermissionBinding? = null
    private val mainViewModel by sharedViewModel<LocationAndAddressViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FloatingBottomSheetStyle)
    }

    private lateinit var behavior: BottomSheetBehavior<View>
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(sheet!!)
            behavior.isFitToContents = true
            behavior.isDraggable = true
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLocationPermissionBinding.bind(view)
        initUi()
    }

    private fun initUi() {
        binding!!.locationPermissionFragAllow.setOnClickListener {
            mainViewModel.locationPermissionEvent(true)
//            mainViewModel.onLocationPermissionDone()
            askLocationPermission()
        }

        binding!!.locationPermissionFragReject.setOnClickListener {
            mainViewModel.locationPermissionEvent(false)
//            mainViewModel.onLocationPermissionDone()
            dismiss()
        }
    }

    private fun askLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                Log.d(LocationAndAddressActivity.TAG,"location grated")
                mainViewModel.onLocationPermissionDone()
                dismiss()
            }
            shouldShowRequestPermissionRationale() -> {
                Log.d(LocationAndAddressActivity.TAG,"shouldShowRequestPermissionRationale")
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected.
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            }
            else -> {
                Log.d(LocationAndAddressActivity.TAG,"asking for permission")
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            }
        }
    }

    private fun shouldShowRequestPermissionRationale() =
        ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) && ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    companion object{
        const val TAG = "wowLocationPermission"
    }


}