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

//    fun getCountries(): ArrayList<SelectableString> {
//        if (getMetaDataObject()?.countries != null) {
//            return getMetaDataObject()?.countries as ArrayList<SelectableString>
//        }
//        return arrayListOf()
//    }
//
//    fun getCookingMethodsList(): java.util.ArrayList<SelectableString> {
//        if (getMetaDataObject()?.cookingMethods != null) {
//            return getMetaDataObject()?.cookingMethods as ArrayList<SelectableString>
//        }
//        return arrayListOf()
//    }
//
//    fun getPrepTimeList(): ArrayList<PrepTimeRange> {
//        if (getMetaDataObject()?.prepTimeRanges != null) {
//            return getMetaDataObject()?.prepTimeRanges as ArrayList<PrepTimeRange>
//        }
//        return arrayListOf()
//    }
//
//    fun getIngredientsList(): ArrayList<Ingredient> {
//        if (getMetaDataObject()?.ingredients != null) {
//            return getMetaDataObject()?.ingredients!!
//        }
//        return arrayListOf()
//    }
//
//    fun getIngredientsSelectableList(): ArrayList<SelectableString> {
//        if (getMetaDataObject()?.ingredients != null) {
//            return getMetaDataObject()?.ingredients as ArrayList<SelectableString>
//        }
//        return arrayListOf()
//    }


}