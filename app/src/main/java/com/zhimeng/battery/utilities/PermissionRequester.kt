package com.zhimeng.battery.utilities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import io.reactivex.Single

interface PermissionGranter {
    fun requestPermission(permission: String, resultHandler: (Int) -> Unit)
}

class SystemPermissionDenied(val permission: String) : SecurityException()

object PermissionRequester {

    private fun get(context: Context, permission: String): Single<Boolean> {
        return Single.create { subscriber ->
            val granter = context as PermissionGranter
            val granted = ContextCompat.checkSelfPermission(context, permission)
            if (granted == PackageManager.PERMISSION_GRANTED) {
                subscriber.onSuccess(true)
            } else {
                granter.requestPermission(permission) { result ->
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        subscriber.onSuccess(true)
                    } else {
                        subscriber.onError(SystemPermissionDenied(permission))
                    }
                }
            }
        }
    }

    fun getContact(c: AppCompatActivity): Single<Boolean> {
        return get(c, Manifest.permission.READ_CONTACTS)
    }

    fun getCamera(c: AppCompatActivity): Single<Boolean> {
        return get(c, Manifest.permission.CAMERA)
    }

    fun getReadExternalStorage(c: AppCompatActivity): Single<Boolean> {
        return get(c, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    fun getWriteExternalStorage(c: AppCompatActivity): Single<Boolean> {
        return get(c, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    fun getReadPhoneState(c: AppCompatActivity): Single<Boolean> {
        return get(c, Manifest.permission.READ_PHONE_STATE)
    }
    fun getCallPhone(c: AppCompatActivity): Single<Boolean> {
        return get(c, Manifest.permission.CALL_PHONE)
    }
}