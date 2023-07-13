package com.example.myapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.myapp.utils.navigateFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Login : Fragment() {

    private lateinit var edEmail : EditText
    private lateinit var edPassword : EditText
    private lateinit var loginBtn : Button
    private lateinit var googleBtn : ImageView
    private lateinit var facebookBtn : ImageView
    private lateinit var twitterBtn : ImageView
    private lateinit var signup : TextView

    private lateinit var mAuth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        edEmail = view.findViewById(R.id.edemail_edit_text1)
        edPassword = view.findViewById(R.id.edpassword_edit_text1)
        loginBtn = view.findViewById(R.id.login_btn1)
        googleBtn = view.findViewById(R.id.google_btn1)
        facebookBtn = view.findViewById(R.id.facebook_btn1)
        twitterBtn = view.findViewById(R.id.twitter_btn1)
        signup = view.findViewById(R.id.text2_btn)

        mAuth = FirebaseAuth.getInstance()

        loginBtn.setOnClickListener {

            val enteredEmail = edEmail.text.toString()
            val enteredPassword = edPassword.text.toString()

            if (enteredEmail.isEmpty() || enteredPassword.isEmpty()) {

                showNoInputDialog()

            } else {

                mAuth.signInWithEmailAndPassword(enteredEmail, enteredPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            val dbRef = FirebaseDatabase.getInstance().reference
                            val userRef = dbRef.child("User").child(mAuth.uid.toString())
                            val updates = HashMap<String, Any>()
                            updates["loginDone"] = true // Set the new boolean value

                            userRef.updateChildren(updates)
                                .addOnSuccessListener {
                                    // Variable updated successfully

                                    val intentMain = Intent(requireActivity(), MainActivity::class.java)
                                    startActivity(intentMain)

                                    val intent = Intent(requireContext(), MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                }
                                .addOnFailureListener { e ->
                                    // Handle any errors that occurred
                                    e.printStackTrace()
                                }

                        } else {

                            showFalseInfo_showSignUp_Dialog()

                        }

                    }


            }

        }
        signup.setOnClickListener {

            view.navigateFragment(
                R.id.action_login_to_signup
            )

        }

        googleBtn.setOnClickListener {

            Toast.makeText(requireContext(), "Button Not Used", Toast.LENGTH_SHORT).show()

        }

        facebookBtn.setOnClickListener {

            Toast.makeText(requireContext(), "Button Not Used", Toast.LENGTH_SHORT).show()

        }

        twitterBtn.setOnClickListener {

            Toast.makeText(requireContext(), "Button Not Used", Toast.LENGTH_SHORT).show()

        }

        return view

    }

    companion object {
        fun newInstance() = Login()
    }

    private fun showFalseInfo_showSignUp_Dialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomDialogBox)
        alertDialogBuilder.setTitle("Wong Info")
        alertDialogBuilder.setMessage("Kindly Enter Correct Email & Password. If you don't have account kindly Signup.")

        alertDialogBuilder.setPositiveButton("OK"){dialog, which ->
            dialog.dismiss()

        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

    }

    private fun showNoInputDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomDialogBox)
        alertDialogBuilder.setTitle("Info Not Found")
        alertDialogBuilder.setMessage("Kindly Enter Email and Password.")

        alertDialogBuilder.setPositiveButton("OK"){dialog, which ->
            dialog.dismiss()

        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

}