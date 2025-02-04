package com.example.chargehoodapp.data.repository

import android.util.Log
import com.example.chargehoodapp.base.Constants.Collections.USERS
import com.example.chargehoodapp.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception


//Connect the ViewModel with the database- local and remote
class UserRepository {

    private val firestore = FirebaseModel.database
    private val usersCollection = firestore.collection(USERS)
    private val user = FirebaseModel.getCurrentUser()

    //Create a new user
    suspend fun createUser(user: User): Boolean {
        return try {
            usersCollection.document(user.uid).set(user).await()
            Log.d("TAG", "UserRepository-User created successfully: ${user.uid}")
            true
        } catch (e: Exception) {
            Log.e("TAG", "UserRepository-Error creating user: ${e.message}")
            false
        }
    }

    //Get user by UID
    suspend fun getUserByUid(uid: String): User? {
        if (uid.isBlank()) {
            Log.e("TAG", "UserRepository - Error: UID is empty!")
            return null
        }
        return try {
            val documentSnapshot = usersCollection.document(uid).get().await()
            val user = documentSnapshot.toObject(User::class.java)
            if (user != null) {
                Log.d("TAG", "UserRepository - User retrieved: ${user.name}")
            } else {
                Log.d("TAG", "UserRepository - User not found for UID: $uid")
            }
            user
        } catch (e: Exception) {
            Log.e("TAG", "UserRepository - Error getting user by UID: ${e.message}")
            null
        }
    }


    //Update user profile
    suspend fun updateUser(updates: Map<String, Any>): Boolean {
        return try {
            val uid = user?.uid ?: throw IllegalStateException("User not logged in")
            usersCollection.document(uid).update(updates).await()
            true
        } catch (e: Exception) {
            Log.e("TAG", "UserRepository-Error updating user: ${e.message}")
            false
        }
    }
}
