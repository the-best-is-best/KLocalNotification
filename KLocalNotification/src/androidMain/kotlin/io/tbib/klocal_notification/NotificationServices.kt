package io.tbib.klocal_notification

import android.app.Service
import android.content.Intent
import android.os.IBinder

class NotificationServices : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // by returning this we make sure the service is restarted if the system kills the service
        return START_STICKY
    }


}
