package com.example.myapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.example.myapp.utils.navigateFragment
import kotlinx.coroutines.*

class Login : Fragment() {

    private var isLoginDone : Boolean = false
//    private lateinit var mySharedPreference : SharedPreferences
    private var user : UsersInfo? = null
    private  var savedEmail : String =""
    private  var savedPassword : String =""

    private lateinit var edEmail : EditText
    private lateinit var edPassword : EditText
    private lateinit var loginBtn : Button
    private lateinit var googleBtn : ImageView
    private lateinit var facebookeBtn : ImageView
    private lateinit var twitterBtn : ImageView
    private lateinit var signup : TextView
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
        facebookeBtn = view.findViewById(R.id.facebook_btn1)
        twitterBtn = view.findViewById(R.id.twitter_btn1)
        signup = view.findViewById(R.id.text2_btn)

        loginBtn.setOnClickListener {

            val enteredEmail = edEmail.text.toString()
            val enteredPassword = edPassword.text.toString()

            if (enteredEmail.isEmpty() || enteredPassword.isEmpty()) {

                showNoInputDialog()

            } else {

                val email = enteredEmail

                val appDataBase = Room.databaseBuilder(
                    requireContext(),
                    MyAppDataBase::class.java, "MyAppDB"
                ).build()

                val userDao = appDataBase.userInfoDao()

                CoroutineScope(Dispatchers.IO).launch {

                    user = userDao.getUserInfoByEmail(email)

                    withContext(Dispatchers.Main) {

                        if (user == null) {

                            showFalseInfo_showSignUp_Dialog()

                        } else {
                            savedEmail = user!!.email
                            savedPassword = user!!.password

                            if (savedEmail == enteredEmail && savedPassword == enteredPassword) {


//                                updateLoginStatus(isLoginDone)

                                val myAppSharedPreference = requireContext().getSharedPreferences("myAppPref", Context.MODE_PRIVATE)
                                myAppSharedPreference.edit().putBoolean("isLoginDone", true).apply()

                                val intentMain = Intent(requireActivity(), MainActivity::class.java)
                                startActivity(intentMain)

                                val intent = Intent(requireContext(), MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)

                            } else {

                                showFalseInfo_showSignUp_Dialog()

                            }

                        }

                    }

                }

                /*mySharedPreference = requireActivity().getSharedPreferences(
                    "myAppPref",
                    Context.MODE_PRIVATE
                )
                isLoginDone = mySharedPreference.getBoolean("isLoginDone", false)

                if (savedEmail == enteredEmail && savedPassword == enteredPassword) {

                    isLoginDone = true
                    updateData()

                    mySharedPreference = requireActivity().getSharedPreferences(
                        "myAppPref",
                        Context.MODE_PRIVATE
                    )
                    val editor = mySharedPreference.edit()
                    editor.apply {
                        putBoolean("isLoginDone", isLoginDone)
                    }.apply()

                    val intentMain = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intentMain)


                } else {

                    showFalseInfo_showSignUp_Dialog()

                }*/
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

        facebookeBtn.setOnClickListener {

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

    /*private fun updateLoginStatus(isLoginDone: Boolean) {

        mySharedPreference = requireActivity().getSharedPreferences("myAppPref", Context.MODE_PRIVATE)

        val editor = remySharedPreference.edit()
        editor.apply {

            putBoolean("isLoginDone", isLoginDone)

        }.apply()

    }*/

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