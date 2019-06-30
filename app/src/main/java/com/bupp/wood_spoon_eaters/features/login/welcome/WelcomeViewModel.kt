package com.bupp.wood_spoon_eaters.features.login.welcome

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.network.ApiService


class WelcomeViewModel(val api: ApiService) : ViewModel() {

    val navigationEvent: SingleLiveEvent<NavigationEvent> = SingleLiveEvent()

    data class NavigationEvent(val isLoginSuccess: Boolean = false)

    fun onLoginSuccess() {
        navigationEvent.postValue(NavigationEvent(true))
    }

    fun getSettings() {
//        val requests = ArrayList<Observable<*>>()
//        requests.add(api.getSettings())
//        Observable.zip(requests) { objects ->
//            Log.d("wowSplash", "all server call success")
//            //parsing settings:
//            val settings = objects[0] as List<Settings>
//            Log.d("wow", "Settings get success !" + settings.size);
//            onLoginSuccess()
//            Any()
//        }.timeout(55000, TimeUnit.MILLISECONDS)
//                .subscribeOn(Schedulers.io())
//                .subscribe({
//                    Log.d("wowSplash", "accept 1")
//                },
//                        {
//                            //Do something on error completion of requests
//                            Log.d("wowSplash", "server timeout")
//                        }
//                )
    }


}