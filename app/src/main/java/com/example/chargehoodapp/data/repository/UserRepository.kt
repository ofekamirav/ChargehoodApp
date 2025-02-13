package com.example.chargehoodapp.data.repository

import android.util.Log
import com.example.chargehoodapp.base.Constants.Collections.USERS
import com.example.chargehoodapp.data.local.dao.UserDao
import com.example.chargehoodapp.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception


//Connect the ViewModel with the database- local and remote
class UserRepository(private val userDao: UserDao) {

    private val firestore = FirebaseModel.database
    private val usersCollection = firestore.collection(USERS)
    private val user = FirebaseModel.getCurrentUser()

    //Create a new user
    suspend fun createUser(user: User): Boolean {
        return try {
            usersCollection.document(user.uid).set(user).await()
            Log.d("TAG", "UserRepository-User created successfully: ${user.uid}")
            withContext(Dispatchers.IO) {
                userDao.insertUser(user)
            }
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

    //Get user from local DB
    suspend fun getUserFromLocalDB(uid: String): User? {
        return withContext(Dispatchers.IO) {
            userDao.getUserByUid(uid)
        }
    }


    //Update user profile firebase
    suspend fun updateUser(updates: Map<String, Any>, userUpdated: User): Boolean {
        return try {
            val uid = user?.uid ?: throw IllegalStateException("User not logged in")
            usersCollection.document(uid).update(updates).await()
            withContext(Dispatchers.IO) {
                userDao.updateUser(userUpdated)
            }
            syncUserDetails()
            true
        } catch (e: Exception) {
            Log.e("TAG", "UserRepository-Error updating user: ${e.message}")
            false
        }
    }

    //Check if user has changes in details at firestore and update the dao
    fun syncUserDetails() {
        val uid = user?.uid ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val documentSnapshot = usersCollection.document(uid).get().await()
                val user = documentSnapshot.toObject(User::class.java)
                if (user != null) {
                    withContext(Dispatchers.IO) {
                        userDao.deleteUser(user)
                        userDao.insertUser(user)
                    }
                }
            } catch (e: Exception) {
                Log.e("TAG", "UserRepository-Error syncing user details: ${e.message}")

            }
        }


    }

}
