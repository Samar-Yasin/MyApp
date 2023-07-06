package com.example.myapp

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.myapp.utils.navigateFragment

class SignupOrSignin : Fragment() {


    private lateinit var signinBtn: Button
    private lateinit var signupBtn: Button
    private lateinit var googleBtn: ImageView
    private lateinit var twitterBtn: ImageView
    private lateinit var facebookBtn: ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_signup_or_signin, container, false)

        signinBtn = view.findViewById(R.id.signin_btn)
        signupBtn = view.findViewById(R.id.signup_btn)
        googleBtn = view.findViewById(R.id.google_btn)
        twitterBtn = view.findViewById(R.id.twitter_btn)
        facebookBtn = view.findViewById(R.id.facebook_btn)

        signinBtn.setOnClickListener {

            view.navigateFragment(
                R.id.action_signupOrSignin_to_login
            )

        }

        signupBtn.setOnClickListener {

            view.navigateFragment(
                R.id.action_signupOrSignin_to_signup
            )

        }

        googleBtn.setOnClickListener {

            Toast.makeText(requireContext(),"Button is not applied", Toast.LENGTH_SHORT).show()

        }

        facebookBtn.setOnClickListener {

            Toast.makeText(requireContext(),"Button is not applied", Toast.LENGTH_SHORT).show()

        }

        twitterBtn.setOnClickListener {

            Toast.makeText(requireContext(),"Button is not applied", Toast.LENGTH_SHORT).show()

        }

        return view
    }
    companion object {
        fun newInstance() = SignupOrSignin()
    }

}