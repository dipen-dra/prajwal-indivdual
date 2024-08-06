package com.example.kotlinproject.repository.auth

import android.net.Uri
import com.example.kotlinproject.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class AuthRepoImpl : AuthRepo {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val reference: DatabaseReference = database.reference.child("users")
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference = storage.reference.child("users")

    override fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { res ->
            if (res.isSuccessful) {
                callback(true, "Login success")
            } else {
                callback(false, "Unable to login: ${res.exception?.message}")
            }
        }
    }

    override fun signUp(email: String, password: String, callback: (Boolean, String?, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { res ->
            if (res.isSuccessful) {
                callback(true, "Sign up success", auth.currentUser?.uid)
            } else {
                callback(false, "Unable to sign up: ${res.exception?.message}", null)
            }
        }
    }

    override fun addUserToDatabase(userId: String, userModel: UserModel, callback: (Boolean, String?) -> Unit) {
        userModel.id = userId
        reference.child(userId).setValue(userModel).addOnCompleteListener { res ->
            if (res.isSuccessful) {
                callback(true, "User added to database")
            } else {
                callback(false, "Unable to add user to database: ${res.exception?.message}")
            }
        }
    }

    override fun forgotPassword(email: String, callback: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { res ->
            if (res.isSuccessful) {
                callback(true, "Reset mail sent to $email")
            } else {
                callback(false, "Unable to reset password: ${res.exception?.message}")
            }
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override fun getUserFromFirebase(userId: String, callback: (UserModel?, Boolean, String?) -> Unit) {
        reference.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userModel = snapshot.getValue(UserModel::class.java)
                    callback(userModel, true, "Fetch success")
                } else {
                    callback(null, false, "User does not exist")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, false, error.message)
            }
        })
    }

    override fun logout(callback: (Boolean, String?) -> Unit) {
        try {
            auth.signOut()
            callback(true, "Logout success")
        } catch (e: Exception) {
            callback(false, "Logout failed: ${e.message}")
        }
    }

    override fun uploadImages(imageName: String, imageUri: Uri, callback: (Boolean, String?, String?) -> Unit) {
        val imageReference = storageReference.child(imageName)
        imageReference.putFile(imageUri).addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                callback(true, uri.toString(), "Image uploaded successfully")
            }.addOnFailureListener { exception ->
                callback(false, null, "Failed to get image URL: ${exception.message}")
            }
        }.addOnFailureListener { exception ->
            callback(false, null, "Image upload failed: ${exception.message}")
        }
    }

    override fun updateUser(userId: String, data: MutableMap<String, Any?>, callback: (Boolean, String?) -> Unit) {
        reference.child(userId).updateChildren(data).addOnCompleteListener { res ->
            if (res.isSuccessful) {
                callback(true, "User updated")
            } else {
                callback(false, "Unable to update user: ${res.exception?.message}")
            }
        }
    }
}
