package com.zhimeng.battery.network.model

import com.zhimeng.battery.data.model.FavoritePhoto

data class FavoriteResponse(
    val list: List<FavoritePhoto>,
    val pageIndex: Int,
    val pageSize: Int,
    val pages: Int,
    val totalItems: Int
) {
    companion object {
        fun fake(pageIndex: Int, pageSize: Int): FavoriteResponse {
            return FavoriteResponse(
                list = (0 until pageSize).map { FavoritePhoto.fake() },
                pageIndex = pageIndex,
                pageSize = pageSize,
                pages = 100,
                totalItems = 10000
            )
        }
    }
}
