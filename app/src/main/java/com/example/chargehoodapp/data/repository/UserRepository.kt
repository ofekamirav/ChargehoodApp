package com.example.chargehoodapp.data.repository

import android.util.Log
import com.example.chargehoodapp.base.Constants.Collections.USERS
import com.example.chargehoodapp.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class UserRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection(USERS)
    private val auth = FirebaseAuth.getInstance()

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
            Log.e("TAG", "UserRepository-Error getting user by UID: ${e.message}")
            null
        }
    }

    //Update user profile
    suspend fun updateUser(uid: String, updates: Map<String, Any>): Boolean {
        return try {
            //Update the user profile in Firestore
            usersCollection.document(uid).update(updates).await()

            //add logic to update user profile picture in Cloudinary

            //Update the user email and password in Firebase Authentication if needed
            val user = auth.currentUser

            if(updates.containsKey("email")){
                val newEmail = updates["email"] as String
                if(newEmail != null && newEmail != user?.email){
                    user?.updateEmail(newEmail)?.await()
                    Log.d("TAG", "UserRepository-User email updated: $newEmail")
                }
                if(updates.containsKey("password")) {
                    val newPassword = updates["password"] as String
                    if (newPassword != null) {
                        user?.updatePassword(newPassword)?.await()
                        Log.d("TAG", "UserRepository-User password updated")
                    }
                }
            }
            true
        } catch (e: Exception) {
            Log.e("TAG", "UserRepository-Error updating user: ${e.message}")
            false
        }
    }
}
