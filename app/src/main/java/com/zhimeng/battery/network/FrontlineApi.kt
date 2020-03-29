package com.zhimeng.battery.network

import android.content.Context
import com.jakewharton.rxrelay2.BehaviorRelay
import com.zhimeng.battery.BuildConfig
import com.zhimeng.battery.network.interceptors.ErrorInterceptor
import com.zhimeng.battery.network.interceptors.HeaderInterceptor
import com.zhimeng.battery.network.interceptors.LoggingInterceptor
import com.zhimeng.battery.network.model.DownloadProgressModel
import com.zhimeng.battery.network.model.BatteryResponse
import com.zhimeng.battery.network.model.validate
import com.zhimeng.battery.utilities.printf
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

fun <T : Any> Single<BatteryResponse<T>>.unwrap(validateResult: Boolean = true): Single<T> {
    return map {
        val data = it.data
        if (validateResult) {
            validate(obj = data)
        }
        data ?: throw IOException("服务器数据异常")
    }
}


class BatteryApi(context: Context) {
    companion object {
        private const val PREF_SERVER: String = "prefServer"
        private const val KEY_SELECTED_SERVER: String = "keySelectedServer"

        private const val TIMEOUT_DEBUG: Long = 10000
        private const val TIMEOUT_PROD: Long = 60000

        lateinit var sharedInstance: BatteryApi

        val service: BatteryService
            get() = sharedInstance.service!!


        fun init(context: Context) {
            sharedInstance = BatteryApi(context = context)
        }
    }

    private var service: BatteryService? = null
    val selectedServerKey = BehaviorRelay.createDefault(kProductionServer)
    val selectedServerInfo: BatteryServerInfo
        get() = availableServers[selectedServerKey.value]!!

    init {
        var storedServerKey = context.getSharedPreferences(
            PREF_SERVER,
            Context.MODE_PRIVATE
        ).getString(KEY_SELECTED_SERVER, kProductionServer)!!
        if (BuildConfig.ALLOW_SERVER_SWITCH.not()) {
            storedServerKey = kProductionServer
        }
        selectedServerKey.accept(storedServerKey)
        setupServer(serverInfoInfo = availableServers[selectedServerKey.value]!!)
    }

    fun switchToServer(context: Context, serverInfo: BatteryServerInfo) {
        if (serverInfo.key == selectedServerKey.value) {
            return
        }
        selectedServerKey.accept(serverInfo.key)
        setupServer(serverInfo)

        context.getSharedPreferences(PREF_SERVER, Context.MODE_PRIVATE)
            .edit().putString(KEY_SELECTED_SERVER, selectedServerKey.value)
            .apply()
    }

    private fun setupServer(serverInfoInfo: BatteryServerInfo) {
        if (BuildConfig.DEBUGGING_MODE) {
            printf("========================")
            printf("Switch to Server: ${serverInfoInfo.key}")
            printf("Server URL      : ${serverInfoInfo.baseUrl}")
            printf("========================")
        }

        val timeout: Long = if (BuildConfig.DEBUGGING_MODE) TIMEOUT_DEBUG else TIMEOUT_PROD
        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.addInterceptor(HeaderInterceptor())
            .addInterceptor(ErrorInterceptor())
            .connectTimeout(timeout, TimeUnit.MILLISECONDS)
        if (BuildConfig.DEBUGGING_MODE) {
            clientBuilder.addInterceptor(LoggingInterceptor())
        }
        val retrofit = Retrofit.Builder()
            .baseUrl(serverInfoInfo.baseUrl)
            .client(clientBuilder.build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(BatteryService::class.java)
    }

}

/**
 * 下载某个url的内容到target.
 */
fun download(url: String, target: File): Observable<DownloadProgressModel> {
    return Observable.create {

        val okHttpClient = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response: Response
        try {
            response = okHttpClient.newCall(request).execute()
        } catch (e: IOException) {
            if (it.isDisposed.not()) {
                it.onError(e)
            }
            return@create
        }

        val ins = response.body()?.byteStream()
        val input = BufferedInputStream(ins)
        val output = FileOutputStream(target)
        var shouldCancel = false

        it.setCancellable {
            shouldCancel = true
        }

        var data = ByteArray(2048)
        var total = 0
        var count = input.read(data)
        while (count != -1) {
            if (shouldCancel) {
                break
            }
            val progress = total.toFloat() / (response.body()?.contentLength() ?: 1).toFloat()
            it.onNext(DownloadProgressModel(progress = progress, file = null))
            total += count
            try {
                output.write(data, 0, count)
                count = input.read(data)
            } catch (e: IOException) {
                if (it.isDisposed.not()) {
                    it.onError(e)
                }
                break
            }
        }

        try {
            output.flush()
            output.close()
            input.close()
            it.onNext(DownloadProgressModel(progress = 1f, file = target))
            it.onComplete()
        } catch (e: IOException) {
            if (it.isDisposed.not()) {
                it.onError(e)
            }
        }
    }
}