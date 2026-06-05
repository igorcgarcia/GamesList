package com.marketdata.sfmcdemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketdata.sfmcdemo.model.EventEntry
import com.marketdata.sfmcdemo.repository.SFMCRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnalyticsViewModel : ViewModel() {

    private val _eventLog = MutableLiveData<List<EventEntry>>(emptyList())
    val eventLog: LiveData<List<EventEntry>> = _eventLog

    private val _result = MutableLiveData<Result<String>>()
    val result: LiveData<Result<String>> = _result

    fun trackEvent(eventName: String, attrsJson: String) {
        if (eventName.isBlank()) {
            _result.value = Result.failure(IllegalArgumentException("Nome do evento obrigatório"))
            return
        }
        viewModelScope.launch {
            try {
                val attrs = withContext(Dispatchers.Default) {
                    SFMCRepository.parseJsonAttributes(attrsJson)
                }
                withContext(Dispatchers.IO) {
                    SFMCRepository.trackCustomEvent(eventName.trim(), attrs)
                }
                addLog(EventEntry("custom", eventName.trim(), "attrs: $attrs"))
                _result.value = Result.success(eventName.trim())
            } catch (e: Exception) {
                _result.value = Result.failure(e)
            }
        }
    }

    fun trackPageView(url: String, title: String) {
        val resolvedUrl = url.ifBlank { "https://marketdata.com.br" }
        val resolvedTitle = title.ifBlank { "Marketdata" }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                SFMCRepository.trackPageView(resolvedUrl, resolvedTitle)
            }
            addLog(EventEntry("pageview", resolvedTitle, resolvedUrl))
            _result.value = Result.success("pageview:$resolvedTitle")
        }
    }

    fun clearLog() {
        _eventLog.value = emptyList()
    }

    private fun addLog(entry: EventEntry) {
        val current = _eventLog.value.orEmpty().toMutableList()
        current.add(0, entry)
        _eventLog.value = current
    }
}
