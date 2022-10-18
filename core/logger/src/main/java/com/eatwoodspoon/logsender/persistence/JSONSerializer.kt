package com.eatwoodspoon.logsender.persistence

import com.eatwoodspoon.logsender.Serializer
import org.json.JSONObject

internal class JSONSerializer : Serializer<String> {

    override fun serializeLog(logItem: Map<String, Any>): String {

        return JSONObject(
            logItem.mapValues {
                JSONObject.wrap(it.value) ?: it.value.toString()
            }
        ).toString()
    }
}
