package com.tfl.vguardrishta.utils
//
//import android.content.Context
//import android.content.Intent
//import com.onesignal.OSNotificationOpenResult
//import com.onesignal.OneSignal
//import com.tfl.vguardrishta.ui.components.logIn.LogInActivity
//import com.tfl.vguardrishta.ui.components.vguard.home.RishtaHomeActivity
//
//class ExampleNotificationOpenedHandler internal constructor(private var context: Context) :
//    OneSignal.NotificationOpenedHandler {
//
//    override fun notificationOpened(result: OSNotificationOpenResult) {
//
//
//        if (PrefUtil(context).getIsLoggedIn()) {
//            val intent = Intent(context, RishtaHomeActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
//            context.startActivity(intent)
//        } else {
//            val intent = Intent(context, LogInActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
//            context.startActivity(intent)
//        }
//    }
//}