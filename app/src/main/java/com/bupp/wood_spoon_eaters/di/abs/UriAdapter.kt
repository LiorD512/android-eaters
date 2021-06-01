package com.bupp.wood_spoon_eaters.di.abs

import android.net.Uri
import com.squareup.moshi.*

class UriAdapter : JsonAdapter<Uri>() {

  companion object {
    val FACTORY = Factory { type, _, _ ->
      return@Factory if (type === Uri::class.java) {
        UriAdapter()
      } else {
        null
      }
    }
  }

  @FromJson
  override fun fromJson(reader: JsonReader): Uri {
    return Uri.parse(reader.nextString())
  }

  @ToJson
  override fun toJson(
    writer: JsonWriter,
    value: Uri?
  ) {
    writer.value(value?.toString())
  }

  override fun toString(): String {
    return "JsonAdapter(Uri)"
  }
}