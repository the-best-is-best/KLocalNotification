import UIKit
import ComposeApp
import UserNotifications

@main
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate {

    var window: UIWindow?

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        window = UIWindow(frame: UIScreen.main.bounds)

        // Set the delegate for handling notifications
        LocalNotification.shared.doInit(userNotificationCenterDelegate: self)

        // Set the root view controller
        if let window = window {
            window.rootViewController = MainKt.MainViewController()
            window.makeKeyAndVisible()
        }

 // i think don't need add this
//        if let userInfo = launchOptions?[.remoteNotification] as? [String: AnyObject] {
//            LocalNotification.shared.notifyNotification(data: userInfo)
//        }

        return true
    }

    // Handle notifications while the app is in the foreground
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        // Show notifications while app is in the foreground
        completionHandler([.alert, .sound, .badge])
    }

    // Handle notifications when clicked
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        let notification = response.notification.request
        let userInfo = notification.content.userInfo

        // Notify about the notification click
        Task {
            do {
                try await LocalNotification.shared.notifyPayloadListeners(data: userInfo)
            } catch {
                print("Error notifying payload listeners: \(error)")
            }
        }
        // Always call the completion handler
        completionHandler()
    }
}
