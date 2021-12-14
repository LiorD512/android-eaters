package com.bupp.wood_spoon_eaters.network

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class VERSION(val version: String = "v3")