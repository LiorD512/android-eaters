package com.bupp.wood_spoon_chef.di.abs

import android.net.Uri
import com.squareup.moshi.*

class UriAdapter : JsonAdapter<Uri>() {

  companion object;

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