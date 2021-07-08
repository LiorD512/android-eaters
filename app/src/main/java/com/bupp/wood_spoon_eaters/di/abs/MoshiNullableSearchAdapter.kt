import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.model.DishSection
import com.bupp.wood_spoon_eaters.model.Pagination
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

class MoshiNullableSearchAdapter: JsonAdapter<Any?>() {
    override fun fromJson(reader: JsonReader): Any? {
//        MTLogger.c("networkModule","fromJson: $reader")
        try {
            var resourceVal: String? = "Dish"
            reader.beginObject()
            while (reader.hasNext()) {
                if(reader.peek() == JsonReader.Token.NULL){
                    reader.skipValue()
                }
                when (reader.nextName()) {
                    "resource" -> resourceVal = "Dish"
                    null -> resourceVal = "Dish2"
                    else -> reader.skipValue()
                }
            }
            reader.endObject()
            return DishSection(null, null, Pagination())
        }catch (ex: Exception){
            MTLogger.c("networkModule","ex: ${ex.message}")
        }
        MTLogger.c("networkModule","nullSafe")
        return null
    }

    override fun toJson(writer: JsonWriter, value: Any?) {
//        MTLogger.c("networkModule","toJson: $writer, value: $value")
    }


}