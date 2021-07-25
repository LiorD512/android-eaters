package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page

import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.Restaurant
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types.newParameterizedType
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.sql.Types
import java.util.*

val moshi = Moshi.Builder()
    .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
    .add(KotlinJsonAdapterFactory())
    .build()

fun getRestaurantData(): Restaurant? {
    val jsonAdapter = moshi.adapter(Restaurant::class.java);
    return jsonAdapter.fromJson(json)
}


private const val json: String =
   "{\n" +
           "  \"id\": 1,\n" +
           "  \"first_name\": \"Assaf\",\n" +
           "  \"last_name\": \"Beer\",\n" +
           "  \"cover\": \"https://res.cloudinary.com/woodspoon/image/upload/t_small/uploads/dishes/6ac2cd78_7d07_4f6c_b4f3_7a34ec91a132_.jpg\",\n" +
           "  \"thumbnail\": \"https://res.cloudinary.com/woodspoon/image/upload/t_small/uploads/dishes/6ac2cd78_7d07_4f6c_b4f3_7a34ec91a132_.jpg\",\n" +
           "  \"avg_rating\": 4.5,\n" +
           "  \"video\": \"https://vod-progressive.akamaized.net/exp=1627058953~acl=%2Fvimeo-prod-skyfire-std-us%2F01%2F4364%2F14%2F371823632%2F1544203958.mp4~hmac=6d20fc9e581cb7a6082225405e8d0402991001665e4d6449b22dc41f4b65bb7a/vimeo-prod-skyfire-std-us/01/4364/14/371823632/1544203958.mp4?filename=video.mp4\",\n" +
           "  \"reviews_count\": 415,\n" +
           "  \"about\": \"Welcome to my kitchen, today and every day... Welcome to my kitchen, today and every day... Welcome to my kitchen, today and every day... Welcome to my kitchen, today and every day...Welcome to my kitchen, today and every day... Welcome to my kitchen, today and every day...\",\n" +
           "  \"cuisines\": [],\n" +
           "  \"dishes\": [\n" +
           "    {\n" +
           "      \"id\": 332,\n" +
           "      \"name\": \"Side of Pita\",\n" +
           "      \"description\": \"Labaneh is soft cheese, similar in texture to cream cheese, made from strained\",\n" +
           "      \"thumbnail\": \"https://res.cloudinary.com/woodspoon/image/upload/t_small/uploads/dishes/6ac2cd78_7d07_4f6c_b4f3_7a34ec91a132_.jpg\",\n" +
           "      \"price\": {\n" +
           "        \"formatted\": \"\$1.5\",\n" +
           "        \"cents\": 150,\n" +
           "        \"value\": 1.50\n" +
           "      },\n" +
           "      \"cuisines\": []\n" +
           "    },\n" +
           "    {\n" +
           "      \"id\": 338,\n" +
           "      \"name\": \"Side of Pita\",\n" +
           "      \"description\": \"Labaneh is soft cheese, similar in texture to cream cheese, made from strained\",\n" +
           "      \"thumbnail\": \"https://res.cloudinary.com/woodspoon/image/upload/t_small/uploads/dishes/6ac2cd78_7d07_4f6c_b4f3_7a34ec91a132_.jpg\",\n" +
           "      \"price\": {\n" +
           "        \"formatted\": \"\$1.5\",\n" +
           "        \"cents\": 150,\n" +
           "        \"value\": 1.50\n" +
           "      },\n" +
           "      \"cuisines\": []\n" +
           "    },\n" +
           "    {\n" +
           "      \"id\": 339,\n" +
           "      \"name\": \"Side of Pita\",\n" +
           "      \"description\": \"Labaneh is soft cheese, similar in texture to cream cheese, made from strained\",\n" +
           "      \"thumbnail\": \"https://res.cloudinary.com/woodspoon/image/upload/t_small/uploads/dishes/6ac2cd78_7d07_4f6c_b4f3_7a34ec91a132_.jpg\",\n" +
           "      \"price\": {\n" +
           "        \"formatted\": \"\$1.5\",\n" +
           "        \"cents\": 150,\n" +
           "        \"value\": 1.50\n" +
           "      },\n" +
           "      \"cuisines\": []\n" +
           "    },\n" +
           "    {\n" +
           "      \"id\": 334,\n" +
           "      \"name\": \"Labaneh\",\n" +
           "      \"description\": \"Labaneh is soft cheese, similar in texture to cream cheese, made from strained\",\n" +
           "      \"thumbnail\": \"https://res.cloudinary.com/woodspoon/image/upload/t_small/uploads/dishes/6ac2cd78_7d07_4f6c_b4f3_7a34ec91a132_.jpg\",\n" +
           "      \"price\": {\n" +
           "        \"formatted\": \"\$10\",\n" +
           "        \"cents\": 1000,\n" +
           "        \"value\": 10.00\n" +
           "      },\n" +
           "      \"cuisines\": []\n" +
           "    }\n" +
           "  ],\n" +
           "  \"cooking_slots\": [\n" +
           "    {\n" +
           "      \"id\": 8840,\n" +
           "      \"starts_at\": \"2021-07-12T12:00:33.000-04:00\",\n" +
           "      \"ends_at\": \"2021-07-12T16:00:33.000-04:00\",\n" +
           "      \"order_from\": \"2021-07-12T12:30:33.000-04:00\",\n" +
           "      \"last_call_at\": null,\n" +
           "      \"free_delivery\": false,\n" +
           "      \"nationwide_shipping\": false,\n" +
           "      \"menu_items\": [\n" +
           "        {\n" +
           "          \"id\": 332,\n" +
           "          \"quantity\": 2,\n" +
           "          \"units_sold\": 0\n" +
           "        },\n" +
           "        {\n" +
           "          \"id\": 334,\n" +
           "          \"quantity\": 2,\n" +
           "          \"units_sold\": 0\n" +
           "        }\n" +
           "      ]\n" +
           "    },\n" +
           "    {\n" +
           "      \"id\": 8841,\n" +
           "      \"starts_at\": \"2021-07-12T12:00:33.000-04:00\",\n" +
           "      \"ends_at\": \"2021-07-12T16:00:33.000-04:00\",\n" +
           "      \"order_from\": \"2021-07-12T12:30:33.000-04:00\",\n" +
           "      \"last_call_at\": null,\n" +
           "      \"free_delivery\": false,\n" +
           "      \"nationwide_shipping\": false,\n" +
           "      \"menu_items\": [\n" +
           "        {\n" +
           "          \"id\": 332,\n" +
           "          \"quantity\": 2,\n" +
           "          \"units_sold\": 0\n" +
           "        },\n" +
           "        {\n" +
           "          \"id\": 334,\n" +
           "          \"quantity\": 3,\n" +
           "          \"units_sold\": 0\n" +
           "        }\n" +
           "      ]\n" +
           "    },\n" +
           "{\n" +
           "      \"id\": 8842,\n" +
           "      \"starts_at\": \"2021-08-12T12:00:33.000-04:00\",\n" +
           "      \"ends_at\": \"2021-08-12T16:00:33.000-04:00\",\n" +
           "      \"order_from\": \"2021-08-12T12:30:33.000-04:00\",\n" +
           "      \"last_call_at\": null,\n" +
           "      \"free_delivery\": false,\n" +
           "      \"nationwide_shipping\": false,\n" +
           "      \"menu_items\": [\n" +
           "        {\n" +
           "          \"id\": 332,\n" +
           "          \"quantity\": 2,\n" +
           "          \"units_sold\": 0\n" +
           "        },\n" +
           "        {\n" +
           "          \"id\": 334,\n" +
           "          \"quantity\": 3,\n" +
           "          \"units_sold\": 0\n" +
           "        }\n" +
           "      ]\n" +
           "    }\n" +
           "\n" +
           "  ]\n" +
           "}"
