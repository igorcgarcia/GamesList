package com.marketdata.sfmcdemo

import android.app.Application
import android.util.Log
import com.marketdata.sfmcdemo.config.SFMCConfig
import com.salesforce.marketingcloud.MarketingCloudConfig
import com.salesforce.marketingcloud.MarketingCloudSdk
import com.salesforce.marketingcloud.notifications.NotificationCustomizationOptions

class MarketdataApp : Application() {

    companion object {
        private const val TAG = "MarketdataApp"
    }

    override fun onCreate() {
        super.onCreate()
        initializeSFMC()
    }

    private fun initializeSFMC() {
        if (SFMCConfig.isDemo()) {
            Log.w(TAG, "SFMC Demo Mode: placeholder credentials — SDK will NOT connect to server.")
        }

        try {
            MarketingCloudSdk.init(
                this,
                MarketingCloudConfig.builder()
                    .setApplicationId(SFMCConfig.APP_ID)
                    .setAccessToken(SFMCConfig.ACCESS_TOKEN)
                    .setMarketingCloudServerUrl(SFMCConfig.SERVER_URL)
                    .setSenderId(SFMCConfig.SENDER_ID)
                    .setAnalyticsEnabled(SFMCConfig.ANALYTICS_ENABLED)
                    .setPushEnabled(SFMCConfig.PUSH_ENABLED)
                    .setInboxEnabled(SFMCConfig.INBOX_ENABLED)
                    .setNotificationCustomizationOptions(
                        NotificationCustomizationOptions.create(R.mipmap.ic_launcher)
                    )
                    .build(this)
            ) { initStatus ->
                if (initStatus.isUsable) {
                    Log.i(TAG, "SFMC SDK initialized ✓")
                    MarketingCloudSdk.requestSdk { sdk ->
                        sdk.pushMessageManager.enablePush()
                    }
                } else {
                    Log.e(TAG, "SFMC SDK init failed: ${initStatus.localizedException?.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "SFMC SDK exception during init: ${e.message}", e)
        }
    }
}
