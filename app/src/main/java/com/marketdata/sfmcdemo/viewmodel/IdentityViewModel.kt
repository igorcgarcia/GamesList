package com.marketdata.sfmcdemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketdata.sfmcdemo.model.ContactData
import com.marketdata.sfmcdemo.repository.SFMCRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IdentityViewModel : ViewModel() {

    private val _currentContact = MutableLiveData<ContactData>()
    val currentContact: LiveData<ContactData> = _currentContact

    private val _saveResult = MutableLiveData<Result<Unit>>()
    val saveResult: LiveData<Result<Unit>> = _saveResult

    fun loadCurrentValues() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val key = SFMCRepository.getContactKey() ?: ""
                val attrs = SFMCRepository.getAttributes()
                val contact = ContactData(
                    contactKey = key,
                    firstName = attrs["FirstName"] ?: "",
                    lastName = attrs["LastName"] ?: "",
                    email = attrs["Email"] ?: "",
                    phone = attrs["MobilePhone"] ?: ""
                )
                withContext(Dispatchers.Main) {
                    _currentContact.value = contact
                }
            }
        }
    }

    fun saveIdentity(
        contactKey: String,
        firstName: String,
        lastName: String,
        email: String,
        phone: String
    ) {
        if (contactKey.isBlank()) {
            _saveResult.value = Result.failure(IllegalArgumentException("Contact Key obrigatório"))
            return
        }
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    SFMCRepository.setContact(contactKey, firstName, lastName, email, phone)
                }
                _saveResult.value = Result.success(Unit)
                loadCurrentValues()
            } catch (e: Exception) {
                _saveResult.value = Result.failure(e)
            }
        }
    }
}
