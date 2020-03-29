package com.zhimeng.battery.app

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.multidex.MultiDexApplication
import com.google.gson.Gson
import com.jakewharton.rxrelay2.BehaviorRelay
import com.mooveit.library.Fakeit
import com.zhimeng.battery.data.UserStore
import com.zhimeng.battery.data.aws3.AWS3Utility
import com.zhimeng.battery.network.BatteryApi
import com.zhimeng.battery.utilities.weak
import es.dmoral.toasty.Toasty
import io.reactivex.disposables.CompositeDisposable

class BatteryApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        AWS3Utility.awsSDKInit(this)
        BatteryApi.init(this)
        Fakeit.init()
        CONTEXT = this
        UserStore.init(this)
//        AppDataStore.Companion.init(this)
        Toasty.Config.getInstance()
            .tintIcon(true) // optional (apply textColor also to the icon)
            .setTextSize(14) // optional
            .allowQueue(true) // optional (prevents several Toastys from queuing)
            .apply() // required
    }

    companion object {
        private val TAG = BatteryApplication::class.java.name
        val GSON = Gson()
        val MAIN_HANDLER = Handler(Looper.getMainLooper())
        var CONTEXT: Context? by weak()
        /* 正在前台的Activity总数，如果 >0 就代表app在foreground */
        var activeActivityCount = BehaviorRelay.createDefault(0)
        var appCompositeDisposable = CompositeDisposable()
    }
}
