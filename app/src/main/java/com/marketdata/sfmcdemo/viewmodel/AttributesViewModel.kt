package com.marketdata.sfmcdemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketdata.sfmcdemo.repository.SFMCRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AttributesViewModel : ViewModel() {

    private val _attributes = MutableLiveData<Map<String, String>>()
    val attributes: LiveData<Map<String, String>> = _attributes

    private val _tags = MutableLiveData<Set<String>>()
    val tags: LiveData<Set<String>> = _tags

    private val _snackMessage = MutableLiveData<String>()
    val snackMessage: LiveData<String> = _snackMessage

    fun loadData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val attrs = SFMCRepository.getAttributes()
                val tags = SFMCRepository.getTags()
                withContext(Dispatchers.Main) {
                    _attributes.value = attrs
                    _tags.value = tags
                }
            }
        }
    }

    fun addAttribute(key: String, value: String) {
        if (key.isBlank()) {
            _snackMessage.value = "Nome do atributo obrigatório"
            return
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                SFMCRepository.setAttribute(key.trim(), value.trim())
            }
            _snackMessage.value = "Atributo salvo!"
            loadData()
        }
    }

    fun removeAttribute(key: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                SFMCRepository.clearAttribute(key)
            }
            _snackMessage.value = "Atributo removido"
            loadData()
        }
    }

    fun addTag(tag: String) {
        if (tag.isBlank()) {
            _snackMessage.value = "Nome da tag obrigatório"
            return
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                SFMCRepository.addTag(tag.trim())
            }
            _snackMessage.value = "Tag adicionada!"
            loadData()
        }
    }

    fun removeTag(tag: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                SFMCRepository.removeTag(tag)
            }
            _snackMessage.value = "Tag removida"
            loadData()
        }
    }
}
