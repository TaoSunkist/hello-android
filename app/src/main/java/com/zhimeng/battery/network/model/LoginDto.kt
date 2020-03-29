package com.zhimeng.battery.network.model

import com.google.gson.annotations.SerializedName

data class LoginDto(@SerializedName("cellphone")
                    val cellphone: String,
                    @SerializedName("smsCode")
                    val smsCode: String)