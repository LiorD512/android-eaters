package com.bupp.wood_spoon_eaters.bottom_sheets.fees_and_tax_bottom_sheet

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.model.ReportTopic
import com.bupp.wood_spoon_eaters.model.Reports
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeeAndTaxViewModel(private val metaDataRepository: MetaDataRepository) : ViewModel() {

    fun getGlobalMinimumFee(): String{
        return metaDataRepository.getMinOrderFeeStr(false)
    }


}
