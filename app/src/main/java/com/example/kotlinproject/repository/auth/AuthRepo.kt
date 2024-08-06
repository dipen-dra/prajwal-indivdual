package com.example.kotlinproject.repository.auth

import android.net.Uri
import com.example.kotlinproject.model.UserModel
import com.google.firebase.auth.FirebaseUser

interface AuthRepo {

    // User authentication methods
    /**
     * Log in a user with email and password.
     * @param email User's email address.
     * @param password User's password.
     * @param callback Callback with success status and error message.
     */
    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit)

    /**
     * Register a new user with email and password.
     * @param email User's email address.
     * @param password User's password.
     * @param callback Callback with success status, message, and user ID.
     */
    fun signUp(email: String, password: String, callback: (Boolean, String?, String?) -> Unit)

    // User data management methods
    /**
     * Add a new user to the database.
     * @param userId Unique ID of the user.
     * @param userModel User data model.
     * @param callback Callback with success status and message.
     */
    fun addUserToDatabase(userId: String, userModel: UserModel, callback: (Boolean, String?) -> Unit)

    /**
     * Send a password reset email to the user.
     * @param email User's email address.
     * @param callback Callback with success status and message.
     */
    fun forgotPassword(email: String, callback: (Boolean, String?) -> Unit)

    /**
     * Get the currently authenticated user.
     * @return The currently authenticated FirebaseUser, or null if no user is logged in.
     */
    fun getCurrentUser(): FirebaseUser?

    /**
     * Retrieve user data from Firebase.
     * @param userId Unique ID of the user.
     * @param callback Callback with user data, success status, and message.
     */
    fun getUserFromFirebase(userId: String, callback: (UserModel?, Boolean, String?) -> Unit)

    /**
     * Log out the current user.
     * @param callback Callback with success status and message.
     */
    fun logout(callback: (Boolean, String?) -> Unit)

    // File management method
    /**
     * Upload an image to Firebase Storage.
     * @param imageName Name of the image file.
     * @param imageUri URI of the image file.
     * @param callback Callback with success status, image URL, and message.
     */
    fun uploadImages(imageName: String, imageUri: Uri, callback: (Boolean, String?, String?) -> Unit)

    /**
     * Update user data in the database.
     * @param userId Unique ID of the user.
     * @param data Map of data to update.
     * @param callback Callback with success status and message.
     */
    fun updateUser(userId: String, data: MutableMap<String, Any?>, callback: (Boolean, String?) -> Unit)
}
