package com.marketdata.sfmcdemo.repository

import android.util.Log
import com.salesforce.marketingcloud.MarketingCloudSdk
import com.salesforce.marketingcloud.events.CustomEvent
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val TAG = "SFMCRepository"

/**
 * Single access point for all Salesforce Marketing Cloud SDK operations.
 * Each function requests the SDK on the calling coroutine context.
 */
object SFMCRepository {

    // ─── Contact / Identity ────────────────────────────────────────────────────

    fun getContactKey(): String? = runOnSdk { sdk ->
        sdk.registrationManager.contactKey
    }

    fun getAttributes(): Map<String, String> = runOnSdk { sdk ->
        sdk.registrationManager.attributes ?: emptyMap()
    } ?: emptyMap()

    fun getTags(): Set<String> = runOnSdk { sdk ->
        sdk.registrationManager.tags ?: emptySet()
    } ?: emptySet()

    fun setContact(
        contactKey: String,
        firstName: String = "",
        lastName: String = "",
        email: String = "",
        phone: String = ""
    ) {
        MarketingCloudSdk.requestSdk { sdk ->
            val editor = sdk.registrationManager.edit()
                .setContactKey(contactKey)
            if (firstName.isNotBlank()) editor.setAttribute("FirstName", firstName)
            if (lastName.isNotBlank()) editor.setAttribute("LastName", lastName)
            if (email.isNotBlank()) editor.setAttribute("Email", email)
            if (phone.isNotBlank()) editor.setAttribute("MobilePhone", phone)
            editor.commit()
            Log.d(TAG, "Contact saved: contactKey=$contactKey")
        }
    }

    // ─── Attributes ────────────────────────────────────────────────────────────

    fun setAttribute(key: String, value: String) {
        MarketingCloudSdk.requestSdk { sdk ->
            sdk.registrationManager.edit()
                .setAttribute(key, value)
                .commit()
            Log.d(TAG, "Attribute set: $key=$value")
        }
    }

    fun clearAttribute(key: String) {
        MarketingCloudSdk.requestSdk { sdk ->
            sdk.registrationManager.edit()
                .clearAttribute(key)
                .commit()
            Log.d(TAG, "Attribute cleared: $key")
        }
    }

    // ─── Tags ──────────────────────────────────────────────────────────────────

    fun addTag(tag: String) {
        MarketingCloudSdk.requestSdk { sdk ->
            sdk.registrationManager.edit()
                .addTag(tag)
                .commit()
            Log.d(TAG, "Tag added: $tag")
        }
    }

    fun removeTag(tag: String) {
        MarketingCloudSdk.requestSdk { sdk ->
            sdk.registrationManager.edit()
                .removeTag(tag)
                .commit()
            Log.d(TAG, "Tag removed: $tag")
        }
    }

    // ─── Push ──────────────────────────────────────────────────────────────────

    fun isPushEnabled(): Boolean = runOnSdk { sdk ->
        sdk.pushMessageManager.isPushEnabled
    } ?: false

    fun getPushToken(): String? = runOnSdk { sdk ->
        sdk.pushMessageManager.pushToken
    }

    fun setPushEnabled(enabled: Boolean) {
        MarketingCloudSdk.requestSdk { sdk ->
            if (enabled) sdk.pushMessageManager.enablePush()
            else sdk.pushMessageManager.disablePush()
            Log.d(TAG, "Push enabled=$enabled")
        }
    }

    // ─── Analytics ─────────────────────────────────────────────────────────────

    fun trackPageView(url: String, title: String) {
        MarketingCloudSdk.requestSdk { sdk ->
            sdk.analyticsManager.trackPageView(url, title, null)
            Log.d(TAG, "PageView tracked: $title ($url)")
        }
    }

    fun trackCustomEvent(name: String, attributes: Map<String, String> = emptyMap()) {
        MarketingCloudSdk.requestSdk { sdk ->
            val builder = CustomEvent.builder(name)
            attributes.forEach { (k, v) -> builder.setAttribute(k, v) }
            sdk.eventManager.track(builder.build())
            Log.d(TAG, "Event tracked: $name with ${attributes.size} attrs")
        }
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    fun isSdkReady(): Boolean =
        MarketingCloudSdk.getInstance() != null

    /** Parse simple JSON object to Map<String,String> for event attributes */
    fun parseJsonAttributes(json: String): Map<String, String> {
        if (json.isBlank()) return emptyMap()
        return try {
            val obj = JSONObject(json.trim())
            buildMap {
                obj.keys().forEach { key -> put(key, obj.getString(key)) }
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("JSON inválido: ${e.message}", e)
        }
    }

    // ─── Private ───────────────────────────────────────────────────────────────

    private fun <T> runOnSdk(block: (MarketingCloudSdk) -> T): T? {
        return try {
            MarketingCloudSdk.getInstance()?.let { block(it) }
        } catch (e: Exception) {
            Log.e(TAG, "SDK error: ${e.message}", e)
            null
        }
    }
}
