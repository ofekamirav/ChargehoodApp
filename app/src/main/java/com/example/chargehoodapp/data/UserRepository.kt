package com.example.chargehoodapp.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class UserRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

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
        return try {
            val documentSnapshot = usersCollection.document(uid).get().await()
            val user = documentSnapshot.toObject(User::class.java)
            if (user != null) {
                Log.d("TAG", "UserRepository-User retrieved: ${user.name}")
            } else {
                Log.d("TAG", "UserRepository-User not found for UID: $uid")
            }
            user
        } catch (e: Exception) {
            //Connection error
            null
        }
    }

    //Update user profile
    suspend fun updateUser(uid: String, updates: Map<String, Any>): Boolean {
        return try {
            usersCollection.document(uid).update(updates).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
