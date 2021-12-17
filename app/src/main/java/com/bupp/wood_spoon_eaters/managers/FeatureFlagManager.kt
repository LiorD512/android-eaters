package com.bupp.wood_spoon_eaters.managers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import com.facebook.FacebookSdk.getApplicationContext
import com.squareup.moshi.Json
import io.split.android.client.SplitClient

import io.split.android.client.SplitFactoryBuilder

import io.split.android.client.SplitFactory

import io.split.android.client.SplitClientConfig
import io.split.android.client.api.Key
import io.split.android.client.events.SplitEvent
import io.split.android.client.events.SplitEventTask


class FeatureFlagManager(userRepository: UserRepository) {

    var apikey = "ch7l69h7l4549d3kvm4dnaqtdvtbvsbo79rb"

    var config = SplitClientConfig.builder().build()

    var matchingKey = "Eater-${userRepository.getUser()?.id}"
    var k: Key = Key(matchingKey)

    var splitFactory = SplitFactoryBuilder.build(apikey, k, config, getApplicationContext())
    var client = splitFactory.client()


//    data class FeatureFlag(val featureFlagType: FeatureFlagType, val isOn: Boolean)
    val featureFlagData = MutableLiveData<SplitClient?>()
    fun getFeatureFlags() = featureFlagData
    fun fetchSplitData(){
        val isReady = client.isReady
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

        client.on(SplitEvent.SDK_READY_TIMED_OUT, object: SplitEventTask() {
            override fun onPostExecution(client: SplitClient?) {
                super.onPostExecution(client)
            }

            override fun onPostExecutionView(client: SplitClient?) {
                super.onPostExecutionView(client)
            }
        })

        client.on(SplitEvent.SDK_READY_FROM_CACHE, object : SplitEventTask() {
            override fun onPostExecution(client: SplitClient) {
                Log.d("wow","onPostExecution")
                featureFlagData.postValue(client)
            }

            override fun onPostExecutionView(client: SplitClient) {
                //UI Code in Here
                val treatment = client.getTreatment("SPLIT_NAME")
                if (treatment == "on") {
                    // insert code here to show on treatment
                } else if (treatment == "off") {
                    // insert code here to show off treatment
                } else {
                    // insert your control treatment code here
                }
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