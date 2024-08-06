package com.example.kotlinproject.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kotlinproject.R
import com.example.kotlinproject.databinding.ActivityLoginBinding
import com.example.kotlinproject.repository.auth.AuthRepoImpl
import com.example.kotlinproject.utils.LoadingUtils
import com.example.kotlinproject.viewModel.AuthViewModel

class LoginActivity : AppCompatActivity() {

    lateinit var loginActivityBinding: ActivityLoginBinding
    lateinit var authViewModel: AuthViewModel
    lateinit var loadingUtils: LoadingUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loginActivityBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginActivityBinding.root)

        var repo = AuthRepoImpl()
        authViewModel = AuthViewModel(repo)
        loadingUtils = LoadingUtils(this)

        loginActivityBinding.buttonLogin.setOnClickListener{
            loadingUtils.showDialog()
            var email:String = loginActivityBinding.emailLoginInput.text.toString()
            var password:String = loginActivityBinding.passwordLoginInput.text.toString()

            authViewModel.login(email,password){
                success,message->
                if(success){
                    Toast.makeText(applicationContext,message,Toast.LENGTH_LONG).show()
                    loadingUtils.dismiss()
                    var intent = Intent(this@LoginActivity,NavigationActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(applicationContext,message,Toast.LENGTH_LONG).show()
                    loadingUtils.dismiss()
                }
            }



        }

        loginActivityBinding.signupText.setOnClickListener {
            var intent = Intent(this@LoginActivity,SignupActivity::class.java)
            startActivity(intent)
        }
        loginActivityBinding.forgotPassword.setOnClickListener{
            var intent = Intent(this@LoginActivity,ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}