import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase

object FirebaseModel {

    private val database: FirebaseFirestore by lazy {
        Firebase.firestore.apply {
            // Configure Firebase settings - No local cache because we use ROOM
            val settings = firestoreSettings {
                setLocalCacheSettings(memoryCacheSettings { })
            }
            firestoreSettings = settings
        }
    }

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val user: FirebaseUser?
        get() = auth.currentUser

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
        Log.d("TAG", "AuthRepository-User logged out")
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
}
