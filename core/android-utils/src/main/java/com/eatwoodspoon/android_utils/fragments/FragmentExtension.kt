package com.eatwoodspoon.android_utils.fragments

import androidx.fragment.app.Fragment
import kotlin.reflect.KClass

@Suppress("ReturnCount", "UNCHECKED_CAST")
fun <T> Fragment.findParent(clazz: Class<T>): T? {
    if (clazz.isInstance(activity)) {
        return activity as T
    }
    var nextParent = parentFragment
    do {
        if (clazz.isInstance(nextParent)) {
            return nextParent as T
        }
        nextParent = nextParent?.parentFragment
    } while (nextParent != null)
    return null
}

fun <T> Fragment.requireParent(clazz: Class<T>): T = findParent(clazz)
    ?: throw ParentNotFoundException(clazz)

class ParentNotFoundException(message: String) : RuntimeException(message) {
    constructor(parentClass: Class<*>) : this("Parent of type `${parentClass.simpleName}` not found")
    constructor(parentClass: KClass<*>) : this("Parent of type `${parentClass.simpleName}` not found")
}