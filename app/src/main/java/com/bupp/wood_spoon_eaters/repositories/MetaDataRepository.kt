package com.bupp.wood_spoon_eaters.repositories

import android.util.Log
import com.bupp.wood_spoon_eaters.BuildConfig
import com.bupp.wood_spoon_eaters.bottom_sheets.reviews.Metrics
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.base_repos.MetaDataRepositoryImpl
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class MetaDataRepository(private val apiService: MetaDataRepositoryImpl) {

    private var metaDataObject: MetaDataModel = MetaDataModel(null, null)

    enum class MetaDataRepoStatus{
        SUCCESS,
        FAILED,
    }
    data class MetaDataRepoResult(val status: MetaDataRepoStatus)

    suspend fun initMetaData(): MetaDataRepoResult {
        val result = withContext(Dispatchers.IO){
            apiService.getMetaData()
        }
        result.let{
            when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG,"initMetaData - NetworkError")
                    return MetaDataRepoResult(MetaDataRepoStatus.FAILED)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG,"initMetaData - GenericError")
                    return MetaDataRepoResult(MetaDataRepoStatus.FAILED)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG,"initMetaData - Success")
                    val metaData = result.value.data?.copy()
                    metaData?.let{
                        this.metaDataObject = it
                    }
                    return MetaDataRepoResult(MetaDataRepoStatus.SUCCESS)
                }
                is ResultHandler.WSCustomError -> {
                    return MetaDataRepoResult(MetaDataRepoStatus.FAILED)
                }
            }
        }

    }

    private fun getMetaDataObject(): MetaDataModel {
        return this.metaDataObject
    }

    fun getCuisineListSelectableIcons(): List<SelectableIcon>{
        if(getMetaDataObject().cuisines != null) {
            return metaDataObject.cuisines as List<SelectableIcon>
        }
        return listOf()
    }

    fun getDietaryList(): List<SelectableIcon> {
        if (getMetaDataObject().diets != null) {
            return metaDataObject.diets as List<SelectableIcon>
        }
        return listOf()
    }

    fun getReportTopics(): List<ReportTopic> {
        if (getMetaDataObject().reportTopic != null) {
            return metaDataObject.reportTopic as List<ReportTopic>
        }
        return listOf()
    }

    fun getNotificationsGroup(): List<NotificationGroup> {
        if (getMetaDataObject().notificationsGroup != null) {
            return metaDataObject.notificationsGroup as List<NotificationGroup>
        }
        return listOf()
    }

    fun getSearchTags(): List<String> {
        if (getMetaDataObject().searchTags != null) {
            return metaDataObject.searchTags as List<String>
        }
        return listOf()
    }


    companion object{
        const val TAG = "wowMetaDataRepo"
    }


}