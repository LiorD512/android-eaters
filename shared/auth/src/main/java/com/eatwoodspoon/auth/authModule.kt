package com.eatwoodspoon.auth

import org.koin.dsl.module

val authModule = module {

    single { WoodSpoonAuth(get(), get(), get()) }
}