package com.bupp.wood_spoon_eaters.repositoriesimport com.bupp.wood_spoon_eaters.managers.CampaignManagerimport com.bupp.wood_spoon_eaters.managers.CartManagerimport com.bupp.wood_spoon_eaters.model.*import com.bupp.wood_spoon_eaters.network.base_repos.FeedRepositoryImplimport com.bupp.wood_spoon_eaters.network.result_handler.ResultHandlerimport kotlinx.coroutines.Dispatchersimport kotlinx.coroutines.withContextclass FeedRepository(    private val apiService: FeedRepositoryImpl,    private val cartManager: CartManager,    private val campaignManager: CampaignManager,    private val metaDataManager: MetaDataRepository) {    data class FeedRepoResult(        val type: FeedRepoStatus,        val feed: List<FeedAdapterItem> = emptyList(),        val isLargeItems: Boolean = false    )    data class SearchTagsResult(        val type: FeedRepoStatus,        val tags: List<String>? = null    )    enum class FeedRepoStatus {        SUCCESS,        HREF_SUCCESS,        SERVER_ERROR,        SOMETHING_WENT_WRONG,    }    private val isLargeItems = false    private var lastFeedDataResult: FeedResult? = null    suspend fun getFeed(        feedRequest: FeedRequest,        isLongFeed: Boolean    ): FeedRepoResult = withContext(Dispatchers.IO) {        apiService.getFeed(            isLongFeed,            feedRequest.lat,            feedRequest.lng,            feedRequest.addressId,            feedRequest.timestamp        )    }.let {        return when (it) {            is ResultHandler.NetworkError -> {                FeedRepoResult(FeedRepoStatus.SERVER_ERROR)            }            is ResultHandler.GenericError -> {                FeedRepoResult(FeedRepoStatus.SOMETHING_WENT_WRONG)            }            is ResultHandler.Success -> {                val feedData = processFeedData(it.value.data)                FeedRepoResult(FeedRepoStatus.SUCCESS, feedData, isLargeItems)            }            else -> {                FeedRepoResult(FeedRepoStatus.SOMETHING_WENT_WRONG)            }        }    }    suspend fun getFeedHref(href: String): FeedRepoResult {        val result = withContext(Dispatchers.IO) {            apiService.getHrefCollection(href)        }        result.let {            return when (it) {                is ResultHandler.NetworkError -> {                    FeedRepoResult(FeedRepoStatus.SERVER_ERROR)                }                is ResultHandler.GenericError -> {                    FeedRepoResult(FeedRepoStatus.SOMETHING_WENT_WRONG)                }                is ResultHandler.Success -> {                    val feedData = processFeedHrefData(it.value.data, href)                    FeedRepoResult(FeedRepoStatus.HREF_SUCCESS, feedData, isLargeItems)                }                else -> {                    FeedRepoResult(FeedRepoStatus.SOMETHING_WENT_WRONG)                }            }        }    }    private fun processFeedData(        feedResult: FeedResult?,        input: String? = null    ): List<FeedAdapterItem> {        var localId: Long = -1        val feedData = mutableListOf<FeedAdapterItem>()        feedResult?.sections?.forEachIndexed { feedSectionIndex, feedSection ->            if (input != null && feedData.size == 0 && feedResult.sections.firstOrNull()?.collections?.firstOrNull() is FeedRestaurantSection) {                localId++                val searchTitle = "Results for “${input.capitalize()}”"                feedData.add(FeedAdapterSearchTitle(searchTitle, localId))            }            feedSection.title?.let { title ->                val hasHref = !feedSection.full_href.isNullOrEmpty()                val hasSupportedSections = feedSection.collections?.firstOrNull { section -> section !is FeedUnknownSection } != null                if ( title.isNotBlank() && (hasHref || hasSupportedSections)) {                    localId++                    feedData.add(FeedAdapterTitle(title, localId))                }            }            feedSection.full_href?.let {                localId++                feedData.add(FeedAdapterHref(it, localId))            }            feedSection.collections?.forEachIndexed { index, feedSectionCollectionItem ->                localId++                when (feedSectionCollectionItem) {                    is FeedCampaignSection -> {                        feedData.add(FeedAdapterCoupons(feedSectionCollectionItem, localId))                    }                    is FeedIsEmptySection -> {                        feedData.add(                            FeedAdapterEmptyFeed(                                feedSectionCollectionItem,                                localId,                                isCartEmpty()                            )                        )                    }                    is FeedSingleEmptySection -> {                        feedData.add(FeedAdapterEmptySection(feedSectionCollectionItem, localId))                    }                    is FeedSearchEmptySection -> {                        feedData.add(FeedAdapterEmptySearch(feedSectionCollectionItem, localId))                    }                    is FeedComingSoonSection -> {                        feedData.add(                            FeedAdapterComingSoonSection(                                feedSectionCollectionItem,                                localId                            )                        )                    }                    is FeedRestaurantSection -> {                        feedSectionCollectionItem.flagUrl =                            metaDataManager.getCountryFlagById(feedSectionCollectionItem.countryId)                        feedSectionCollectionItem.countryIso =                            metaDataManager.getCountryIsoById(feedSectionCollectionItem.countryId)                        if (isLargeItems) {                            feedData.add(                                FeedAdapterLargeRestaurant(                                    feedSectionCollectionItem,                                    localId                                )                            )                        } else {                            feedData.add(                                FeedAdapterRestaurant(                                    id = localId,                                    restaurantSection = feedSectionCollectionItem,                                    sectionTitle = feedSection.title,                                    sectionOrder = feedSectionIndex + 1,                                    restaurantOrderInSection = index + 1,                                    )                            )                        }                    }                    is FeedHeroItemSection -> {                        computeShareUrlIfHeroCampaign(feedSectionCollectionItem)?.let {                            feedSectionCollectionItem.url = it                        }                        val filterIsInstance = feedData.filterIsInstance<FeedAdapterHero>()                        if (filterIsInstance.isNotEmpty()) {                            filterIsInstance.first().heroList.add(feedSectionCollectionItem)                        } else {                            feedData.add(                                FeedAdapterHero(                                    mutableListOf(feedSectionCollectionItem),                                    localId                                )                            )                        }                    }                    is FeedChefItemSection -> {                        val filterIsInstance = feedData.filterIsInstance<FeedAdapterChefSection>()                        if (filterIsInstance.isNotEmpty()) {                            filterIsInstance.first().chefSection.add(feedSectionCollectionItem)                        } else {                            feedData.add(                                FeedAdapterChefSection(                                    mutableListOf(                                        feedSectionCollectionItem                                    ), localId                                )                            )                        }                    }                    is FeedDishItemSection -> {                        val filterIsInstance = feedData.filterIsInstance<FeedAdapterDishSection>()                        if (filterIsInstance.isNotEmpty()) {                            filterIsInstance.first().dishSection.add(feedSectionCollectionItem)                        } else {                            feedData.add(                                FeedAdapterDishSection(                                    mutableListOf(                                        feedSectionCollectionItem                                    ), localId                                )                            )                        }                    }                    is FeedUnknownSection -> { /* not implemented yet */                    }                }            }        }        lastFeedDataResult = feedResult        return feedData    }    private fun computeShareUrlIfHeroCampaign(        feedSectionCollectionItem: FeedHeroItemSection    ): String? = campaignManager.curCampaigns?.firstOrNull {        val campaignId = feedSectionCollectionItem.url            ?.substringAfter("campaign_id=")            ?.substringBeforeLast(",")        it.campaignId.equals(campaignId)    }?.shareUrl    private fun isCartEmpty(): Boolean {        return cartManager.isCartEmpty()    }    private fun processFeedHrefData(        data: List<FeedSectionCollectionItem>?,        href: String    ): List<FeedAdapterItem> {        val tempFeedResult = mutableListOf<FeedSection>()        lastFeedDataResult?.sections?.forEachIndexed { index, section ->            section.full_href?.let {                if (it == href) {                    data?.let { data ->                        if (data.isNotEmpty() && !data[0].items.isNullOrEmpty()) {                            section.full_href = null                            lastFeedDataResult?.sections?.let { sections -> sections[index].collections = data.toMutableList() }                        } else {                            return@forEachIndexed                        }                    }                }            }            tempFeedResult.add(section)        }        return processFeedData(FeedResult(tempFeedResult))    }    suspend fun getFeedBySearch(input: String, feedRequest: FeedRequest): FeedRepoResult {        val result = withContext(Dispatchers.IO) {            val lat = feedRequest.lat            val lng = feedRequest.lng            val addressId = feedRequest.addressId            val timestamp = feedRequest.timestamp            apiService.search(input, lat, lng, addressId, timestamp)        }        result.let {            return when (it) {                is ResultHandler.NetworkError -> {                    FeedRepoResult(FeedRepoStatus.SERVER_ERROR)                }                is ResultHandler.GenericError -> {                    FeedRepoResult(FeedRepoStatus.SOMETHING_WENT_WRONG)                }                is ResultHandler.Success -> {                    val feedData = processFeedData(it.value.data, input)                    FeedRepoResult(FeedRepoStatus.SUCCESS, feedData, isLargeItems)                }                else -> {                    FeedRepoResult(FeedRepoStatus.SOMETHING_WENT_WRONG)                }            }        }    }    suspend fun getRecentOrders(feedRequest: FeedRequest): FeedRepoResult {        val result = withContext(Dispatchers.IO) {            val lat = feedRequest.lat            val lng = feedRequest.lng            val addressId = feedRequest.addressId            val timestamp = feedRequest.timestamp            apiService.getRecentOrders(lat, lng, addressId, timestamp)        }        result.let {            return when (it) {                is ResultHandler.NetworkError -> {                    FeedRepoResult(FeedRepoStatus.SERVER_ERROR)                }                is ResultHandler.GenericError -> {                    FeedRepoResult(FeedRepoStatus.SOMETHING_WENT_WRONG)                }                is ResultHandler.Success -> {                    val feedData = processFeedData(                        FeedResult(                            listOf(                                FeedSection(                                    collections = it.value.data as MutableList<FeedSectionCollectionItem>                                )                            )                        )                    )                    FeedRepoResult(FeedRepoStatus.SUCCESS, feedData, isLargeItems)                }                else -> {                    FeedRepoResult(FeedRepoStatus.SOMETHING_WENT_WRONG)                }            }        }    }    suspend fun getSearchTags(feedRequest: FeedRequest): SearchTagsResult {        val result = withContext(Dispatchers.IO) {            val lat = feedRequest.lat            val lng = feedRequest.lng            val addressId = feedRequest.addressId            apiService.getSearchTags(lat, lng, addressId)        }        result.let {            return when (it) {                is ResultHandler.NetworkError -> {                    SearchTagsResult(FeedRepoStatus.SERVER_ERROR)                }                is ResultHandler.GenericError -> {                    SearchTagsResult(FeedRepoStatus.SOMETHING_WENT_WRONG)                }                is ResultHandler.Success -> {                    SearchTagsResult(FeedRepoStatus.SUCCESS, it.value.data)                }                else -> {                    SearchTagsResult(FeedRepoStatus.SOMETHING_WENT_WRONG)                }            }        }    }}