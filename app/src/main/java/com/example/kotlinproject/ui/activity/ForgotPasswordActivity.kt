package com.example.kotlinproject.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kotlinproject.R
import com.example.kotlinproject.databinding.ActivityForgotPasswordBinding
import com.example.kotlinproject.repository.auth.AuthRepoImpl
import com.example.kotlinproject.utils.LoadingUtils
import com.example.kotlinproject.viewModel.AuthViewModel

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var forgotPasswordActivityBinding: ActivityForgotPasswordBinding
    lateinit var authViewModel: AuthViewModel
    lateinit var loadingUtils: LoadingUtils


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        forgotPasswordActivityBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(forgotPasswordActivityBinding.root)

        var repo = AuthRepoImpl()
        authViewModel = AuthViewModel(repo)
        loadingUtils = LoadingUtils(this)
        forgotPasswordActivityBinding.btnForget.setOnClickListener {
            loadingUtils.showDialog()
            var email = forgotPasswordActivityBinding.editEmail.text.toString()

            authViewModel.forgotPassword(email){
                success, message ->
                if(success){
                    loadingUtils.dismiss()
                    Toast.makeText(applicationContext,message, Toast.LENGTH_LONG).show()
                    finish()
                }else{
                    loadingUtils.dismiss()
                    Toast.makeText(applicationContext,message, Toast.LENGTH_LONG).show()
                }
            }

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}