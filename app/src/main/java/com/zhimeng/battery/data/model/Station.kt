package com.zhimeng.battery.data.model

import com.google.gson.annotations.SerializedName

data class Station(
    val boxId: String,
    val remaining: Int,
    val status: BoxStatus
)

/*机柜状态：{ONLINE：在线, REPAIR：维修中, NOT_FIND：没有发现机柜,TIMEOUT: 超时}*/
enum class BoxStatus {
    @SerializedName("ONLINE")
    ONLINE,

    @SerializedName("REPAIR")
    REPAIR,

    @SerializedName("NOT_FIND")
    NOT_FIND,

    @SerializedName("TIMEOUT")
    TIMEOUT
}
