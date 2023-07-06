package com.example.myapp

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.example.myapp.utils.navigateFragment
import kotlinx.coroutines.*
import okhttp3.Dispatcher
import java.util.regex.Pattern

class Signup : Fragment() {

//    private lateinit var mySharedPreference : SharedPreferences
    private var isLoginDone : Boolean = false

    private lateinit var edCompleteName : EditText
    private lateinit var edEmail : EditText
    private lateinit var edPassword : EditText
    private lateinit var edRepeatPassword : EditText
    private lateinit var signupBtn : Button
    private lateinit var loginBtn : TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

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

//                                            updateLoginStatus(isLoginDone)

                                            val myAppSharedPreference = requireContext().getSharedPreferences("myAppPref", Context.MODE_PRIVATE)
                                            myAppSharedPreference.edit().putBoolean("isLoginDone", isLoginDone).apply()
                                            myAppSharedPreference.edit().putString("Email", enteredEmail).apply()
                                            myAppSharedPreference.edit().putString("Complete Name", enteredName).apply()
//                                            updateEmail(enteredEmail)


                                            val  appDataBase = Room.databaseBuilder(requireContext(),
                                                MyAppDataBase::class.java, "MyAppDB").build()

                                            val userDao = appDataBase.userInfoDao()

                                            val newUser = UsersInfo(completeName = enteredName, email = enteredEmail,
                                            password = enteredPassword, id = 0)

                                            GlobalScope.launch(Dispatchers.IO) {

                                                userDao.insertUserInfo(newUser)

                                            }

                                            view.navigateFragment(
                                                R.id.action_signup_to_login
                                            )
                                            Toast.makeText(requireContext(), "Registration Successful.", Toast.LENGTH_SHORT).show()

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

    private fun updateLoginStatus(isLoginDone: Boolean) {

        /*mySharedPreference = requireActivity().getSharedPreferences("myAppPref", Context.MODE_PRIVATE)
        val editor = mySharedPreference.edit()
        editor.apply {

            putBoolean("isLoginDone", isLoginDone)

        }.apply()*/



    }

//    private fun updateEmail(email: String) {

        /*mySharedPreference = requireActivity().getSharedPreferences("myAppPref", Context.MODE_PRIVATE)
        val editor = mySharedPreference.edit()
        editor.apply {

            putString("Email", email)

        }.apply()*/

//        val myAppSharedPreference = requireContext().getSharedPreferences("myAppPref", Context.MODE_PRIVATE)
//        myAppSharedPreference.edit().putString("Email", email).apply()

//    }

/*    private fun isEmailValid(email: String): Boolean {
        val regexPattern = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,})$")
        return regexPattern.matches(email)
    }*/

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