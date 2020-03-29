package com.zhimeng.battery.data.model

import com.google.gson.annotations.SerializedName
import com.mooveit.library.Fakeit
import com.zhimeng.battery.utilities.Debug


data class FavoritePhoto(
    @SerializedName("code")
    val id: String,

    @SerializedName("nickname")
    val nickname: String,

    @SerializedName("photo")
    val photo: Photo
) {
    companion object {

        private var currID: Long = 100000

        fun fake(): FavoritePhoto {
            currID += 1
            return FavoritePhoto(
                id = currID.toString(),
                nickname = Fakeit.pokemon().name(),
                photo = Photo.fake(Debug.images.random())
            )
        }
    }
}
