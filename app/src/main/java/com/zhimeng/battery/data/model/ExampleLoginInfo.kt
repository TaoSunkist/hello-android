package com.zhimeng.battery.data.model

import com.zhimeng.battery.network.model.UserResponse

data class ExampleLoginInfo(val userResponse: UserResponse, val userStatus: UserStatus) {
    companion object {
        fun fake(): ArrayList<ExampleLoginInfo> {
            return arrayListOf<ExampleLoginInfo>().apply {
                for (i in 0..6) {
                    add(
                        ExampleLoginInfo(
                            userResponse = UserResponse.fake(),
                            userStatus = UserStatus.fake()
                        )
                    )
                }
            }
        }
    }
}