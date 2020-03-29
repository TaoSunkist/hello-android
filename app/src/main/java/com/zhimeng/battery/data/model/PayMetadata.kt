package com.zhimeng.battery.data.model

data class PayMetadata(
    val orderNumber: String?,
    val signData: String?
)

enum class PayType {
    /*交押金*/
    DEPOSIT,

    /*逾期补交*/
    OVERDUE,

    /*充值*/
    TOPUP
}