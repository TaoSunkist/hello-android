package com.zhimeng.battery.network

import com.zhimeng.battery.data.model.*
import com.zhimeng.battery.data.service.FeedListType
import com.zhimeng.battery.network.model.*
import io.reactivex.Single
import retrofit2.http.*

interface BatteryService {

    @POST("boot/v1.0.0/login")
    fun login(@Body loginDto: LoginDto): Single<BatteryResponse<UserResponse>>

    @GET("boot/v1.0.0/login/SMSCode")
    fun getVerificationCode(@Query("cellphone") cellPhone: String): Single<BatteryResponse<UserResponse>>

    @POST("boot/v1.0.0/login/logout")
    fun logout(): Single<BatteryResponse<Any>>

    @GET("boot/v1.0.0/chargerBoxes/{boxid}")
    fun chargerBoxes(@Path("boxid") boxid: Long): Single<BatteryResponse<UserResponse>>

    @PATCH("boot/v1.0.0/users")
    fun updateUser(@Body userDto: HashMap<String, String>): Single<BatteryResponse<UserResponse>>

    /* 获取爱豆列表 */
    @GET("boot/v1.0.0/feeds")
    fun fetchFeeds(
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int,
        @Query("type") type: FeedListType
    ): Single<BatteryResponse<FeedResponse>>

    /* 关注/取消关注偶像 */
    @POST("boot/v1.0.0/feeds/{code}/follows")
    fun follows(@Path("code") id: String): Single<BatteryResponse<Any>>

    /* 获取爱豆列表 */
    @GET("boot/v1.0.0/favorites")
    fun fetchFavorites(
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int
    ): Single<BatteryResponse<FavoriteResponse>>

    /* 收藏/取消收藏偶像图片 */
    @POST("boot/v1.0.0/favorites")
    fun likePhoto(@Body parameters: HashMap<String, Any>): Single<BatteryResponse<Any>>

    @PUT("boot/v1.0.0/avatars/{code}/messages/{messageId}/readStatus")
    fun markMessageAsRead(
        @Path("code") code: String,
        @Path("messageId") messageId: Long
    ): Single<BatteryResponse<Any>>

    @GET("boot/v1.0.0/feeds/{code}/messages")
    fun fetchChatMessages(
        @Path("code") id: String,
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int
    ): Single<BatteryResponse<ChatMessageResponse>>

    @POST("boot/v1.0.0/feeds/{code}/messages")
    fun sendMessages(
        @Path("code") id: String,
        @Body parameters: HashMap<String, Any>
    ): Single<BatteryResponse<Any>>

    @GET("api/v1.0.0/users/status")
    fun usersStatus(): Single<BatteryResponse<UserStatus>>

    @GET("api/v1.0.0/login")
    fun fetchUserObject(): Single<BatteryResponse<UserResponse>>

    @GET("api/v1.0.0/staions/{boxId}")
    fun stationsBoxid(@Path("boxId") boxid: String): Single<BatteryResponse<BoxInfomation>>

    @GET("api/v1.0.0/orders/{orderNumber}")
    fun fetchOrderDetail(@Path("orderNumber") orderNumber: String): Single<BatteryResponse<OrderDetail>>

    @POST("api/v1.0.0/staions/{boxId}/borrowing")
    fun borrowing(@Path("boxId") boxId: String): Single<BatteryResponse<Borrowing>>

    @POST("api/v1.0.0/payments")
    fun payments(@Body parameters: HashMap<String, Any>): Single<BatteryResponse<PayMetadata>>

    /* /v1.0.0/payments/{orderNumber} */
    @PUT("api/v1.0.0/payments/{orderNumber}")
    fun alterOrderStatus(@Path("orderNumber") orderNumber: String): Single<BatteryResponse<Any>>
}
