package com.zhimeng.battery.data.service

import com.jakewharton.rxrelay2.PublishRelay
import com.zhimeng.battery.BuildConfig
import com.zhimeng.battery.network.BatteryApi
import com.zhimeng.battery.network.model.FavoriteResponse
import com.zhimeng.battery.network.model.FeedResponse
import com.zhimeng.battery.network.model.BatteryResponse
import com.zhimeng.battery.network.unwrap
import io.reactivex.Single
import kotlin.random.Random

data class FeedEvent(
    val id: String,
    val type: Type
) {
    enum class Type {
        FOLLOWED,
        UNFOLLOWED
    }
}

enum class FeedListType(s: String) {
    ALL("ALL"),
    FOLLOWED("FOLLOWED")
}

data class PhotoEvent(
    val id: Long,
    val type: Type
) {
    enum class Type {
        LIKE,
        UNLIKE
    }
}


class FeedService {

    companion object {
        val shared = FeedService()
        val useFake = false
    }

    val feedEventRelay = PublishRelay.create<FeedEvent>()

    val likePhotoEventRelay = PublishRelay.create<PhotoEvent>()

    /**
     * 抓取所有的Feeds.
     */
    fun fetchAllFeeds(
        onlyFollowed: Boolean,
        pageIndex: Int,
        pageSize: Int
    ): Single<FeedResponse> {
        if (BuildConfig.DEBUGGING_MODE && useFake) {
            return Single.create {
                Thread.sleep(1500)
                if (Random.nextInt(0, 100) > 50) {
                    /* success */
                    it.onSuccess(
                        FeedResponse.fake(
                            onlyFollowed = onlyFollowed,
                            pageIndex = pageIndex,
                            pageSize = if (onlyFollowed) 0 else 20,
                            maxPages = if (onlyFollowed) 1 else 3
                        )
                    )
                } else {
                    it.onError(
                        Throwable("我就让你出错你能咋地")
                    )
                }
            }
        } else {
            return BatteryApi.service.fetchFeeds(
                pageIndex = pageIndex,
                pageSize = pageSize,
                type = if (onlyFollowed)
                    FeedListType.FOLLOWED
                else
                    FeedListType.ALL
            ).unwrap()
        }
    }

    fun followFeed(id: String): Single<BatteryResponse<Any>> {
        return if (BuildConfig.DEBUGGING_MODE && useFake) {
            Single.create {
                Thread.sleep(500)
                it.onSuccess(BatteryResponse.success(1))
                feedEventRelay.accept(FeedEvent(id = id, type = FeedEvent.Type.FOLLOWED))
            }
        } else {
            BatteryApi.service.follows(id = id)
        }
    }

    /**
     * 抓取所有喜欢（收藏）过的图片.
     */
    fun fetchFavorites(pageIndex: Int, pageSize: Int): Single<FavoriteResponse> {
        if (BuildConfig.DEBUGGING_MODE && useFake) {
            return Single.create {
                Thread.sleep(1500)
                if (Random.nextInt(0, 100) > 10) {
                    /* success */
                    it.onSuccess(
                        FavoriteResponse.fake(
                            pageIndex = pageIndex,
                            pageSize = 20
                        )
                    )
                } else {
                    it.onError(
                        Throwable("我就让你出错你能咋地")
                    )
                }
            }
        } else {
            return BatteryApi.service.fetchFavorites(
                pageIndex = pageIndex,
                pageSize = pageSize
            ).unwrap()
        }
    }

    fun unlikePhoto(id: Long): Single<BatteryResponse<Any>> {
        return Single.create {
            Thread.sleep(500)
            it.onSuccess(BatteryResponse.success(1))
        }
    }

    fun likePhoto(photoId: Long): Single<BatteryResponse<Any>> {
        return if (BuildConfig.DEBUGGING_MODE && UserService.useFake) {
            Single.create {
                Thread.sleep(500)
                it.onSuccess(BatteryResponse.success(1))
            }
        } else {
            BatteryApi.service.likePhoto(hashMapOf<String, Any>().apply {
                put("photoId", photoId)
            })
        }
    }
}
