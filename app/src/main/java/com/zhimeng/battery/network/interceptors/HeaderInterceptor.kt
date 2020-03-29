package com.zhimeng.battery.network.interceptors

import android.os.Build
import androidx.multidex.BuildConfig
import com.zhimeng.battery.data.UserStore
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.*

class HeaderInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (userAgent == null) {
            userAgent = String.format(
                "Tatame %s %s %s %s (%d)",
                BuildConfig.VERSION_NAME,
                Build.MANUFACTURER,
                Build.MODEL,
                Build.VERSION.CODENAME,
                Build.VERSION.SDK_INT
            )
        }

        val request = chain.request()
        val builder = request.newBuilder()

        val token =
            "3728e377255a41db88627b53dbdac4e85f81755a31f3c4788472c8c13a21d5d7"//UserStore.shared.authenticationToken
        if (token != null) {
//            builder.addHeader("Authorization", token)
            builder.addHeader(
                "userToken", token
            )
        }
        builder.addHeader("Content-Type", "application/json")
            .addHeader("app-version", BuildConfig.VERSION_NAME)
            .removeHeader("User-Agent")
            .addHeader("User-Agent", userAgent!!)
            .addHeader("Device-Name", Build.MODEL)
            .addHeader("client-platform", "ANDROID")
            .addHeader(
                "Accept-Language",
                Locale.getDefault().language + "-" + Locale.getDefault().country
            )
            .build()
        return chain.proceed(builder.build())
    }

    companion object {
        private var userAgent: String? = null
    }
}
