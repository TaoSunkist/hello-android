package com.zhimeng.battery.data.service

import com.zhimeng.battery.data.model.Borrowing
import com.zhimeng.battery.data.model.BoxInfomation
import com.zhimeng.battery.network.BatteryApi
import com.zhimeng.battery.network.unwrap
import io.reactivex.Single

class StationService {

    companion object {
        val shared = StationService()
    }

    fun queryBoxInfomation(boxid: String = "RL3H042003250001"): Single<BoxInfomation> {
        return BatteryApi.service.stationsBoxid(boxid = boxid).unwrap()
    }

    fun borrowing(boxId: String): Single<Borrowing> {
        return BatteryApi.service.borrowing(boxId = boxId).unwrap()
    }
}