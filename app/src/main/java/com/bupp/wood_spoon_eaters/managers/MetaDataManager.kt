package com.bupp.wood_spoon_eaters.managers

import com.bupp.wood_spoon_eaters.model.*

//import com.google.gson.JsonObject

class MetaDataManager {

    private var metaDataObject: MetaDataModel = MetaDataModel(null, null)

    fun setMetaDataObject(metaDataObject: MetaDataModel) {
        this.metaDataObject = metaDataObject
    }

    fun getMetaDataObject(): MetaDataModel? {
        return this.metaDataObject
    }

    fun getCuisineList(): ArrayList<CuisineLabel> {
        if (getMetaDataObject()?.cuisines != null) {
            return metaDataObject?.cuisines as ArrayList<CuisineLabel>
        }
        return arrayListOf()
    }
//
    fun getDietaryList(): ArrayList<SelectableIcon> {
        if (getMetaDataObject()?.diets != null) {
            return metaDataObject?.diets as ArrayList<SelectableIcon>
        }
        return arrayListOf()
    }

    fun getMetricsList(): ArrayList<Metrics> {
        if (getMetaDataObject()?.metrics != null) {
            return metaDataObject.metrics as ArrayList<Metrics>
        }
        return arrayListOf()
    }

    fun getReportTopics(): ArrayList<ReportTopic> {
        if (getMetaDataObject()?.reportTopic != null) {
            return metaDataObject.reportTopic as ArrayList<ReportTopic>
        }
        return arrayListOf()
    }



}