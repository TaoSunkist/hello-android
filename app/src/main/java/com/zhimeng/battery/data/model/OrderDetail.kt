package com.zhimeng.battery.data.model

data class OrderDetail(
    val amount: Int,
    val borrowAddress: String,
    val borrowStartTime: String,
    val borrowStatus: String,
    val chargingInfo: String,
    val createTimestamp: String,
    val duration: Int,
    val orderNumber: String,
    val paymentMethod: String,
    val paymentStatus: String,
    val returnAddress: String,
    val returnTime: String,
    val terminalId: String,
    val type: String
)