package com.zhimeng.battery.network.model

import com.zhimeng.battery.data.model.Feed
import kotlin.math.min

data class FeedResponse(
    val list: List<Feed>,
    val pageIndex: Int,
    val pageSize: Int,
    val pages: Int,
    val totalItems: Int
) {
    companion object {
        fun fake(onlyFollowed: Boolean, pageIndex: Int, pageSize: Int, maxPages: Int): FeedResponse {
            return FeedResponse(
                list = (0 until pageSize).map { Feed.fake(onlyFollowed = onlyFollowed) },
                pageIndex = pageIndex,
                pageSize = pageSize,
                pages = min(maxPages, pageIndex + 1),
                totalItems = 10000
            )
        }
    }
}
