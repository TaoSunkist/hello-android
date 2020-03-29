package com.zhimeng.battery.data

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.jakewharton.rxrelay2.BehaviorRelay
import com.zhimeng.battery.app.BatteryApplication
import com.zhimeng.battery.data.model.User
import com.zhimeng.battery.data.model.UserStatus
import com.zhimeng.battery.data.service.UserService
import com.zhimeng.battery.network.model.UserResponse
import com.zhimeng.battery.utilities.observeOnMainThread
import com.zhimeng.battery.utilities.printf
import io.reactivex.Single

enum class UserObjectEventType {
    LOGIN,
    REFRESH,
    LOGOUT
}

data class UserObjectEvent(
    val user: User?,
    val timestamp: Long,
    val type: UserObjectEventType
) {
    val isAuthenticated: Boolean
        get() = user != null
}

class UserStore internal constructor(context: Context) {

    companion object {

        const val PREF_USER = "prefUserV2"
        private const val KEY_USER = "keyUserV2"
        private const val KEY_AUTH_TOKEN = "keyAuthTokenV2"
        private const val KEY_STATUS = "keyStatusV2"

        lateinit var shared: UserStore

        fun init(context: Context) {
            shared = UserStore(context)
        }
    }

    var user: User? = null
        private set

    var authenticationToken: String? = null
        private set

    val isAuthenticated: Boolean
        get() {
            return user != null && authenticationToken?.isNotEmpty() == true
        }

    val userObjectRelay = BehaviorRelay.create<UserObjectEvent>()

    init {
        val sp = context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE)
        val rawUser = sp.getString(KEY_USER, null)
        val authToken = sp.getString(KEY_AUTH_TOKEN, null)
        if (rawUser == null || authToken == null) {
            /* Not logged in */
        } else {
            try {
                val decodedUser = BatteryApplication.GSON.fromJson(rawUser, User::class.java)
                user = decodedUser
                authenticationToken = authToken
                when {
                    user == null -> {
                        authenticationToken = null
                    }
                    authenticationToken == null -> {
                        user = null
                    }
                }
            } catch (e: Exception) {
                /* Cannot decode json, consider as not logged in */
            }
        }
        userObjectRelay.accept(
            UserObjectEvent(
                user = user,
                timestamp = System.currentTimeMillis(),
                type = UserObjectEventType.REFRESH
            )
        )
    }

    private fun persistUserObject() {
        user?.let { user ->
            val rawUser = BatteryApplication.GSON.toJson(user)
            val editor =
                BatteryApplication.CONTEXT!!.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE)
                    .edit()
            editor.putString(KEY_USER, rawUser)
            editor.putString(KEY_AUTH_TOKEN, authenticationToken)
            editor.apply()

            printf("Successfully persisted $user")
        }
    }

    private fun requestUserUpdate(): Single<UserResponse> {
        val single = UserService.shared.fetchUserInfo()
        single.observeOnMainThread(onSuccess = {
            updateUser(user = it.user, token = it.token, type = UserObjectEventType.REFRESH)
        }, onError = {
            /* Ignore */
        })
        return single
    }

    fun updateUser(
        userResponse: UserResponse,
        type: UserObjectEventType = UserObjectEventType.LOGIN
    ) {
        updateUser(
            user = userResponse.user,
            token = userResponse.token,
            type = type,
            shouldSilence = false
        )
    }

    fun updateUser(
        user: User?,
        token: String? = null,
        type: UserObjectEventType,
        shouldSilence: Boolean = false
    ) {
        this.user = user
        if (token != null) {
            this.authenticationToken = token
        }
        persistUserObject()
        if (shouldSilence.not()) {
            this.user?.let {
                userObjectRelay.accept(
                    UserObjectEvent(
                        user = it,
                        timestamp = System.currentTimeMillis(),
                        type = type
                    )
                )
            }
        }
    }

    /* 刷新本地的用户状态 */
    fun requestUserStatusUpdate(): Single<UserStatus> {
        val single = UserService.shared.usersStatus()
        single.observeOnMainThread(onSuccess = {
            user?.userStatus = it
            updateUser(user = user, type = UserObjectEventType.REFRESH)
        }, onError = {
            /* Ignore */
        })
        return single
    }


    fun logout() {

        val notificationManagerCompat = NotificationManagerCompat.from(BatteryApplication.CONTEXT!!)
        notificationManagerCompat.cancelAll()

        val editor =
            BatteryApplication.CONTEXT!!.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE)
                .edit()
        editor.remove(KEY_USER)
        editor.remove(KEY_AUTH_TOKEN)
        editor.clear()
        editor.apply()
        user = null
        authenticationToken = null
        userObjectRelay.accept(
            UserObjectEvent(
                user = null,
                timestamp = System.currentTimeMillis(),
                type = UserObjectEventType.LOGOUT
            )
        )
    }

}
