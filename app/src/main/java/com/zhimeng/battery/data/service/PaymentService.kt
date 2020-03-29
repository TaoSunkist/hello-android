package com.zhimeng.battery.data.service

import androidx.annotation.NonNull
import com.zhimeng.battery.BuildConfig
import com.zhimeng.battery.data.model.OrderDetail
import com.zhimeng.battery.data.model.PayMetadata
import com.zhimeng.battery.data.model.PayType
import com.zhimeng.battery.network.BatteryApi
import com.zhimeng.battery.network.BatteryService
import com.zhimeng.battery.network.model.BatteryResponse
import com.zhimeng.battery.network.model.LoginDto
import com.zhimeng.battery.network.model.UserResponse
import com.zhimeng.battery.network.unwrap
import io.reactivex.Single

class PaymentService {

    companion object {
        val shared = PaymentService()
    }

    fun payments(orderNumber: String, payType: PayType): Single<PayMetadata> {
        return BatteryApi.service.payments(parameters = hashMapOf<String, Any>().apply {
            put("orderNumber", orderNumber)
            put("type", payType)
        }).unwrap()
    }

    fun alterOrderStatus(orderNumber: String): Single<Any> {
        return BatteryApi.service.alterOrderStatus(orderNumber = orderNumber).unwrap()
    }

    fun fetchOrderDetail(orderNumber: String): Single<OrderDetail> {
        return BatteryApi.service.fetchOrderDetail(orderNumber = orderNumber).unwrap()
    }
}