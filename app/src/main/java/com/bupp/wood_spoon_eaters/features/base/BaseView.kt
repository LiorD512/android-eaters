package com.bupp.wood_spoon_eaters.features.base

interface BaseView<out T : BasePresenter<*>> {

    val presenter: T

}
