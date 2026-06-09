package com.marketdata.sfmcdemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketdata.sfmcdemo.config.SFMCConfig
import com.marketdata.sfmcdemo.repository.SFMCRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {

    private val _contactKey = MutableLiveData<String>()
    val contactKey: LiveData<String> = _contactKey

    private val _pushEnabled = MutableLiveData<Boolean>()
    val pushEnabled: LiveData<Boolean> = _pushEnabled

    private val _fcmToken = MutableLiveData<String>()
    val fcmToken: LiveData<String> = _fcmToken

    private val _isDemo = MutableLiveData<Boolean>()
    val isDemo: LiveData<Boolean> = _isDemo

    fun refresh() {
        viewModelScope.launch {
            _isDemo.value = SFMCConfig.isDemo()
            withContext(Dispatchers.IO) {
                val key = SFMCRepository.getContactKey()
                val push = SFMCRepository.isPushEnabled()
                val token = SFMCRepository.getPushToken()
                withContext(Dispatchers.Main) {
                    _contactKey.value = key ?: ""
                    _pushEnabled.value = push
                    _fcmToken.value = token ?: ""
                }
            }
        }
    }
}
