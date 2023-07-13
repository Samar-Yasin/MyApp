package com.example.myapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapp.model.UserDetailModel
import com.example.myapp.utils.navigateFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class Signup : Fragment() {

    private lateinit var edCompleteName : EditText
    private lateinit var edEmail : EditText
    private lateinit var edPassword : EditText
    private lateinit var edRepeatPassword : EditText
    private lateinit var signupBtn : Button
    private lateinit var loginBtn : TextView

    private lateinit var mAuth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        mAuth = Firebase.auth

        edCompleteName = view.findViewById(R.id.complete_name_edit_text_field3)
        edEmail = view.findViewById(R.id.edemail_edit_text3)
        edPassword = view.findViewById(R.id.edpassword_edit_text3)
        edRepeatPassword = view.findViewById(R.id.repeat_edpassword_edit_text3)
        signupBtn = view.findViewById(R.id.signup_btn3)
        loginBtn = view.findViewById(R.id.text_btn3)

        signupBtn.setOnClickListener {
            val enteredName = edCompleteName.text.toString()
            val enteredEmail = edEmail.text.toString()
            val enteredPassword = edPassword.text.toString()
            val enterPasswordAgain = edRepeatPassword.text.toString()

            if(enteredName.isEmpty()){

                showMissingNameDialog()

            }else{

                if(enteredName.length <= 3){

                    showProperNameDialog()

                }else{

                    if(isNameValid(enteredName)){

                        if(enteredEmail.isEmpty()){

                            showMissingEmailDialog()

                        }else{

                            if(isEmailValid(enteredEmail)){

                                if(enteredPassword.isEmpty()){

                                    showMissingPasswordDialog()

                                }else{

                                    if(isValidPassword(enteredPassword)){

                                        if(enteredPassword == enterPasswordAgain){

                                            mAuth.createUserWithEmailAndPassword(enteredEmail, enteredPassword)
                                                .addOnCompleteListener { task ->

                                                    if(task.isSuccessful){
                                                        val dbRef = FirebaseDatabase.getInstance().reference
                                                        val model = UserDetailModel(
                                                            enteredName,
                                                            enteredEmail,
                                                            mAuth.uid.toString(),
                                                            false,
                                                            ""
                                                        )
                                                        dbRef.child("User").child(mAuth.uid.toString()).setValue(model)
                                                        Toast.makeText(requireContext(), "Signup Successful", Toast.LENGTH_SHORT).show()

                                                        view.navigateFragment(
                                                            R.id.action_signup_to_login
                                                        )

                                                    }else{

                                                        Toast.makeText(requireContext(), "User Addition Failed", Toast.LENGTH_SHORT).show()

                                                    }

                                                }.addOnFailureListener { exception ->

                                                    exception.printStackTrace()

                                                }

                                        }else{

                                            showDifferentPasswordDialog()

                                        }

                                    }else{

                                        showInvalidPasswordDialog()

                                    }

                                }

                            }else{

                                showInvalidEmailDialog()

                            }

                        }

                    }else{

                        showInvalidNameDialog()

                    }

                }

            }

        }

        loginBtn.setOnClickListener {

            view.navigateFragment(
                R.id.action_signup_to_login
            )

        }

        return view

    }

    private fun showProperNameDialog() {

        val alertDialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomDialogBox)
        alertDialogBuilder.setTitle("Wrong Name")
        alertDialogBuilder.setMessage("Kindly Enter Your Proper Name.")

        alertDialogBuilder.setPositiveButton("OK"){dialog, which ->
            dialog.dismiss()

        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

    }

    private fun showMissingPasswordDialog() {

        val alertDialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomDialogBox)
        alertDialogBuilder.setTitle("Password Missing")
        alertDialogBuilder.setMessage("Kindly Enter Your Password.")

        alertDialogBuilder.setPositiveButton("OK"){dialog, which ->
            dialog.dismiss()

        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

    }

    private fun showMissingEmailDialog() {

        val alertDialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomDialogBox)
        alertDialogBuilder.setTitle("Email Missing")
        alertDialogBuilder.setMessage("Kindly Enter Your Email.")

        alertDialogBuilder.setPositiveButton("OK"){dialog, which ->
            dialog.dismiss()

        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

    }

    private fun showInvalidNameDialog() {

        val alertDialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomDialogBox)
        alertDialogBuilder.setTitle("Invalid Name")
        alertDialogBuilder.setMessage("Name should only contain alphabets.")

        alertDialogBuilder.setPositiveButton("OK"){dialog, which ->
            dialog.dismiss()

        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

    }

    private fun showInvalidPasswordDialog() {

        val alertDialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomDialogBox)
        alertDialogBuilder.setTitle("Weak Password")
        alertDialogBuilder.setMessage("Password must be at least 8 characters.\n" +
                "It must have at least 1 lowercase and at least 1 uppercase letter.\n" +
                "It must have one special character like ! or + or - or similar\n" +
                "It must have at least 1 digit")

        alertDialogBuilder.setPositiveButton("OK"){dialog, which ->
            dialog.dismiss()

        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

    }

    private fun showInvalidEmailDialog() {

        val alertDialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomDialogBox)
        alertDialogBuilder.setTitle("Invalid Email")
        alertDialogBuilder.setMessage("Kindly Enter Correct Email.")

        alertDialogBuilder.setPositiveButton("OK"){dialog, which ->
            dialog.dismiss()

        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

    }

    private fun showDifferentPasswordDialog() {

        val alertDialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomDialogBox)
        alertDialogBuilder.setTitle("Password Mismatch")
        alertDialogBuilder.setMessage("Kindly Enter Same Password.")

        alertDialogBuilder.setPositiveButton("OK"){dialog, which ->
            dialog.dismiss()

        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

    }

    private fun showMissingNameDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomDialogBox)
        alertDialogBuilder.setTitle("Name Missing")
        alertDialogBuilder.setMessage("Kindly Enter Your Name.")

        alertDialogBuilder.setPositiveButton("OK"){dialog, which ->
            dialog.dismiss()

        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    companion object {

        fun newInstance() = Signup()

    }

    private fun isEmailValid(email : String): Boolean {

        val emailRegex = Regex("^[a-zA-Z]+(?:[._-][a-zA-Z]+)?[0-9]*@[a-zA-Z]+(?:\\.[a-zA-Z]{2,3})+$")

        if(email.length < 10){

            return false

        }else{

            return email.matches(emailRegex)

        }

    }

    private fun isValidPassword(password : String): Boolean {
        if (password.length < 8) return false
        if (password.filter { it.isDigit() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isUpperCase() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isLowerCase() }.firstOrNull() == null) return false
        if (password.filter { !it.isLetterOrDigit() }.firstOrNull() == null) return false

        return true
    }

    private fun isNameValid(name : String): Boolean{
            val regex = "^[a-zA-Z\\s]+\$" // Regular expression pattern for valid name
            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher(name)
            return matcher.matches()
    }

}