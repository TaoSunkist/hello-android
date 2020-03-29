package com.zhimeng.battery.network.model

import java.io.File

data class UploadProgressModel(val progress: Float)
data class DownloadProgressModel(val progress: Float, val file: File?) {
    val isComplete: Boolean
        get() = progress == 1.0f
}
