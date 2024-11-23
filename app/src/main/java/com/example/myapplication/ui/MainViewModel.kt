package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Contact
import com.example.myapplication.data.ContactDAO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
class MainViewModel(private val dao: ContactDAO) : ViewModel() {

    // Holds the currently selected screen
    private val _selectedScreen = MutableStateFlow("")
    val selectedScreen: StateFlow<String> = _selectedScreen.asStateFlow()

    var editContact: Contact? = null // Stores the contact being edited

    // Holds the list of contacts
    private val _contactList = MutableStateFlow<List<Contact>>(emptyList())
    val contactList: StateFlow<List<Contact>> = _contactList.asStateFlow()

    // Updates the currently selected page
    fun selectPage(screen: String) {
        _selectedScreen.value = screen
    }

    // Saves a contact and refreshes the list
    fun saveContact(contact: Contact) {
        viewModelScope.launch {
            dao.insertContact(contact)
            getAllContacts() // Refresh the contact list
        }
    }

    // Deletes a contact and refreshes the list
    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            dao.deleteContact(contact)
            getAllContacts() // Refresh the contact list
        }
    }

    // Retrieves all contacts from the database
    fun getAllContacts() {
        viewModelScope.launch {
            dao.getContacts().collect { data ->
                _contactList.value = data // Updates the contact list
            }
        }
    }

    // Updates a contact with the given details
    fun updateContact(nameEdit: String, phoneEdit: String, idEdit: Int, favouriteEdit: Int) {
        val updateContact = Contact(
            name = nameEdit,
            phone = phoneEdit,
            id = idEdit,
            favourite = favouriteEdit
        )
        viewModelScope.launch {
            dao.updateContact(updateContact)
        }
    }

    // Marks a contact as a favorite
    fun addFavourite(contact: Contact) {
        var contactEdit = contact
        contactEdit.favourite = 1 // Sets favorite flag
        viewModelScope.launch {
            dao.updateContact(contactEdit)
        }
    }

    // Removes a contact from favorites
    fun removeFavorite(contact: Contact) {
        var contactEdit = contact
        contactEdit.favourite = 0 // Unsets favorite flag
        viewModelScope.launch {
            dao.updateContact(contactEdit)
        }
    }
}
