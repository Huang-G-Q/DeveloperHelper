package com.wrbug.developerhelper.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.view.LayoutInflater
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.wrbug.developerhelper.R
import com.wrbug.developerhelper.constant.ReceiverConstant
import com.wrbug.developerhelper.commonutil.shell.Callback
import com.wrbug.developerhelper.commonutil.shell.ShellManager
import com.wrbug.developerhelper.commonwidget.util.setOnDoubleCheckClickListener
import com.wrbug.developerhelper.ui.activity.main.MainActivity
import com.yhao.floatwindow.FloatWindow
import com.yhao.floatwindow.Screen

class FloatWindowService: Service() {

    companion object {

        const val FLOAT_BUTTON = "floatButton"
        private const val CHANNEL_ID = "DEMON"
        fun start(context: Context) {
            context.startService(Intent(context, FloatWindowService::class.java))
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, FloatWindowService::class.java))
        }

        fun setFloatButtonVisible(context: Context, visible: Boolean) {
            val intent = Intent(ReceiverConstant.ACTION_SET_FLOAT_BUTTON_VISIBLE)
            intent.putExtra("visible", visible)
            context.sendBroadcast(intent)
        }
    }

    private val floatCustomView: RemoteViews by lazy {
        RemoteViews(packageName, R.layout.view_float_custom).apply {
            setOnClickPendingIntent(
                R.id.adbWifiContainer, PendingIntent.getBroadcast(
                    applicationContext,
                    0,
                    Intent(ReceiverConstant.ACTION_ADB_WIFI_CLICKED),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        }
    }
    private lateinit var notification: Notification
    private val receiver = Receiver()
    override fun onCreate() {
        super.onCreate()
        initReceiver()
        LayoutInflater.from(this).inflate(R.layout.layout_float_window_button, null)?.let {
            it.setOnDoubleCheckClickListener {
                if (!DeveloperHelperAccessibilityService.isAccessibilitySettingsOn()) {
                    AccessibilityManager.startService(this, object: Callback<Boolean> {
                        override fun onSuccess(data: Boolean) {
                            if (data) {
                                it.postDelayed({
                                    sendBroadcast(Intent(ReceiverConstant.ACTION_HIERARCHY_VIEW))
                                }, 500)
                            }
                        }

                    })
                    return@setOnDoubleCheckClickListener
                }
                sendBroadcast(Intent(ReceiverConstant.ACTION_HIERARCHY_VIEW))
            }
            FloatWindow
                .with(applicationContext)
                .setView(it)
                .setWidth(Screen.width, 0.1f)                               //设置控件宽高
                .setHeight(Screen.width, 0.1f)
                .setY(Screen.height, 0.3f)
                .setTag(FLOAT_BUTTON)
                .setDesktopShow(true)                        //桌面显示
                .build()

        }
    }

    private fun updateNotification() {
        startForeground(0x10000, notification)
    }

    private fun updateNotificationContent(text: String) {
        floatCustomView.setTextViewText(R.id.contentTv, text)
        updateNotification()
    }

    private fun updateNotificationWifi(id: Int) {
        floatCustomView.setImageViewResource(R.id.adbWifiIv, id)
        updateNotification()
    }

    private fun initNotification() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.demon_process),
                    NotificationManager.IMPORTANCE_LOW
                )
            channel.enableLights(true)
            channel.setShowBadge(true)
            notificationManager.createNotificationChannel(channel)
        }
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        }
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.demon_process_content))
            .setSmallIcon(R.drawable.ic_launcher_notify)
            .setVibrate(null)
        notification = builder.build()
        notification.flags = Notification.FLAG_ONGOING_EVENT or Notification.FLAG_NO_CLEAR or
                Notification.FLAG_FOREGROUND_SERVICE
        updateNotification()
    }

    private fun showFloatButton() {
        FloatWindow.get(FLOAT_BUTTON).show()
    }

    private fun hideFloatButton() {
        FloatWindow.get(FLOAT_BUTTON).hide()
    }

    private fun initReceiver() {
        val filter = IntentFilter(ReceiverConstant.ACTION_SET_FLOAT_BUTTON_VISIBLE)
        filter.addAction(ReceiverConstant.ACTION_ADB_WIFI_CLICKED)
        registerReceiver(receiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initNotification()
        showFloatButton()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        try {
            FloatWindow.destroy(FLOAT_BUTTON)
            unregisterReceiver(receiver)
            stopForeground(true)
        } catch (th: Throwable) {

        }

        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder = null!!

    private inner class Receiver: BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ReceiverConstant.ACTION_SET_FLOAT_BUTTON_VISIBLE -> {
                    val visible = intent.getBooleanExtra("visible", false)
                    if (visible) {
                        showFloatButton()
                    } else {
                        hideFloatButton()
                    }
                }
                ReceiverConstant.ACTION_ADB_WIFI_CLICKED -> {
                    updateNotificationContent("正在开启adb wifi")
                    val success = ShellManager.openAdbWifi()
                    if (success) {
                        updateNotificationContent("adb wifi 已开启")
                        updateNotificationWifi(R.drawable.ic_wifi_primary)
                    } else {
                        updateNotificationContent("adb wifi 开启失败")
                        updateNotificationWifi(R.drawable.ic_wifi_gray)
                    }
                }
            }
        }

    }
}
