package com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.order_packaging

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.databinding.BottomSheetOrderFoodPackagingBinding


class OrderFoodPackagingBottomSheet : TopCorneredBottomSheet() {

    private var binding: BottomSheetOrderFoodPackagingBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_order_food_packaging, container, false)
        binding = BottomSheetOrderFoodPackagingBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFullScreenDialog()

        initUi()
    }

    private fun initUi() {
        binding?.apply {
            orderFoodPackagingBsOrderNowBtn.setOnClickListener {
                openChromeTabOrWebView()
            }
        }
    }

    private fun openChromeTabOrWebView() {
        val woodspoonCatalogUri = "https://woodspoon.four51storefront.com/store/catalog"
        val chromePackageName = "com.android.chrome"
        if (isPackageInstalled(chromePackageName)) {
            val chromeTabBuilder = CustomTabsIntent.Builder()
                .setShowTitle(false)
                .setDefaultColorSchemeParams(
                    CustomTabColorSchemeParams.Builder().setToolbarColor(
                        requireContext().getColor(R.color.orangeish)).build()
                )
                .setInstantAppsEnabled(false)
                .setUrlBarHidingEnabled(true)
            val chromeTab = chromeTabBuilder.build()
            chromeTab.launchUrl(requireContext(), Uri.parse(woodspoonCatalogUri))
        } else {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(woodspoonCatalogUri))
            startActivity(intent)
        }
    }

    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            requireContext().packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    override fun clearClassVariables() {}
}