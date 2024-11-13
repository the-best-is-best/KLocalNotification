<h1 align="center">KLocalNotification</h1><br>
<div align="center">
<a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
<a href="https://android-arsenal.com/api?level=23" rel="nofollow">
    <img alt="API" src="https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat" style="max-width: 100%;">
</a>
  <img src="https://img.shields.io/badge/Platform-Android-brightgreen.svg?logo=android" alt="Badge Android" />
  <img src="https://img.shields.io/badge/iOS-12%2B-blue.svg?logo=apple" alt="iOS 12+ Badge" />

<a href="https://github.com/the-best-is-best/"><img alt="Profile" src="https://img.shields.io/badge/github-%23181717.svg?&style=for-the-badge&logo=github&logoColor=white" height="20"/></a>
</div>

<br>

###

The KLocalNotification package provides a streamlined way to create and schedule local notifications
for both Android and iOS platforms within Kotlin Multiplatform projects. It allows developers to set
up notifications that inform or alert users at specified times or intervals, enhancing user
engagement and facilitating reminders or updates.

- Note not work in killed only foreground or background

<hr>

[![Maven Central](https://img.shields.io/maven-central/v/io.github.the-best-is-best/klocal-notification)](https://central.sonatype.com/artifact/io.github.the-best-is-best/klocal-notification)

KLocalNotification is available on `mavenCentral()`.

## Installation

```kotlin
implementation("io.github.the-best-is-best:klocal-notification:1.0.0-3-rc")
```

<br>

- Note add permissions notification needed android and ios

<br>

- First in gradle

```gradle
  listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            export("io.github.the-best-is-best:klocal-notification")  // Export KLocalNotification so it's available in the framework
        }
    }
    ...
    iosMain.dependencies {
          ...
            api("io.github.the-best-is-best:klocal-notification")
        
        }
```

- Second in AndroidMain create your channels

```kotlin
       

        AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ....
        AndroidKMessagingChannel.initialization(this)
        AndroidKMessagingChannel().initChannel("reminder", "reminder")
        AndroidKMessagingChannel().initChannel("reminder1", "reminder1") 
        ...
         setContent { App() }
         // add this code after setContent
        val data = intent.getStringExtra("data")
        LocalNotification.notifyNotificationOpenAppClicked(data)
    }
      override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle new intent when activity is already in the background
        val data = intent.getStringExtra("data")
        LocalNotification.notifyNotificationClickedListener(data)

    }
}
```

- Third in ios project

```swift
import UserNotifications

@main
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate {
  var window: UIWindow?

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        window = UIWindow(frame: UIScreen.main.bounds)
         // add this
          LocalNotification.shared.doInit(userNotificationCenterDelegate: self)
        ...
        // for received data when app open add this in end
       if let userInfo = launchOptions?[.remoteNotification] as? [String: AnyObject] {
            LocalNotification.shared.notifyNotificationAppOpenClicked(data: userInfo)
            
        }
         return true
    }
    // Handle notifications while the app is in the foreground
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        // Show notifications while app is in the foreground
        let userInfo = notification.request.content.userInfo
        
        LocalNotification.shared.notifyNotificationReceived(data: userInfo)
        completionHandler([.alert, .sound, .badge])
    }
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                 didReceive response: UNNotificationResponse,
                                 withCompletionHandler completionHandler: @escaping () -> Void) {
        // Handle the notification tap
        let userInfo = response.notification.request.content.userInfo
            // Do something based on the type of the notification
        LocalNotification.shared.notifyNotificationClicked(data: userInfo)
        // Always call the completion handler
        completionHandler()
    }

}
```

## How use it

```kotlin
   LocalNotification.setNotificationReceivedListener {
        println("notification received is $it")
    }
    LocalNotification.setNotificationClickedListener {
        println("notification clicked data is $it")
    }
    LaunchedEffect(Unit) {
        LocalNotification.requestAuthorization()
    }
    var notificationId: Int? = null
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            notificationId = Random.nextInt().absoluteValue
            println("id is $notificationId")
            val currentDateTme =
                Clock.System.now().plus(1.minutes).toLocalDateTime(TimeZone.currentSystemDefault())
            LocalNotification.showNotification(
                config = NotificationConfig(
                    id = notificationId!!,
                    idChannel = "reminder",
                    title = "Test title",
                    message = "Test Message",
                    smallIcon = "ic_notification",
                    data = mapOf("test" to 1),
                    schedule = true,
                    dateTime = currentDateTme,
                )
            )
        }) {
            Text("Show notification")
        }

        Button(onClick = {
            println("id is removed $notificationId")

            if (notificationId != null)
                LocalNotification.removeNotification(notificationId!!)
        }) {
            Text("remove notification")
        }
    }

```
