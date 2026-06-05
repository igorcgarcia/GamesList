package com.marketdata.sfmcdemo.model

data class SdkStatus(
    val isInitialized: Boolean = false,
    val isDemo: Boolean = false,
    val sdkVersion: String = "",
    val errorMessage: String? = null
)
