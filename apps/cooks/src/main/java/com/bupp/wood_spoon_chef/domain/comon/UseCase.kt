package com.bupp.wood_spoon_chef.domain.comon

interface UseCase<out T, in P> {

    fun execute(params: P): T
}