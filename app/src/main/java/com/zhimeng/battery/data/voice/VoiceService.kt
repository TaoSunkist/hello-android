package com.zhimeng.battery.data.voice

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.zhimeng.battery.app.BatteryApplication
import com.zhimeng.battery.app.TatameFileManager
import com.zhimeng.battery.network.download
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.math.max

class VoiceService {

    companion object {
        val shared = VoiceService().apply {
            voiceStatusRelay.accept(VoiceStatus.IDLE)
        }
    }

    private var player: MediaPlayer? = null
    private var timer: Timer? = null
    private var compositeDisposable = CompositeDisposable().apply {
        BatteryApplication.appCompositeDisposable.add(this)
    }

    val currentPlayingId: Long?
        get() = model?.id

    val isPlaying: Boolean
        get() = player?.isPlaying ?: false

    var model: VoiceModel? = null
    val voiceStatusRelay = BehaviorRelay.create<VoiceStatus>()
    val voiceErrorRelay = PublishRelay.create<Throwable>()

    fun playVoice(context: Context, model: VoiceModel) {
        stopAll()
        this.model = model

        val uri = model.uri
        val url = model.url
        if (uri != null) {
            play(context, uri)
        } else if (url != null) {
            voiceStatusRelay.accept(
                VoiceStatus(
                    id = model.id,
                    state = VoiceStatusState.LOADING,
                    progress = 0f
                )
            )
            val target = TatameFileManager.getTempVoiceFile(context)
            download(url = url, target = target)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                }, {
                    voiceErrorRelay.accept(it)
                    voiceStatusRelay.accept(VoiceStatus.IDLE)
                }, {
                    play(context, Uri.fromFile(target))
                }).addTo(compositeDisposable)
        }
    }

    private fun play(context: Context, uri: Uri) {
        val player = MediaPlayer.create(context, uri)
        player.setOnCompletionListener {
            if (model?.voiceConfig?.isLooping == true) {
                play(context, model?.uri!!)
            } else {
                stopAll()
            }
        }
        player.start()

        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                try {
                    onAudioPlaying()
                } catch (e: IllegalStateException) {

                }
            }
        }, 0, 100)

        this.player = player
        this.timer = timer
    }

    private fun onAudioPlaying() {
        val currentTime = player?.currentPosition
        val duration = player?.duration
        val model = this.model
        if (currentTime != null && duration != null && model != null) {
            val progress = currentTime.toFloat() / max(1f, duration.toFloat())
            voiceStatusRelay.accept(
                VoiceStatus(
                    id = model.id,
                    state = VoiceStatusState.PLAYING,
                    progress = progress
                )
            )
        }
    }

    fun stopAll() {
        compositeDisposable.clear()
        player?.setOnCompletionListener(null)
        try {
            player?.stop()
        } catch (e: IllegalStateException) {

        } finally {
            player?.release()
        }
        player = null
        timer?.cancel()
        timer = null
        voiceStatusRelay.accept(VoiceStatus.IDLE)
        model = null
    }
}