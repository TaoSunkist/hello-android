package com.zhimeng.battery.data.voice

import android.net.Uri

/**
 * 播放语音所需的Model.
 */
data class VoiceModel(
    val id: Long,
    val url: String?,
    val uri: Uri?,
    val voiceConfig: VoiceConfig
) {
    companion object {

        /**
         * 产生一个非本地的语音.
         */
        fun create(
            id: Long,
            url: String,
            voiceConfig: VoiceConfig = VoiceConfig.init(false)
        ): VoiceModel {
            return VoiceModel(id = id, url = url, uri = null, voiceConfig = voiceConfig)
        }

        /**
         * 产生一个本地语音.
         */
        fun create(
            id: Long,
            uri: Uri,
            voiceConfig: VoiceConfig = VoiceConfig.init(false)
        ): VoiceModel {
            return VoiceModel(id = id, url = null, uri = uri, voiceConfig = voiceConfig)
        }
    }
}

data class VoiceConfig(val isLooping: Boolean) {
    companion object {
        fun init(isLooping: Boolean = false): VoiceConfig {
            return VoiceConfig(isLooping)
        }
    }
}

enum class VoiceStatusState {
    /* 无状态 */
    NONE,
    /* 正在下载/读取中 */
    LOADING,
    /* 正在播放中 */
    PLAYING
}


data class VoiceStatus(
    val id: Long,
    val state: VoiceStatusState,
    val progress: Float
) {
    companion object {
        val IDLE = VoiceStatus(id = -1, state = VoiceStatusState.NONE, progress = 0f)
    }

    val isLoading: Boolean = state == VoiceStatusState.LOADING
    val isPlaying: Boolean = state == VoiceStatusState.PLAYING
}