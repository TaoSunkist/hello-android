package com.zhimeng.battery.data.service

import com.zhimeng.battery.BuildConfig
import com.zhimeng.battery.network.BatteryApi
import com.zhimeng.battery.network.model.UserResponse
import com.zhimeng.battery.network.unwrap
import io.reactivex.Single

class ChargerService {

    companion object {
        val shared = ChargerService()
        var useFake = true
    }

    /**
     * 查询机柜状态
     */
    fun chargerBoxes(boxid: Long): Single<UserResponse> {
        if (BuildConfig.DEBUGGING_MODE && useFake) {
            return Single.create {
                Thread.sleep(500)
                it.onSuccess(UserResponse.fake())
            }
        } else {
            return BatteryApi.service.chargerBoxes(boxid = boxid).unwrap()
        }
    }
}