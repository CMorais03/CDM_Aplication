package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDAO {

    // Inserts a new contact into the database
    @Insert
    suspend fun insertContact(contact: Contact)

    // Updates an existing contact in the database
    @Update
    suspend fun updateContact(contact: Contact)

    // Deletes a contact from the database
    @Delete
    suspend fun deleteContact(contact: Contact)

    // Retrieves all contacts, ordered by favorite status (desc to show the favourites first) and name (asc)
    @Query("SELECT * FROM contacts ORDER BY favourite DESC, name ASC")
    fun getContacts(): Flow<List<Contact>>
}
