package com.marketdata.sfmcdemo.config

import com.marketdata.sfmcdemo.BuildConfig

object SFMCConfig {

    // ---------------------------------------------------------------------------
    // Credenciais do Salesforce Marketing Cloud (SFMC)
    // Obtidas em: Marketing Cloud > Setup > Mobile Studio > MobilePush > App Config
    //
    // Para produção, coloque os valores reais em app/build.gradle:
    //   buildConfigField "String", "SFMC_APP_ID",      '"xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"'
    //   buildConfigField "String", "SFMC_ACCESS_TOKEN", '"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"'
    //   buildConfigField "String", "SFMC_SERVER_URL",  '"https://YOUR_TENANT.rest.marketingcloudapis.com"'
    //   buildConfigField "String", "SFMC_SENDER_ID",   '"263851361821"'  // Firebase project number
    // ---------------------------------------------------------------------------

    val APP_ID: String get() = BuildConfig.SFMC_APP_ID
    val ACCESS_TOKEN: String get() = BuildConfig.SFMC_ACCESS_TOKEN
    val SERVER_URL: String get() = BuildConfig.SFMC_SERVER_URL
    val SENDER_ID: String get() = BuildConfig.SFMC_SENDER_ID

    val PUSH_ENABLED = true
    val ANALYTICS_ENABLED = true
    val INBOX_ENABLED = true
    val GEOFENCE_ENABLED = false

    /** Returns true when placeholder credentials are still in place */
    fun isDemo(): Boolean =
        APP_ID.startsWith("YOUR_") || ACCESS_TOKEN.startsWith("YOUR_")
}
