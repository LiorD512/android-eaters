package com.bupp.wood_spoon_chef.di.abs

import android.os.Build
import androidx.annotation.RequiresApi
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class SerializeNulls {
    companion object {
        var JSON_ADAPTER_FACTORY: JsonAdapter.Factory = object : JsonAdapter.Factory {

            @RequiresApi(api = Build.VERSION_CODES.P)
            override fun create(type: Type, annotations: Set<Annotation?>, moshi: Moshi): JsonAdapter<*>? {
                val nextAnnotations = Types.nextAnnotations(annotations, SerializeNulls::class.java)

                return if (nextAnnotations == null) {
                    null
                } else {
                    moshi.nextAdapter<Any>(this, type, nextAnnotations).serializeNulls()
                }
            }
        }
    }
}