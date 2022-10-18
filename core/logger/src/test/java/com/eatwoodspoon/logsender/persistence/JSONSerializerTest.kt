package com.eatwoodspoon.logsender.persistence

import org.json.JSONObject
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

class JSONSerializerTest {

    private lateinit var serializer: JSONSerializer

    @Before
    fun setUp() {
        serializer = JSONSerializer()
    }

    @Test
    fun `ensure result is valid JSON`() {
        JSONObject(
            serializer.serializeLog(logItem =
            mapOf(
                "value1" to "value1",
                "value2" to 12345,
                "value3" to 123.45,
                "value4" to true,
                "value6" to listOf("value6", 12345, 123.45, true, null),
                "value7" to mapOf(
                    "value7" to "value7",
                    "value8" to 12345,
                    "value9" to 123.45,
                    "value10" to true,
                    "value11" to null
                ),
                "value12" to object : Any() {
                    override fun toString(): String {
                        return "value12"
                    }
                }
            )
            )
        )
    }

    @Test
    fun `ensure result is always one-liner`() {
        val json = serializer.serializeLog(logItem = mapOf(
            "value1" to "value1\nvalue2",
            "value3" to "value1\n\rvalue2",
            "value12" to object : Any() {
                override fun toString(): String {
                    return "value12\n\t\nvalue13"
                }
            }
        )
        )
        JSONObject(json)
        assertEquals(json.lines().size, 1)
    }

    @Test
    fun `ensure emoji is valid JSON`() {
        JSONObject(
            serializer.serializeLog(
                logItem =
                mapOf(
                    "value1" to "value1\uD83E\uDEE3\uD83D\uDE00\uD83D\uDE2D\uD83D\uDE00\uD83E\uDDD0\uD83D\uDC4B"
                )
            )
        )
    }
}