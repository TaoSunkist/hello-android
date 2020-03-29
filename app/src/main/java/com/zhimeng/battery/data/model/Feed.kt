package com.zhimeng.battery.data.model

import com.google.gson.annotations.SerializedName
import com.mooveit.library.Fakeit
import com.zhimeng.battery.utilities.Debug

data class Feed(
    @SerializedName("code")
    val code: String,

    @SerializedName("profilePictureUrl")
    val profilePictureUrl: String,

    @SerializedName("nickname")
    val nickname: String,

    @SerializedName("fansCount")
    val fansCount: Long,

    @SerializedName("unreadMessageTotal")
    val unreadMessageTotal: Int,

    @SerializedName("isFollow")
    val isFollow: Boolean

) {
    companion object {
        fun fake(onlyFollowed: Boolean): Feed {
            return Feed(
                code = System.currentTimeMillis().toString(),
                profilePictureUrl = Debug.images.random(),
                nickname = Fakeit.book().author(),
                fansCount = (1000..1000000).random().toLong(),
                unreadMessageTotal = (0..1).random(),
                isFollow = if (onlyFollowed) onlyFollowed else listOf(true, false).random()
            )
        }
    }
}