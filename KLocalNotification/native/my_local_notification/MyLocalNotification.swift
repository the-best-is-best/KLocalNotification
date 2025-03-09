//
//  LocalNotification.swift
//  LocalNotification
//
//  Created by Michelle Raouf on 09/03/2025.
//

import Foundation

#if canImport(UserNotifications)
import UserNotifications
#endif

@objc public class MyLocalNotification: NSObject {
    
    private static var notificationListener: (([AnyHashable: Any]) -> Void)?
    private static var lastNotificationData: [AnyHashable: Any]?

    @objc public static func initNotification(delegate: UNUserNotificationCenterDelegate) {
        UNUserNotificationCenter.current().delegate = delegate
        requestAuthorization()
    }

    @objc public static func showNotification(
        id: String,
        title: String,
        body: String,
        schedule: Bool,
        date: Date? = nil,
        data: [AnyHashable: Any]? = nil
    ) {
        #if os(tvOS)
        print("tvOS does not support banner notifications. Only badges are available.")
        return
        #else
        let content = UNMutableNotificationContent()
        content.title = title
        content.body = body
        content.sound = .default

        // إضافة البيانات إلى userInfo
        if let data = data {
            content.userInfo = data
        }

        let trigger: UNNotificationTrigger?
        if schedule, let date = date {
            let timeInterval = max(1, date.timeIntervalSinceNow)
            trigger = UNTimeIntervalNotificationTrigger(timeInterval: timeInterval, repeats: false)
        } else {
            trigger = UNTimeIntervalNotificationTrigger(timeInterval: 1, repeats: false)
        }

        let request = UNNotificationRequest(identifier: id, content: content, trigger: trigger)

        UNUserNotificationCenter.current().add(request) { error in
            if let error = error {
                print("Error scheduling notification: \(error.localizedDescription)")
            }
        }
        #endif
    }

    @objc public static func removeNotification(notificationId: String) {
        #if canImport(UserNotifications)
        UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: [notificationId])
        #endif
    }

    @objc public static func setNotificationListener(callback: @escaping ([AnyHashable: Any]) -> Void) {
        notificationListener = callback
        if let data = lastNotificationData {
            callback(data)
        }
        lastNotificationData = nil
    }

    @objc public static func notifyNotification(data: [AnyHashable: Any]) {
        lastNotificationData = data
        notificationListener?(data)
    }

    @objc public static func requestAuthorization() {
        #if canImport(UserNotifications)
        let center = UNUserNotificationCenter.current()
        center.requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            if let error = error {
                print("Authorization request failed: \(error.localizedDescription)")
            } else {
                print("Authorization granted: \(granted)")
            }
        }
        #endif
    }
}
