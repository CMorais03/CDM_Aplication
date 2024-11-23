package com.example.myapplication.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R
import com.example.myapplication.data.Contact

// Function to validate contact data
fun validateContactData(name: String, phone: String): Pair<Boolean, String?> {
    return when {
        name.isEmpty() -> Pair(false, "Fill the name")
        phone.length != 9 || phone.any { !it.isDigit() } -> Pair(false, "The phone number should have 9 digits")
        else -> Pair(true, null)
    }
}

@Composable
fun MainView(vm: MainViewModel) {
    val navController = rememberNavController()
    val state = vm.selectedScreen.collectAsState()
    Scaffold(
        topBar = { topBar(navController, state.value) }, // Top bar component
        modifier = Modifier.padding(top = 40.dp)
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "Form", // Default starting page
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("Form") {
                vm.selectPage("Form") // Track selected page
                FormView(vm) // Form screen
            }
            composable("List") {
                vm.selectPage("List")
                vm.getAllContacts() // Fetch contacts
                ListView(vm, navController) // List screen
            }
            composable("EditScreen") {
                vm.selectPage("Edit") // Set screen to "Edit" (not shown in top bar)
                EditScreen(vm, navController) // Edit screen
            }
        }
    }
}

@Composable
fun topBar(nav: NavController, screen: String) {
    NavigationBar(
        modifier = Modifier.height(80.dp) // Top bar height
    ) {
        NavigationBarItem(
            modifier = Modifier.padding(top = 25.dp),
            selected = screen == "Form",
            onClick = { nav.navigate("Form") }, // Navigate to Form
            label = { Text(text = "Form") },
            icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add") }
        )
        NavigationBarItem(
            modifier = Modifier.padding(top = 25.dp),
            selected = screen == "List",
            onClick = { nav.navigate("List") }, // Navigate to List
            label = { Text(text = "List") },
            icon = { Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = "List") }
        )
    }
}

@Composable
fun FormView(vm: MainViewModel) {
    var name: String by rememberSaveable { mutableStateOf("") }
    var phone: String by rememberSaveable { mutableStateOf("") }
    var errorMessage: String? by rememberSaveable { mutableStateOf(null) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Add Contact", modifier = Modifier.padding(vertical = 30.dp), fontWeight = FontWeight.Bold) // Form title
        Spacer(modifier = Modifier.height(5.dp))

        // Input for name
        OutlinedTextField(
            value = name,
            onValueChange = { value ->
                name = value
                errorMessage = null // Clear error on edit
            },
            label = { Text(text = stringResource(R.string.name)) },
            isError = errorMessage != null && name.isEmpty()
        )

        // Input for phone
        OutlinedTextField(
            value = phone,
            onValueChange = { value ->
                if (value.length <= 9 && value.all { it.isDigit() }) {
                    phone = value
                    errorMessage = null // Clear error on edit
                }
            },
            label = { Text(text = stringResource(R.string.phone)) },
            isError = errorMessage != null && phone.length != 9
        )

        if (errorMessage != null) {
            // Display error message
            Text(
                text = errorMessage!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = {
            val (isValid, error) = validateContactData(name, phone)
            if (isValid) {
                vm.saveContact(Contact(name, phone)) // Save contact
                // Clear fields
                name = ""
                phone = ""
                errorMessage = null
            } else {
                errorMessage = error // Show error message
            }
        }) {
            Text(text = stringResource(R.string.save))
        }
    }
}

@Composable
fun ListView(vm: MainViewModel, navController: NavController) {
    val state = vm.contactList.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(10.dp) // General padding
    ) {
        item {
            // Title
            Text(
                text = "List of Contacts",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
        items(state.value) { contact ->
            Row(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp)
                ) {
                    Log.d("ID", contact.id.toString())
                    Text(text = contact.name)
                    Text(text = contact.phone)
                }
                if (contact.favourite == 1) {
                    // Contact in favorite icon
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favourite",
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable {
                                vm.editContact = contact
                                vm.removeFavorite(contact) // calls the function Remove favorite
                                navController.navigate("List")
                            }
                    )
                } else {
                    // Contact not favorite icon
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Not Favourite",
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable {
                                vm.editContact = contact
                                vm.addFavourite(contact) // Calls the function Add favorite
                                navController.navigate("List")
                            }
                    )
                }
                // Delete icon
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .clickable { vm.deleteContact(contact) } // Calls the function delete contact
                )
                // Edit icon
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier.clickable {
                        vm.editContact = contact // Saves the contact in the variable
                        navController.navigate("EditScreen") // Navigates to the Edit Screen
                    }
                )
            }
        }
    }
}

@Composable
fun EditScreen(vm: MainViewModel, nav: NavController) {
    val contactID = vm.editContact!!.id
    val contactFavourite = vm.editContact!!.favourite
    val nameContact = vm.editContact?.name ?: ""
    val phoneContact = vm.editContact?.phone ?: ""

    var name by rememberSaveable { mutableStateOf(nameContact) }
    var phone by rememberSaveable { mutableStateOf(phoneContact) }
    var errorMessage: String? by rememberSaveable { mutableStateOf(null) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Edit",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Spacer(modifier = Modifier.height(30.dp))
        // Name input
        OutlinedTextField(
            value = name,
            onValueChange = { value ->
                name = value
                errorMessage = null
            },
            label = { Text(text = stringResource(R.string.name)) },
            isError = errorMessage != null && name.isEmpty()
        )
        // Phone input
        OutlinedTextField(
            value = phone,
            onValueChange = { value ->
                if (value.length <= 9 && value.all { it.isDigit() }) {
                    phone = value
                    errorMessage = null
                }
            },
            label = { Text(text = stringResource(R.string.phone)) },
            isError = errorMessage != null && phone.length != 9
        )
        if (errorMessage != null) {
            // Error message
            Text(
                text = errorMessage!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        // Save button
        Button(onClick = {
            val (isValid, error) = validateContactData(name, phone)
            if (isValid) {
                vm.updateContact(name, phone, contactID, contactFavourite) // Update contact
                nav.navigate("List")
            } else {
                errorMessage = error
            }
        }) {
            Text(text = stringResource(R.string.save))
        }
    }
}
