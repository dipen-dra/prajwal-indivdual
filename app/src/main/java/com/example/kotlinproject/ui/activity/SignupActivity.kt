package com.example.kotlinproject.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kotlinproject.R
import com.example.kotlinproject.databinding.ActivitySignupBinding
import com.example.kotlinproject.model.UserModel
import com.example.kotlinproject.repository.auth.AuthRepoImpl
import com.example.kotlinproject.utils.LoadingUtils
import com.example.kotlinproject.viewModel.AuthViewModel

class SignupActivity : AppCompatActivity() {
    lateinit var signupActivityBinding: ActivitySignupBinding
    lateinit var authViewModel: AuthViewModel
    lateinit var loadingUtils: LoadingUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        signupActivityBinding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(signupActivityBinding.root)

        var repo = AuthRepoImpl()
        authViewModel = AuthViewModel(repo)

        loadingUtils = LoadingUtils(this)



        signupActivityBinding.btnSignUp.setOnClickListener {
            loadingUtils.showDialog()

            var email: String = signupActivityBinding.emailSignup.text.toString()
            var password: String = signupActivityBinding.passwordSign.text.toString()
            var confirmPassword: String = signupActivityBinding.confirmSign.text.toString()
            var name: String = signupActivityBinding.fullSignup.text.toString()
            var address: String = signupActivityBinding.addressSign.text.toString()

            var userModel = UserModel(
                id = "",
                email = email,
                fullname = name,
                address = address,
                imageName = "",
                imageUrl = ""
            )
            authViewModel.signUp(email, password) { success, message, userId ->
                if (success) {
                    addUserToDatabase(userId, userModel)
                } else {
                    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                    loadingUtils.dismiss()
                }

            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun addUserToDatabase(userId: String?, userModel: UserModel) {
        authViewModel.addUserToDatabase(userId.toString(), userModel) { success, message ->
            if (success) {

                Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                finish()

            } else {
                Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                loadingUtils.dismiss()
            }

        }
    }
}





