import UIKit
import ComposeApp
import UserNotifications
import UserNotifications

@main
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate {

    var window: UIWindow?

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        window = UIWindow(frame: UIScreen.main.bounds)

        // Set the delegate for handling notifications while app is in the foreground
        LocalNotification.shared.doInit(userNotificationCenterDelegate: self)
        

        // Set the root view controller
        if let window = window {
            window.rootViewController = MainKt.MainViewController()
            window.makeKeyAndVisible()
        }

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
