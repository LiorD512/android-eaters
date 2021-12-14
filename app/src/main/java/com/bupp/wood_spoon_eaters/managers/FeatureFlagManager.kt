package com.bupp.wood_spoon_eaters.managers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.facebook.FacebookSdk.getApplicationContext
import com.squareup.moshi.Json
import io.split.android.client.SplitClient

import io.split.android.client.SplitFactoryBuilder

import io.split.android.client.SplitFactory

import io.split.android.client.SplitClientConfig
import io.split.android.client.api.Key
import io.split.android.client.events.SplitEvent
import io.split.android.client.events.SplitEventTask


class FeatureFlagManager {

    var apikey = "c4gj4v8i6uh178hpjjajq231nmvhucma6f2h"

    var config = SplitClientConfig.builder().build()

    var matchingKey = "key"
    var k: Key = Key(matchingKey)

    var splitFactory = SplitFactoryBuilder.build(apikey, k, config, getApplicationContext())
    var client = splitFactory.client()

    init {
        fetchSplitData()
    }




//    data class FeatureFlag(val featureFlagType: FeatureFlagType, val isOn: Boolean)
    val featureFlagData = MutableLiveData<SplitClient?>()
    fun getFeatureFlags() = featureFlagData
    private fun fetchSplitData(){
        client.on(SplitEvent.SDK_READY, object: SplitEventTask(){
            override fun onPostExecutionView(client: SplitClient?) {
                featureFlagData.postValue(client)
//                handleSplitData(client)
            }

            override fun onPostExecution(client: SplitClient?) {
                super.onPostExecution(client)
                Log.d("wow","onPostExecution")
            }
        })
    }

//    private fun handleSplitData(client: SplitClient?) {
//        val featureFlags = mutableListOf<FeatureFlag>()
//        client?.let{
//            val restaurantSearch = client.getTreatment(FeatureFlagType.SEARCH_IN_RESTAURANT.name)
//            featureFlags.add(FeatureFlag(FeatureFlagType.SEARCH_IN_RESTAURANT, restaurantSearch == "on"))
//        }
////        featureFlagData.postValue(featureFlags)
//    }
}