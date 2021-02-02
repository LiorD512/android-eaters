package com.bupp.wood_spoon_eaters.model

import android.os.Parcelable
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bupp.wood_spoon_eaters.network.ApiService
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class FeedFlow(
    @SerializedName("title") var title: String? = null,
    @SerializedName("subtitle") var subtitle: String? = null,
    @SerializedName("search") var search: Search? = Search()
): Parcelable


class FeedPagingSource(
    val apiService: ApiService,
) : PagingSource<Int, FeedFlow>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FeedFlow> {
        try {
            // Start refresh at page 1 if undefined.
            val nextPage = params.key ?: 1
            val response = apiService.getFeedFlow(page = nextPage)
            Log.d("wowFeedPagingSource","loading.. $nextPage");

            response.let{
                return LoadResult.Page(
                    data = response.data!!,
                    prevKey = if (nextPage == 1) null else nextPage - 1,
                    nextKey = if (nextPage > response.meta.totalPages!!) null else response.meta.currentPage?.plus(1)
                )
            }
            Log.d("wowFeedPagingSource","FeedPagingSource failed");
            return LoadResult.Page(listOf(), 0, 0)

        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, FeedFlow>): Int? {
        return 0
    }

}