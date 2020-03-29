package com.zhimeng.battery.network.model

import com.google.gson.annotations.SerializedName
import com.mooveit.library.Fakeit
import com.zhimeng.battery.data.model.User

data class UserResponse(
    @SerializedName("loginUser")
    val user: User,

    @SerializedName("token")
    val token: String,

    @SerializedName("expirationTime")
    val expirationTime: Long,

    @SerializedName("message")
    val message: String?
) {
    companion object {
        fun fake(): UserResponse {
            return UserResponse(
                user = User.fake(),
                token = "NA",
                expirationTime = (10000..80000).random().toLong(),
                message = Fakeit.pokemon().name()
            )
        }
    }
}

