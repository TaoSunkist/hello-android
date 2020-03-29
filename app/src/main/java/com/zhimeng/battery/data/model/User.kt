package com.zhimeng.battery.data.model

import com.mooveit.library.Fakeit
import com.zhimeng.battery.utilities.Debug

data class User(
    val account: String,
    val cellphone: String,
    val followIdolCount: Int?,
    val nickname: String,
    val profilePictureUrl: String?,
    val followIdolCodeList: List<String>?,
    var userStatus: UserStatus? = null
) {
    companion object {
        fun fake(): User {
            return User(
                cellphone = (15201457760..15201999999).random().toString(),
                nickname = Fakeit.name().name(),
                account = Fakeit.address().zipCode(),
                followIdolCount = listOf(null, 2000000, 3200).random(),
                profilePictureUrl = Debug.images.random(),
                followIdolCodeList = null
            )
        }
    }
}