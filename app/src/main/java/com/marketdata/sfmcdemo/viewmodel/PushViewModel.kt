package com.marketdata.sfmcdemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketdata.sfmcdemo.repository.SFMCRepository
import com.marketdata.sfmcdemo.service.SFMCPushService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PushViewModel : ViewModel() {

    private val _pushEnabled = MutableLiveData<Boolean>()
    val pushEnabled: LiveData<Boolean> = _pushEnabled

    private val _pushToken = MutableLiveData<String>()
    val pushToken: LiveData<String> = _pushToken

    private val _lastPayload = MutableLiveData<String>()
    val lastPayload: LiveData<String> = _lastPayload

    private val _pushCount = MutableLiveData<Int>()
    val pushCount: LiveData<Int> = _pushCount

    fun refresh() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val enabled = SFMCRepository.isPushEnabled()
                val token = SFMCRepository.getPushToken() ?: ""
                withContext(Dispatchers.Main) {
                    _pushEnabled.value = enabled
                    _pushToken.value = token
                    _lastPayload.value = SFMCPushService.lastPushPayload
                    _pushCount.value = SFMCPushService.pushCount
                }
            }
        }
    }

    fun setPushEnabled(enabled: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                SFMCRepository.setPushEnabled(enabled)
            }
            _pushEnabled.value = enabled
        }
    }
}
