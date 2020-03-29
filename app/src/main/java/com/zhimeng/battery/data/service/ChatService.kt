package com.zhimeng.battery.data.service

import com.zhimeng.battery.BuildConfig
import com.zhimeng.battery.data.model.ChatEntity
import com.zhimeng.battery.data.model.ChatMessage
import com.zhimeng.battery.data.model.ChatTime
import com.zhimeng.battery.network.BatteryApi
import com.zhimeng.battery.network.model.BatteryResponse
import io.reactivex.Single
import kotlin.math.abs
import kotlin.random.Random


class ChatService {

    companion object {

        val shared = ChatService()
        const val pageSize = 20
        val useFake = false
    }

    /**
     * 抓取在特定时间以前的最多pageSize那么多的信息. 结果一定是按照时间从远到近排列.
     *
     * @param feedId 具体哪个明星
     * @param time 时间
     * @param pageSize 每页数量
     */
    fun fetchMessagesBefore(
        feedId: String,
        pageSize: Int = 20,
        pageIndex: Int = 1
    ): Single<List<ChatEntity>> {
        if (BuildConfig.DEBUGGING_MODE && useFake) {
            return Single.create {
                Thread.sleep(500)
                val result = mutableListOf<ChatMessage>()
                var currTime = System.currentTimeMillis()
                for (i in 0 until pageSize) {
                    currTime -= Random.nextLong(20000, 5000000)
                    result.add(ChatMessage.fake(time = currTime))
                }
                it.onSuccess(
                    result
                )
            }
        } else {
            return BatteryApi.service.fetchChatMessages(
                id = feedId,
                pageSize = pageSize,
                pageIndex = pageIndex
            ).map { tatameResponse ->
                val chatMessageList = populateTimeItems(tatameResponse.data?.list ?: arrayListOf())
                chatMessageList
            }
        }
    }

    /**
     * TODO 发送信息给服务器. 这版本不上
     */
    fun sendMessage(chatMessage: ChatMessage): Single<BatteryResponse<Any>> {
        throw NotImplementedError()
    }

    /**
     * 将信息标记为已读.
     */
    fun markMessageAsRead(feedId: String, messageId: Long): Single<BatteryResponse<Any>> {
        /* 告诉服务器端已读，但是并不在乎结果 */
        return BatteryApi.service.markMessageAsRead(
            code = feedId,
            messageId = messageId
        )
    }

/* 以下为添加时间model的逻辑 */

    private var beforeMessage: ChatMessage? = null

    private fun populateTimeItems(messageList: List<ChatMessage>): List<ChatEntity> {
        beforeMessage = null
        val chatMessages = arrayListOf<ChatEntity>()
        messageList.forEach { message ->
            getPaddingChatTimeMessage(beforeMessage = beforeMessage, message = message)?.let {
                chatMessages.add(it)
            }
            chatMessages.add(message)
            beforeMessage = message
        }
        getPaddingChatTimeMessage(beforeMessage = beforeMessage, message = null)?.let {
            chatMessages.add(it)
        }
        return chatMessages
    }

    private fun getPaddingChatTimeMessage(
        beforeMessage: ChatMessage?,
        message: ChatMessage?
    ): ChatTime? {
        if (beforeMessage == null) return null
        /* 需要将时间换算成秒**/

        val createTimestamp = message?.createTimestamp ?: System.currentTimeMillis()
        val beforeTimestamp = beforeMessage.createTimestamp
        var timeValue = abs(beforeTimestamp - createTimestamp)

        /*当两个消息差值大于5分钟后，需要和当前时间进行比较
          5分钟之内，两个消息是不需要插入时间cell**/
        if (timeValue > 5 * 60 * 1000 && message != null) {
            timeValue = System.currentTimeMillis() - beforeTimestamp
            if (timeValue < 5 * 60 * 1000) {
                return ChatTime(beforeTimestamp)
            }
        }
        if (timeValue > 5 * 60 * 1000) {
            return ChatTime(beforeTimestamp)
        }
        return null
    }
}