package com.marketdata.sfmcdemo.service

import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import com.salesforce.marketingcloud.MarketingCloudSdk
import com.salesforce.marketingcloud.notifications.CloudMessagingService

/**
 * Extends SFMC CloudMessagingService so the SDK processes all push messages.
 * We override onNewToken + onMessageReceived to capture push data for the UI.
 */
class SFMCPushService : CloudMessagingService() {

    companion object {
        private const val TAG = "SFMCPushService"

        @Volatile var lastPushPayload: String = ""
            private set

        @Volatile var pushCount: Int = 0
            private set
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "New FCM token received (first 20 chars): ${token.take(20)}")
        MarketingCloudSdk.requestSdk { sdk ->
            sdk.pushMessageManager.setPushToken(token)
        }
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notif = remoteMessage.notification
        val payload = buildString {
            if (notif?.title != null) appendLine("title: ${notif.title}")
            if (notif?.body  != null) appendLine("body : ${notif.body}")
            if (remoteMessage.data.isNotEmpty()) {
                append("data : ${remoteMessage.data}")
            }
        }.trimEnd()

        if (payload.isNotBlank()) {
            lastPushPayload = payload
            pushCount++
            Log.d(TAG, "Push received:\n$payload")
        }
        super.onMessageReceived(remoteMessage)
    }
}
