package com.zhimeng.battery.network.interceptors

import com.zhimeng.battery.app.BatteryApplication
import com.zhimeng.battery.data.UserStore
import com.zhimeng.battery.network.model.BatteryErrorResponse
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 封装了包含我们的服务器error json的IOException.
 */
class BatteryException(val errorResponse: BatteryErrorResponse) : IOException() {

    companion object {
        const val NOT_AUTHENTICATED = 2008
        const val OPEN_BOX_LIMIT_REACHED = 7009
        /** @see com.zhimeng.tatame.ui.home.HomeViewController.fetchLottery */
        const val OPEN_BOX_DIAMOND_NOT_ENOUGH = 7008
    }

    override fun getLocalizedMessage(): String? {
        return errorResponse.error?.message
    }

    override val message: String?
        get() = errorResponse.error?.message

    val serverStatusCode: Int
        get() = errorResponse.error?.code ?: -1
}

internal class ErrorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response: Response
        try {
            /* 如果网络请求失败，proceed会throw一个IOException，我们想要一个
               用我们自己的error message的，所以catch到以后再throw出去.
             */
            response = chain.proceed(request)
        } catch (exception: IOException) {
            throw IOException("网络请求失败了，请稍后重试")
        }
        /* 如果服务器给了我们错误，我们需要parse一下服务器给的json，同时扔一个包含我们的服务器的error json的
           专门的IOException.
         */
        if (response.code() in 400..500) {
            /* only care about error */
            val rawJson = response.body()!!.string()
            val errorResponse: BatteryErrorResponse?
            try {
                errorResponse = BatteryApplication.GSON.fromJson(rawJson, BatteryErrorResponse::class.java)
            } catch(error: Exception) {
                throw IOException("服务器数据异常")
            }
            if (errorResponse.error?.code == BatteryException.NOT_AUTHENTICATED) {
                UserStore.shared.logout()
            }

            throw BatteryException(errorResponse = errorResponse)
        } else {
            /* 没错误就原本的返回就好了 */
            return response
        }
    }
}
