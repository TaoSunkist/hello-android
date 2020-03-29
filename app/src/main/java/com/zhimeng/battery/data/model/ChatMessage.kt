package com.zhimeng.battery.data.model

import com.google.gson.annotations.SerializedName
import com.mooveit.library.Fakeit
import com.zhimeng.battery.utilities.Debug

data class ChatMessageResponse(
    val list: List<ChatMessage>,
    val pageIndex: Int,
    val pageSize: Int,
    val pages: Int,
    val totalItems: Int
)

data class MediaResource(
    @SerializedName("size")
    val size: Int?,

    @SerializedName("thumbUrl")
    val thumbUrl: String?,

    @SerializedName("url")
    val url: String?,

    @SerializedName("voiceDuration")
    val voiceDuration: Int?,

    @SerializedName("thumbnailImageWidth")
    val thumbnailImageWidth: Int?,

    @SerializedName("thumbnailImageHeight")
    val thumbnailImageHeight: Int?,

    @SerializedName("allowDownload")
    val allowDownload: Boolean,

    @SerializedName("album")
    val album: Album?
)

data class Album(
    @SerializedName("photoCount")
    val photoCount: Int,

    @SerializedName("photoList")
    val photoList: List<Photo>
)

enum class ChatMessageType(val rawValue: Int, val value: String) {
    @SerializedName("TEXT")
    TEXT(0, "TEXT"),

    @SerializedName("VOICE")
    VOICE(1, "VOICE"),

    @SerializedName("IMAGE")
    IMAGE(2, "IMAGE"),

    @SerializedName("VIDEO")
    VIDEO(3, "VIDEO"),

    @SerializedName("ALBUM")
    ALBUM(4, "ALBUM");

    companion object {
        val random: ChatMessageType
            get() = arrayOf(TEXT, VOICE, IMAGE, ALBUM, VIDEO).random()

        fun valueOf(rawValue: Int): ChatMessageType {
            return when (rawValue) {
                0 -> TEXT
                1 -> VOICE
                2 -> IMAGE
                3 -> VIDEO
                4 -> ALBUM
                else -> TEXT
            }
        }
    }
}

interface ChatEntity

data class ChatTime(val time: Long) : ChatEntity

data class ChatMessage(
    @SerializedName("id") var messageId: Long,
    @SerializedName("isRead") var isRead: Boolean?,
    @SerializedName("isMe") var isMe: Boolean?,
    @SerializedName("readTimestamp") var readTimestamp: Long?,
    @SerializedName("type") var type: ChatMessageType,
    @SerializedName("content") var content: String?,
    @SerializedName("mediaResource") var mediaResource: MediaResource?,
    @SerializedName("createTime") var createTimestamp: Long
) : ChatEntity {
    companion object {

        private var currID: Long = 100000

        fun fake(type: ChatMessageType? = null, time: Long? = null): ChatMessage {
            val messageType = type ?: ChatMessageType.random
            val time = time ?: System.currentTimeMillis()
            currID += 1
            return ChatMessage(
                messageId = currID,
                isRead = false,
                readTimestamp = System.currentTimeMillis(),
                type = messageType,
                content = Fakeit.name().title() + " " + time,
                createTimestamp = time,
                mediaResource = null,
                isMe = arrayOf(true, false).random()
            )
        }

        fun init(type: ChatMessageType, content: String?, isMe: Boolean = true): ChatMessage {
            return ChatMessage(
                messageId = 0,
                isRead = true,
                readTimestamp = System.currentTimeMillis(),
                type = type,
                content = content,
                createTimestamp = System.currentTimeMillis(),
                mediaResource = null,
                isMe = isMe
            )
        }
    }
}

data class Photo(
    val url: String,
    val id: Long,
    val thumbUrl: String?,
    val isCollection: Boolean,
    val thumbnailImageWidth: Int,
    val thumbnailImageHeight: Int,
    val collectionCount: Int?
) {
    companion object {
        fun fake(url: String): Photo {
            return Photo(
                url = url,
                id = System.currentTimeMillis(),
                thumbUrl = Debug.images.random(),
                isCollection = listOf(true, false).random(),
                thumbnailImageWidth = 1,
                thumbnailImageHeight = 1,
                collectionCount = 1
            )
        }
    }

    val ratio: Float
        get() = thumbnailImageHeight.toFloat() / thumbnailImageWidth.toFloat()
}
