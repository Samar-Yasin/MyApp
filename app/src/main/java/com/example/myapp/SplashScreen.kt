package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.myapp.model.UserDetailModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SplashScreen : AppCompatActivity() {

    private lateinit var animationView: LottieAnimationView
    private lateinit var mAuth: FirebaseAuth
    private var isLoggedIn: Boolean? = false
    lateinit var intentMain: Intent
    lateinit var intentLogin: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        intentMain = Intent(this, MainActivity::class.java)
        intentLogin = Intent(this, SignupSigninBasicScreen::class.java)

        animationView = findViewById(R.id.lottie_animation)
        mAuth = FirebaseAuth.getInstance()
        animationView.playAnimation()

        checkLoginStatus()

    }

    private fun checkLoginStatus() {

        val database : DatabaseReference = Firebase.database.reference
        val userId = mAuth.currentUser?.uid

        if(userId != null){

        database.child("User").child(userId).get().addOnSuccessListener {

            val user : UserDetailModel? = it.getValue(UserDetailModel::class.java)

                isLoggedIn = user?.loginDone

                if (isLoggedIn!!) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(intentMain)
                        finish()
                    }, 5000)

                } else {

                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(intentLogin)
                        finish()
                    }, 5000)

                }

            }.addOnFailureListener {

            it.printStackTrace()

        }

        }else{

            startActivity(intentLogin)

        }

    }

}