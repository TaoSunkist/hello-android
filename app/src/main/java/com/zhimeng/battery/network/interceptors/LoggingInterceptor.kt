package com.zhimeng.battery.network.interceptors

import com.zhimeng.battery.utilities.printfNetwork
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException

internal class LoggingInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val t1 = System.nanoTime()
        printfNetwork(String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers()))

        val response = chain.proceed(request)

        val t2 = System.nanoTime()
        printfNetwork(String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6, response.headers()))

        val rawJson = response.body()!!.string()

        try {
            val `object` = JSONTokener(rawJson).nextValue()
            val jsonLog = if (`object` is JSONObject)
                `object`.toString(4)
            else
                (`object` as JSONArray).toString(4)
            printfNetwork("Raw Response:")
            printfNetwork(jsonLog)
        } catch (e: JSONException) {
            printfNetwork("Raw JSON $rawJson")
        }

        // Re-create the response before returning it because body can be read only once
        return response.newBuilder()
                .body(ResponseBody.create(response.body()!!.contentType(), rawJson)).build()

    }
}