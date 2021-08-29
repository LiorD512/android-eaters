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


private const val json: String = """{
        "id": 273,
        "first_name": "Charolette",
        "last_name": "Hilll",
        "restaurant_name": "Charolette Hilll's Restaurant",
        "cover": "https://res.cloudinary.com/woodspoonstaging/image/upload/t_medium/uploads/cooks/7",
        "thumbnail": "https://res.cloudinary.com/woodspoonstaging/image/upload/t_medium/uploads/cooks/7",
        "reviews_count": 0,
        "avg_rating": 0.0,
        "about": "branding",
        "is_favorite": false,
        "tags": [],
        "share_url": "https://www.youtube.com/watch?v=AFFZwSjVxKE",
        "video": null,
        "dishes": [
            {
                "id": 1444,
                "name": "Barbecue Ribs",
                "thumbnail": "https://res.cloudinary.com/woodspoonstaging/image/upload/t_medium/uploads/dishes/2",
                "price": {
                    "formatted": "$3.05",
                    "cents": 305,
                    "value": 3.05
                },
                "description": "Three egg whites with spinach, mushrooms, caramelized onions, tomatoes and low-fat feta cheese. With herbed quinoa, and your choice of rye or whole-grain toast.",
                "tags": []
            },
            {
                "id": 1441,
                "name": "Bunny Chow",
                "thumbnail": "https://res.cloudinary.com/woodspoonstaging/image/upload/t_medium/uploads/dishes/10",
                "price": {
                    "formatted": "$3.58",
                    "cents": 358,
                    "value": 3.58
                },
                "description": "Breaded fried chicken with waffles, and a side of maple syrup.",
                "tags": []
            },
            {
                "id": 1445,
                "name": "Meatballs with Sauce",
                "thumbnail": "https://res.cloudinary.com/woodspoonstaging/image/upload/t_medium/uploads/dishes/6",
                "price": {
                    "formatted": "$18.25",
                    "cents": 1825,
                    "value": 18.25
                },
                "description": "Creamy mascarpone cheese and custard layered between espresso and rum soaked house-made ladyfingers, topped with Valrhona cocoa powder.",
                "tags": []
            },
            {
                "id": 1443,
                "name": "Barbecue Ribs",
                "thumbnail": "https://res.cloudinary.com/woodspoonstaging/image/upload/t_medium/uploads/dishes/2",
                "price": {
                    "formatted": "$7.99",
                    "cents": 799,
                    "value": 7.99
                },
                "description": "28-day aged 300g USDA Certified Prime Ribeye, rosemary-thyme garlic butter, with choice of two sides.",
                "tags": []
            },
            {
                "id": 1442,
                "name": "Souvlaki",
                "thumbnail": "https://res.cloudinary.com/woodspoonstaging/image/upload/t_medium/uploads/dishes/7",
                "price": {
                    "formatted": "$2.64",
                    "cents": 264,
                    "value": 2.64
                },
                "description": "Two butter croissants of your choice (plain, almond or cheese). With a side of herb butter or house-made hazelnut spread.",
                "tags": []
            }
        ],
        "cooking_slots": [
            {
                "id": 2853,
                "starts_at": "2021-07-30T14:00:00.000-04:00",
                "ends_at": "2021-07-30T20:00:00.000-04:00",
                "order_from": "2021-08-02T11:08:08.789-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": [
                            {
                                "id": 5213,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1444,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    },
                    {
                        "title": "On the menu",
                        "menu_items": [
                            {
                                "id": 5213,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1444,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    }
                ]
            },
            {
                "id": 2845,
                "starts_at": "2021-07-26T14:00:00.000-04:00",
                "ends_at": "2021-07-26T20:00:00.000-04:00",
                "order_from": "2021-08-02T11:08:08.814-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": [
                            {
                                "id": 5199,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1442,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5200,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1444,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5201,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1443,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5202,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1441,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    },
                    {
                        "title": "On the menu",
                        "menu_items": [
                            {
                                "id": 5202,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1441,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5199,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1442,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5201,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1443,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5200,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1444,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5203,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1445,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    }
                ]
            },
            {
                "id": 2848,
                "starts_at": "2021-07-28T06:00:00.000-04:00",
                "ends_at": "2021-07-28T12:00:00.000-04:00",
                "order_from": "2021-08-02T11:08:08.848-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": []
                    },
                    {
                        "title": "On the menu",
                        "menu_items": []
                    }
                ]
            },
            {
                "id": 2855,
                "starts_at": "2021-07-31T14:00:00.000-04:00",
                "ends_at": "2021-07-31T20:00:00.000-04:00",
                "order_from": "2021-08-02T11:08:08.861-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": [
                            {
                                "id": 5214,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1443,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5215,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1445,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5216,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1444,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5217,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1441,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    },
                    {
                        "title": "On the menu",
                        "menu_items": [
                            {
                                "id": 5217,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1441,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5214,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1443,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5216,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1444,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5215,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1445,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    }
                ]
            },
            {
                "id": 2847,
                "starts_at": "2021-07-27T14:00:00.000-04:00",
                "ends_at": "2021-07-27T20:00:00.000-04:00",
                "order_from": "2021-08-02T11:08:08.921-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": [
                            {
                                "id": 5204,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1443,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5205,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1441,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5206,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1444,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    },
                    {
                        "title": "On the menu",
                        "menu_items": [
                            {
                                "id": 5205,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1441,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5204,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1443,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5206,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1444,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    }
                ]
            },
            {
                "id": 2849,
                "starts_at": "2021-07-28T14:00:00.000-04:00",
                "ends_at": "2021-07-28T20:00:00.000-04:00",
                "order_from": "2021-08-02T11:08:08.956-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": [
                            {
                                "id": 5207,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1444,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5208,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1443,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5209,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1441,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5210,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1445,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    },
                    {
                        "title": "On the menu",
                        "menu_items": [
                            {
                                "id": 5209,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1441,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5211,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1442,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5208,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1443,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5207,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1444,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5210,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1445,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    }
                ]
            },
            {
                "id": 2857,
                "starts_at": "2021-08-01T14:00:00.000-04:00",
                "ends_at": "2021-08-01T20:00:00.000-04:00",
                "order_from": "2021-08-02T11:08:09.000-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": [
                            {
                                "id": 5218,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1442,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5219,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1444,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    },
                    {
                        "title": "On the menu",
                        "menu_items": [
                            {
                                "id": 5218,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1442,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5219,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1444,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    }
                ]
            },
            {
                "id": 2852,
                "starts_at": "2021-07-30T06:00:00.000-04:00",
                "ends_at": "2021-07-30T12:00:00.000-04:00",
                "order_from": "2021-08-02T11:08:09.040-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": []
                    },
                    {
                        "title": "On the menu",
                        "menu_items": []
                    }
                ]
            },
            {
                "id": 2851,
                "starts_at": "2021-07-29T14:00:00.000-04:00",
                "ends_at": "2021-07-29T20:00:00.000-04:00",
                "order_from": "2021-08-02T11:08:09.052-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": [
                            {
                                "id": 5212,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1441,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    },
                    {
                        "title": "On the menu",
                        "menu_items": [
                            {
                                "id": 5212,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1441,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    }
                ]
            },
            {
                "id": 2844,
                "starts_at": "2021-07-26T06:00:00.000-04:00",
                "ends_at": "2021-07-26T12:00:00.000-04:00",
                "order_from": "2021-08-02T11:08:09.067-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": []
                    },
                    {
                        "title": "On the menu",
                        "menu_items": []
                    }
                ]
            },
            {
                "id": 2846,
                "starts_at": "2021-07-27T06:00:00.000-04:00",
                "ends_at": "2021-07-27T12:00:00.000-04:00",
                "order_from": "2021-08-02T11:08:09.078-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": []
                    },
                    {
                        "title": "On the menu",
                        "menu_items": []
                    }
                ]
            },
            {
                "id": 2856,
                "starts_at": "2021-08-01T06:00:00.000-04:00",
                "ends_at": "2021-08-01T12:00:00.000-04:00",
                "order_from": "2021-08-02T11:08:09.095-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": []
                    },
                    {
                        "title": "On the menu",
                        "menu_items": []
                    }
                ]
            },
            {
                "id": 2850,
                "starts_at": "2021-07-29T06:00:00.000-04:00",
                "ends_at": "2021-07-29T12:00:00.000-04:00",
                "order_from": "2021-08-02T11:08:09.110-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": []
                    },
                    {
                        "title": "On the menu",
                        "menu_items": []
                    }
                ]
            },
            {
                "id": 2854,
                "starts_at": "2021-07-31T06:00:00.000-04:00",
                "ends_at": "2021-07-31T12:00:00.000-04:00",
                "order_from": "2021-08-02T11:08:09.122-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": []
                    },
                    {
                        "title": "On the menu",
                        "menu_items": []
                    }
                ]
            },
            {
                "id": 2859,
                "starts_at": "2021-08-02T14:00:00.000-04:00",
                "ends_at": "2021-08-02T20:00:00.000-04:00",
                "order_from": "2021-08-02T14:30:00.000-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": [
                            {
                                "id": 5220,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1444,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5221,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1445,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5222,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1441,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    },
                    {
                        "title": "On the menu",
                        "menu_items": [
                            {
                                "id": 5222,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1441,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5220,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1444,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5221,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1445,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    }
                ]
            },
            {
                "id": 2858,
                "starts_at": "2021-08-02T06:00:00.000-04:00",
                "ends_at": "2021-08-02T12:00:00.000-04:00",
                "order_from": "2021-08-02T11:08:09.163-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": []
                    },
                    {
                        "title": "On the menu",
                        "menu_items": []
                    }
                ]
            },
            {
                "id": 2867,
                "starts_at": "2021-08-06T14:00:00.000-04:00",
                "ends_at": "2021-08-06T20:00:00.000-04:00",
                "order_from": "2021-08-06T14:30:00.000-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": [
                            {
                                "id": 5231,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1444,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5232,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1443,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5233,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1445,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5234,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1441,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    },
                    {
                        "title": "On the menu",
                        "menu_items": [
                            {
                                "id": 5234,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1441,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5235,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1442,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5232,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1443,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5231,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1444,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5233,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1445,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    }
                ]
            },
            {
                "id": 2861,
                "starts_at": "2021-08-03T14:00:00.000-04:00",
                "ends_at": "2021-08-03T20:00:00.000-04:00",
                "order_from": "2021-08-03T14:30:00.000-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": [
                            {
                                "id": 5223,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1444,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5224,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1443,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5225,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1441,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    },
                    {
                        "title": "On the menu",
                        "menu_items": [
                            {
                                "id": 5225,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1441,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5224,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1443,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5223,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1444,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    }
                ]
            },
            {
                "id": 2860,
                "starts_at": "2021-08-03T06:00:00.000-04:00",
                "ends_at": "2021-08-03T12:00:00.000-04:00",
                "order_from": "2021-08-03T06:30:00.000-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": []
                    },
                    {
                        "title": "On the menu",
                        "menu_items": []
                    }
                ]
            },
            {
                "id": 2863,
                "starts_at": "2021-08-04T14:00:00.000-04:00",
                "ends_at": "2021-08-04T20:00:00.000-04:00",
                "order_from": "2021-08-04T14:30:00.000-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": [
                            {
                                "id": 5226,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1444,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5227,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1442,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    },
                    {
                        "title": "On the menu",
                        "menu_items": [
                            {
                                "id": 5227,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1442,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5226,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1444,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    }
                ]
            },
            {
                "id": 2868,
                "starts_at": "2021-08-07T06:00:00.000-04:00",
                "ends_at": "2021-08-07T12:00:00.000-04:00",
                "order_from": "2021-08-07T06:30:00.000-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": []
                    },
                    {
                        "title": "On the menu",
                        "menu_items": []
                    }
                ]
            },
            {
                "id": 2862,
                "starts_at": "2021-08-04T06:00:00.000-04:00",
                "ends_at": "2021-08-04T12:00:00.000-04:00",
                "order_from": "2021-08-04T06:30:00.000-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": []
                    },
                    {
                        "title": "On the menu",
                        "menu_items": []
                    }
                ]
            },
            {
                "id": 2866,
                "starts_at": "2021-08-06T06:00:00.000-04:00",
                "ends_at": "2021-08-06T12:00:00.000-04:00",
                "order_from": "2021-08-06T06:30:00.000-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": []
                    },
                    {
                        "title": "On the menu",
                        "menu_items": []
                    }
                ]
            },
            {
                "id": 2864,
                "starts_at": "2021-08-05T06:00:00.000-04:00",
                "ends_at": "2021-08-05T12:00:00.000-04:00",
                "order_from": "2021-08-05T06:30:00.000-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": []
                    },
                    {
                        "title": "On the menu",
                        "menu_items": []
                    }
                ]
            },
            {
                "id": 2865,
                "starts_at": "2021-08-05T14:00:00.000-04:00",
                "ends_at": "2021-08-05T20:00:00.000-04:00",
                "order_from": "2021-08-05T14:30:00.000-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": [
                            {
                                "id": 5228,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1443,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5229,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1444,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5230,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1445,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    },
                    {
                        "title": "On the menu",
                        "menu_items": [
                            {
                                "id": 5228,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1443,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5229,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1444,
                                "tags": [
                                    "Only 2 left"
                                ]
                            },
                            {
                                "id": 5230,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1445,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    }
                ]
            },
            {
                "id": 2870,
                "starts_at": "2021-08-08T06:00:00.000-04:00",
                "ends_at": "2021-08-08T12:00:00.000-04:00",
                "order_from": "2021-08-08T06:30:00.000-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": []
                    },
                    {
                        "title": "On the menu",
                        "menu_items": []
                    }
                ]
            },
            {
                "id": 2869,
                "starts_at": "2021-08-07T14:00:00.000-04:00",
                "ends_at": "2021-08-07T20:00:00.000-04:00",
                "order_from": "2021-08-07T14:30:00.000-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": [
                            {
                                "id": 5236,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1445,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    },
                    {
                        "title": "On the menu",
                        "menu_items": [
                            {
                                "id": 5236,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1445,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    }
                ]
            },
            {
                "id": 2871,
                "starts_at": "2021-08-08T14:00:00.000-04:00",
                "ends_at": "2021-08-08T20:00:00.000-04:00",
                "order_from": "2021-08-08T14:30:00.000-04:00",
                "last_call_at": null,
                "free_delivery": false,
                "nationwide_shipping": false,
                "sections": [
                    {
                        "title": "Most popular",
                        "menu_items": [
                            {
                                "id": 5237,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1444,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    },
                    {
                        "title": "On the menu",
                        "menu_items": [
                            {
                                "id": 5237,
                                "quantity": 2,
                                "units_sold": 0,
                                "dish_id": 1444,
                                "tags": [
                                    "Only 2 left"
                                ]
                            }
                        ]
                    }
                ]
            }
        ]
}"""
  
