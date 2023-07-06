package com.example.myapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.airbnb.lottie.LottieAnimationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class SplashScreen : AppCompatActivity() {

//    private var isLoginDone:Boolean = false
    lateinit var user : UsersInfo
//    private lateinit var mySharedPreferences : SharedPreferences

    private lateinit var animationView: LottieAnimationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        animationView = findViewById(R.id.lottie_animation)

        animationView.playAnimation()

        if (getLoginStatus()) {
            Handler(Looper.getMainLooper()).postDelayed({
                val intentMain = Intent(this, MainActivity::class.java)
                startActivity(intentMain)
                finish()
            }, 5000)

        } else {

            Handler(Looper.getMainLooper()).postDelayed({
                val intentLogin = Intent(this, SignupSigninBasicScreen::class.java)
                startActivity(intentLogin)
                finish()
            }, 5000)

        }
    }

    private fun getLoginStatus() : Boolean{

        /*mySharedPreferences = this.getSharedPreferences("myAppPref", Context.MODE_PRIVATE)

        isLoginDone = mySharedPreferences.getBoolean("isLoginDone", false)

        return isLoginDone*/

        val myAppSharedPreference = this.getSharedPreferences("myAppPref", Context.MODE_PRIVATE)
         return myAppSharedPreference.getBoolean("isLoginDone",false)

    }

}