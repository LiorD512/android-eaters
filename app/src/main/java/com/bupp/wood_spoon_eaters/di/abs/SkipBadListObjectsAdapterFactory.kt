//package com.bupp.wood_spoon_eaters.di.abs
//
//import com.bupp.wood_spoon_eaters.model.FeedSection
//import com.squareup.moshi.*
//import java.lang.reflect.Type
//
//class SkipBadListObjectsAdapterFactory : JsonAdapter.Factory {
//    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
//        return if (annotations.isEmpty() && Types.getRawType(type) == FeedSection::class.java) {
//            val elementType = Types.collectionElementType(type, FeedSection::class.java)
//            val elementAdapter = moshi.adapter<Any>(elementType)
//
//            SkipBadListObjectsAdapter(elementAdapter)
//        } else {
//            null
//        }
//    }
//
//    private class SkipBadListObjectsAdapter<T : Any>(private val elementAdapter: JsonAdapter<T>) :
//        JsonAdapter<List<T>>() {
//        override fun fromJson(reader: JsonReader): List<T>? {
//            val goodObjectsList = mutableListOf<T>()
//
//            reader.beginArray()
//
//            while (reader.hasNext()) {
//                try {
//                    elementAdapter.fromJson(reader)?.let(goodObjectsList::add)
//                } catch (e: JsonDataException) {
//                    // Skip bad element ;)
//                }
//            }
//
//            reader.endArray()
//
//            return goodObjectsList
//
//        }
//
//        override fun toJson(writer: JsonWriter, value: List<T>?) {
//            throw UnsupportedOperationException("SkipBadListObjectsAdapter is only used to deserialize objects")
//        }
//    }
//}