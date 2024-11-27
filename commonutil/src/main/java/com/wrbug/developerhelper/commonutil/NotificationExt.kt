package com.wrbug.developerhelper.commonutil


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat


fun notifyNotification(
    context: Context,
    channelId: String,
    channelName: String,
    notificationId: Int,
    block: NotificationCompat.Builder.() -> Unit
) {
    val notification = createNotification(context, channelId, channelName, block)
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.notify(notificationId, notification)
}


fun createNotification(
    context: Context,
    channelId: String,
    channelName: String,
    block: NotificationCompat.Builder.() -> Unit
): Notification {
    val builder = NotificationCompat.Builder(context, channelId)
    block.invoke(builder)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        createNotificationChannel(context, channelId, channelName)
    }
    return builder.build()
}

@RequiresApi(Build.VERSION_CODES.O)
private fun createNotificationChannel(
    context: Context,
    channelId: String,
    channelName: String
): NotificationChannel {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channel =
        NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        notificationManager.createNotificationChannel(channel)
    }
    return channel
}