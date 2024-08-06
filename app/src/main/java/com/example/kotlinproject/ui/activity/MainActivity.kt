package com.example.kotlinproject.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kotlinproject.R
import com.example.kotlinproject.databinding.ActivityMainBinding
import com.example.kotlinproject.repository.auth.AuthRepoImpl
import com.example.kotlinproject.viewModel.AuthViewModel

class MainActivity : AppCompatActivity() {

    lateinit var mainActivityBinding: ActivityMainBinding
    lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)

        var repo= AuthRepoImpl()
        authViewModel = AuthViewModel(repo)
        var currentUser = authViewModel.getCurrentUser()

        Handler(Looper.getMainLooper()).postDelayed({
            if(currentUser == null){
                var intent = Intent(this@MainActivity,LoginActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                var intent = Intent(this@MainActivity,NavigationActivity::class.java)
                startActivity(intent)
                finish()
            }

        },4000)



//        mainActivityBinding.startBtn.setOnClickListener {
//            var intent = Intent(this@MainActivity,LoginActivity::class.java)
//            startActivity(intent)
//            finish()
//        }





        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}