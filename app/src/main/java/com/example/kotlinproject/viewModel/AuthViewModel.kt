package com.example.kotlinproject.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinproject.model.UserModel
import com.example.kotlinproject.repository.auth.AuthRepo
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(private val repo: AuthRepo) : ViewModel() {

    // LiveData for user data
    private val _userData = MutableLiveData<UserModel?>()
    val userData: LiveData<UserModel?> get() = _userData

    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        repo.login(email, password, callback)
    }

    fun signUp(email: String, password: String, callback: (Boolean, String?, String?) -> Unit) {
        repo.signUp(email, password, callback)
    }

    fun addUserToDatabase(userId: String, userModel: UserModel, callback: (Boolean, String?) -> Unit) {
        repo.addUserToDatabase(userId, userModel, callback)
    }

    fun forgotPassword(email: String, callback: (Boolean, String?) -> Unit) {
        repo.forgotPassword(email, callback)
    }

    fun getCurrentUser(): FirebaseUser? {
        return repo.getCurrentUser()
    }

    fun logout(callback: (Boolean, String?) -> Unit) {
        repo.logout(callback)
    }

    fun fetchUserData(userId: String) {
        repo.getUserFromFirebase(userId) { userModel, success, message ->
            if (success) {
                _userData.value = userModel
            } else {
                // Handle failure case (e.g., show a message or log the error)
                _userData.value = null
                // You can use message to show an error toast or log it
                // Log.e("AuthViewModel", "Error fetching user data: $message")
            }
        }
    }

    fun uploadImages(imageName: String, imageUri: Uri, callback: (Boolean, String?, String?) -> Unit) {
        repo.uploadImages(imageName, imageUri) { success, url, message ->
            callback(success, url, message)
        }
    }

    fun updateUser(userId: String, data: MutableMap<String, Any?>, callback: (Boolean, String?) -> Unit) {
        repo.updateUser(userId, data) { success, message ->
            callback(success, message)
        }
    }
}
