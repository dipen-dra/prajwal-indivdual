package com.example.kotlinproject.ui.activity

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kotlinproject.R
import com.example.kotlinproject.databinding.ActivityEditProfileBinding
import com.example.kotlinproject.model.UserModel
import com.example.kotlinproject.repository.auth.AuthRepoImpl
import com.example.kotlinproject.utils.ImageUtils
import com.example.kotlinproject.utils.LoadingUtils
import com.example.kotlinproject.viewModel.AuthViewModel
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.UUID

class EditProfileActivity : AppCompatActivity() {
    lateinit var editProfileBinding: ActivityEditProfileBinding
    lateinit var imageUtils: ImageUtils
    var imageUri: Uri? = null
    lateinit var authViewModel: AuthViewModel
    lateinit var loadingUtils: LoadingUtils
    var imageName = ""
    var userId = ""
    var oldImageUrl = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        editProfileBinding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(editProfileBinding.root)
        imageUtils = ImageUtils(this)
        loadingUtils = LoadingUtils(this)

        var repo = AuthRepoImpl()
        authViewModel = AuthViewModel(repo)

        imageUtils.registerActivity { url ->
            url.let { it ->
                imageUri = it
                Picasso.get().load(it).into(editProfileBinding.profilePicture)
            }
        }


        var userData: UserModel? = intent.getParcelableExtra("userData")
        userData.let { users ->
            userId = users?.id.toString()
            imageName = if(imageName == null || imageName!!.isEmpty()){
                UUID.randomUUID().toString()
            }else{
                users?.imageName.toString()
            }
            oldImageUrl = users?.imageUrl.toString()
            editProfileBinding.emailEdit.setText(users?.email)
            editProfileBinding.addressEdit.setText(users?.address)
            editProfileBinding.fullNameEdit.setText(users?.fullname)
            if (users!!.imageUrl == null || users.imageUrl.isEmpty()) {
                editProfileBinding.profilePicture.setImageResource(R.drawable.profile)
            } else {
                Picasso.get().load(users.imageUrl).into(editProfileBinding.profilePicture)

            }
        }

        editProfileBinding.btnSave.setOnClickListener {
            if (imageUri == null) {
                // No new image selected, just update user data
                updateProduct(oldImageUrl)
            } else {
                // New image selected, upload it and then update user data
                uploadImage(imageName, imageUri!!) { success, downloadUrl, message ->
                    if (success) {
                        updateProduct(downloadUrl)
                    } else {
                        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        editProfileBinding.addPhotoIcon.setOnClickListener {
            imageUtils.launchGallery(this)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun uploadImage(imageName: String, imageUri: Uri, callback: (Boolean, String?, String?) -> Unit) {
        val storageReference = FirebaseStorage.getInstance().reference.child("images").child(imageName)

        val uploadTask = storageReference.putFile(imageUri)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Get the download URL
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                // Successfully got the download URL
                val downloadUrl = uri.toString()
                callback(true, downloadUrl, null)
            }.addOnFailureListener { exception ->
                // Handle any errors
                callback(false, null, exception.message)
            }
        }.addOnFailureListener { exception ->
            // Handle errors during upload
            callback(false, null, exception.message)
        }
    }

    private fun updateProduct(url: String?) {
        loadingUtils.showDialog()
        var updatedEmail : String = editProfileBinding.emailEdit.text.toString()
        var updatedAddress : String = editProfileBinding.addressEdit.text.toString()
        var updateFullName : String = editProfileBinding.fullNameEdit.text.toString()

        var updatedMap = mutableMapOf<String,Any?>()

        updatedMap["address"] = updatedAddress
        updatedMap["fullname"] = updateFullName
        updatedMap["email"] = updatedEmail
        updatedMap["imageName"] = imageName.toString()
        updatedMap["imageUrl"] = url.toString()

        authViewModel.updateUser(userId,updatedMap){
                success,message->
            if(success){
                loadingUtils.dismiss()
                Toast.makeText(applicationContext,message,Toast.LENGTH_LONG).show()
                finish()
            }else{
                loadingUtils.dismiss()
                Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()
            }
        }
    }
}