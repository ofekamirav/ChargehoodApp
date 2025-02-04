import android.util.Log
import com.example.chargehoodapp.base.MyApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object FirebaseModel {

    val database: FirebaseFirestore by lazy {
        Firebase.firestore.apply {
            // Configure Firebase settings - No local cache because we use ROOM
            val settings = firestoreSettings {
                setLocalCacheSettings(memoryCacheSettings { })
            }
            firestoreSettings = settings
        }
    }

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private var user: FirebaseUser? = auth.currentUser

    // Listeners for authentication state changes
    private val authStateListeners = mutableListOf<() -> Unit>()


    init {
        auth.addAuthStateListener { firebaseAuth ->
            val newUser = firebaseAuth.currentUser
            if (user?.uid != newUser?.uid) {
                user = newUser
                Log.d("TAG", "FirebaseModel - User switched to: ${user?.email}")
                notifyAuthStateChanged()
            }
        }
    }

    fun updatePassword(newPassword: String) {
        user?.updatePassword(newPassword)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("TAG", "AuthRepository-Password updated successfully")
            } else {
                val exception = task.exception
                Log.e("TAG", "AuthRepository-Password update failed: $exception")
            }
        }
    }

    fun logout() {
        auth.signOut()
        clearLocalData()
        Log.d("TAG", "AuthRepository-User logged out")
    }

    private fun clearLocalData() {
        CoroutineScope(Dispatchers.IO).launch {
            val appContext = MyApplication.Globals.context?.applicationContext as? MyApplication
            appContext?.database?.clearAllTables()
            Log.d("TAG", "FirebaseModel-Local Room database cleared after logout")
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return user
    }

    fun updateEmail(newEmail: String) {
        user?.updateEmail(newEmail)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("TAG", "AuthRepository-Email updated successfully")
            } else {
                val exception = task.exception
                Log.e("TAG", "AuthRepository-Email update failed: $exception")
            }
        }
    }

    fun addAuthStateListener(listener: () -> Unit) {
        authStateListeners.add(listener)
    }

    private fun notifyAuthStateChanged() {
        authStateListeners.forEach { it.invoke() }
    }
}
