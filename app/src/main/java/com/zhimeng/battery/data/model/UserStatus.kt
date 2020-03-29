package com.zhimeng.battery.data.model

import androidx.core.util.toRange
import com.google.gson.annotations.SerializedName
import com.mooveit.library.Fakeit

data class PaymentData(
    val borrowAddress: String?,
    val borrowStartTime: String?,
    val chargingInfo: String?,
    val cost: Int?,
    val depositAmount: Int?,
    val duration: Int?,
    val orderNumber: String?,
    val paymentMethod: PaymentMethod?,
    val returnAddress: String?,
    val returnTime: String?,
    val terminalId: String?,
    val walletBalance: Int?
)

enum class PaymentMethod {
    ALIPAY, BALANCE, DEPOSIT
}

enum class Status(val tag: String) {
    @SerializedName("USING")
    USING("USING"),

    @SerializedName("OVERDRAFT")
    OVERDRAFT("OVERDRAFT"),

    @SerializedName("FINISH")
    FINISH("FINISH"),

    /* 强制结算 */
    @SerializedName("OVERDUE_SETTLEMENT")
    OVERDUE_SETTLEMENT("OVERDUE_SETTLEMENT"),

    @SerializedName("NONE")
    NONE("NONE")
}

data class UsageData(
    val borrowAddress: String?,
    val borrowStartTime: String?,
    val chargingInfo: String?,
    val amount: Float?,
    val duration: Int?
)

data class UserStatus(
    val paymentData: PaymentData?,
    val status: Status?,
    val usageData: UsageData?
) {
    companion object {
        val TAG: String = "UserStatus"

        fun fake(): UserStatus {
            return UserStatus(
                paymentData = PaymentData(
                    cost = (5..10).random(),
                    duration = (100..500).random(),
                    orderNumber = Fakeit.code().toString(),
                    depositAmount = (10..50).random(),
                    paymentMethod = PaymentMethod.values().random(),
                    walletBalance = (10..50).random(),
                    borrowAddress = Fakeit.address().streetAddress(),
                    borrowStartTime = Fakeit.dateTime().dateTimeFormatter(),
                    chargingInfo = "",
                    returnAddress = "",
                    returnTime = "",
                    terminalId = ""
                ),
                status = Status.values().random(),
                usageData = UsageData(
                    borrowAddress = Fakeit.address().streetAddress(),
                    borrowStartTime = Fakeit.dateTime().dateTimeFormatter(),
                    chargingInfo = Fakeit.ancient().hero(),
                    amount = (1..5).random().toFloat(),
                    duration = (1000..10000).random()
                )
            )
        }
    }
}