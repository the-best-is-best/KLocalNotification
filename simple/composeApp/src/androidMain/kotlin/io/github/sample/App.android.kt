package io.github.sample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.tbib.klocal_notification.AndroidKMessagingChannel
import io.tbib.klocal_notification.LocalNotification

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AndroidKMessagingChannel.initialization(this)
        // Initialize notification channel and set up the listener
        AndroidKMessagingChannel().initChannel("reminder", "reminder")
        setContent { App() }

        // Handle data from the intent if the activity is started with a notification click
        val data = intent.getStringExtra("data")
        LocalNotification.notifyPayloadListeners(data)

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle new intent when activity is already in the background or foreground
        val data = intent.getStringExtra("data")
        LocalNotification.notifyPayloadListeners(data)

    }


}


@Preview
@Composable
fun AppPreview() {
    App()
}
