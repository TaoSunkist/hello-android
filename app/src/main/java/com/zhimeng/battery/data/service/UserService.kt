package com.zhimeng.battery.data.service

import com.zhimeng.battery.BuildConfig
import com.zhimeng.battery.data.model.ExampleLoginInfo
import com.zhimeng.battery.data.model.UserStatus
import com.zhimeng.battery.network.BatteryApi
import com.zhimeng.battery.network.model.BatteryResponse
import com.zhimeng.battery.network.model.LoginDto
import com.zhimeng.battery.network.model.UserResponse
import com.zhimeng.battery.network.unwrap
import io.reactivex.Single
import com.zhimeng.battery.data.model.ExampleLoginInfo.Companion as ExampleLoginInfo1

class UserService {

    companion object {
        val shared = UserService()
        var useFake = true
    }

    fun login(
        cellphone: String
    ): Single<UserResponse> {
        if (BuildConfig.DEBUGGING_MODE && useFake) {
            return Single.create {
                Thread.sleep(500)
                it.onSuccess(UserResponse.fake())
            }
        } else {
            return BatteryApi.service.login(LoginDto(cellphone = cellphone, smsCode = "")).unwrap()
        }
    }

    fun logout(): Single<BatteryResponse<Any>> {
        if (BuildConfig.DEBUGGING_MODE && useFake) {
            return Single.create {
                Thread.sleep(500)
                it.onSuccess(BatteryResponse.success(1))
            }
        } else {
            return BatteryApi.service.logout()
        }
    }

    fun getVerificationCode(cellphone: String): Single<BatteryResponse<UserResponse>> {
        if (BuildConfig.DEBUGGING_MODE && useFake) {
            return Single.create {
                Thread.sleep(500)
                it.onSuccess(BatteryResponse.success(UserResponse.fake()))
            }
        } else {
            return BatteryApi.service.getVerificationCode(cellphone)
        }
    }

    fun fetchUserInfo(): Single<UserResponse> {
        if (BuildConfig.DEBUGGING_MODE && useFake) {
            return Single.create {
                Thread.sleep(500)
                it.onSuccess(UserResponse.fake())
            }
        } else {
            return BatteryApi.service.fetchUserObject().unwrap()
        }
    }


    fun updateUserNickname(nickname: String): Single<BatteryResponse<UserResponse>> {
        if (BuildConfig.DEBUGGING_MODE && useFake) {
            return Single.create {
                Thread.sleep(500)
                it.onSuccess(BatteryResponse.success(UserResponse.fake()))
            }
        } else {
            return BatteryApi.service.updateUser(
                HashMap<String, String>().apply {
                    put("nickname", nickname)
                })
        }
    }

    fun updateUserPicture(imageName: String, imageUrl: String): Single<UserResponse> {
        return if (BuildConfig.DEBUGGING_MODE && useFake) {
            Single.create {
                Thread.sleep(500)
                it.onSuccess(UserResponse.fake())
            }
        } else {
            BatteryApi.service.updateUser(HashMap<String, String>().apply {
                put("profilePictureName", imageName)
                put("profilePictureUrl", imageUrl)
            }).unwrap()
        }
    }

    fun sendMessage(id: String, message: String): Single<BatteryResponse<Any>> {
        return BatteryApi.service.sendMessages(
            id = id,
            parameters = HashMap<String, Any>().apply {
                put("content", message)
                put("recoveryTime", System.currentTimeMillis())
            })
    }

    fun usersStatus(): Single<UserStatus> {
//        return if (BuildConfig.DEBUGGING_MODE && useFake) {
//            Single.create {
//                Thread.sleep(500)
//                it.onSuccess(UserStatus.fake())
//            }
//        } else {
        return BatteryApi.service.usersStatus().unwrap()
//        }
    }

    fun fakeExampleLoginInfo(): ArrayList<ExampleLoginInfo> {
        return ExampleLoginInfo.fake()
    }
}
