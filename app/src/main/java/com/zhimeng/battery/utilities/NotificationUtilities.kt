package com.zhimeng.battery.utilities

import java.util.concurrent.atomic.AtomicInteger

/**
 * @see com.zhimeng.battery.utilities.alarm.AlarmReceiver
 * @see com.zhimeng.battery.utilities.PushService
 * 用于防止Notification因为id相同被覆盖的问题
 **/
val NOTIFICATION_MAX_ID: AtomicInteger = AtomicInteger(Int.MAX_VALUE)
