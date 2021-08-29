//import com.bupp.wood_spoon_eaters.common.MTLogger
//import com.bupp.wood_spoon_eaters.model.DishSection
//import com.bupp.wood_spoon_eaters.model.FeedRestaurantItemTypeDish
//import com.bupp.wood_spoon_eaters.model.Pagination
//import com.squareup.moshi.JsonAdapter
//import com.squareup.moshi.JsonReader
//import com.squareup.moshi.JsonWriter
//
//class MoshiNullableSectionAdapter: JsonAdapter<Any?>() {
//    override fun fromJson(reader: JsonReader): Any? {
//        MTLogger.c("networkModule","fromJson: $reader")
//        try {
//            var resourceVal: String? = "Dish"
//            reader.beginObject()
//            while (reader.hasNext()) {
//                if(reader.peek() == JsonReader.Token.NULL){
//                    reader.skipValue()
//                }
//                when (reader.nextName()) {
//                    "type" -> resourceVal = "dish"
//                    null -> resourceVal = "dish"
//                    else -> reader.skipValue()
//                }
//            }
//            reader.endObject()
//            return FeedRestaurantItemTypeDish(null)
//        }catch (ex: Exception){
//            MTLogger.c("networkModule","ex: ${ex.message}")
//        }
//        MTLogger.c("networkModule","nullSafe")
//        return null
//    }
//
//    override fun toJson(writer: JsonWriter, value: Any?) {
////        MTLogger.c("networkModule","toJson: $writer, value: $value")
//    }
//
//
//}