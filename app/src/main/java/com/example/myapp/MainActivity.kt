package com.example.myapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.myapp.model.UpComingMoviesModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), UpComingMoviesFragmentInterface, TopRatedMoviesFragmentInterface,
    PopularMoviesFragmentInterface, NavigationDrawerClickListener {

    private var tag: String = ""

    private lateinit var mAuth: FirebaseAuth

    private lateinit var userEmail : String

    private lateinit var bottom_nevigation: BottomNavigationView

    private lateinit var tollbar: Toolbar

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var navigationView: NavigationView

    private lateinit var searchedMoviesList: ArrayList<UpComingMoviesModel.Result>

    private lateinit var searchView: EditText

    var callbackUp_coming: ((UpComingMoviesModel) -> Unit)? = null
    var callbackPo_pular: ((UpComingMoviesModel) -> Unit)? = null
    var callbackTop_Rated: ((UpComingMoviesModel) -> Unit)? = null
    private var upComingMoviesFragment: UpComingMoviesFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_nevigation = findViewById(R.id.bottom_navigation)
        tollbar = findViewById(R.id.toolbar_drawer)
        navigationView = findViewById(R.id.navigation_view)

        mAuth = FirebaseAuth.getInstance()

        setSupportActionBar(tollbar)

        drawerLayout = findViewById(R.id.main_content)

        toggle = ActionBarDrawerToggle(
            parent,
            drawerLayout,
            tollbar,
            R.string.nav_open,
            R.string.nav_close
        )

        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigationMethod()

        searchView = findViewById(R.id.search_view)

        var searchJob: Job? = null

        searchView.addTextChangedListener(afterTextChanged = {

            Log.d(TAG, "onTextChanged: Count...1$it")

            searchJob = CoroutineScope(Dispatchers.Main).launch {

                val searchText = it.toString()

                if (tag.equals("Popular", true)) {

                    searchForPopularMovies(searchText)

                } else if (tag.equals("Top", true)) {

                    searchForTopMovies(searchText)

                } else if (tag.equals("upComing", true)) {

                    searchForUpComingMovies(searchText)

                }

            }

        })

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val toolbar = supportActionBar

        upComingMoviesFragment = UpComingMoviesFragment.newInstance()

        loadFragmentReplace("UpComing", upComingMoviesFragment!!)

        tag = "UpComing"

        bottom_nevigation.setOnItemSelectedListener { item ->

            val fragment: Fragment

            when (item.itemId) {

                R.id.upcoming_movies -> {
                    toolbar?.title = "UpComing"
                    tag = "UpComing"
                    fragment = UpComingMoviesFragment()
                    loadFragmentReplace(tag, fragment)
                    true
                }

                R.id.popular -> {
                    toolbar?.title = "Popular"
                    tag = "Popular"
                    fragment = PopularMoviesFragment()
                    loadFragmentReplace(tag, fragment)
                    true
                }

                R.id.top_rated -> {
                    toolbar?.title = "TopRated"
                    tag = "Top"
                    fragment = TopRatedMoviesFragment()
                    loadFragmentReplace(tag, fragment)
                    true
                }

                R.id.user_account -> {
                    toolbar?.title = "Account"
                    tag = "User"
                    fragment = UserAccount()
                    loadFragmentReplace(tag, fragment)
                    true
                }

                else -> false

            }

        }

    }

    private fun searchForUpComingMovies(p0: String) {

        val retrofitData = APIsInterface.create().getSearchedMoviesData(p0, false, "en-US", 1)

        retrofitData.enqueue(object : Callback<UpComingMoviesModel> {

            override fun onResponse(
                call: Call<UpComingMoviesModel>,
                response: Response<UpComingMoviesModel>
            ) {

                if (response.code() == 200) {

                    try {

                        val result = response.body()!!

                        callbackUp_coming!!.invoke(result)
                        //moviesViewModel.searchedMoviesList = response.body()!!.results as ArrayList<UpComingMoviesModel.Result>

                        searchedMoviesList =
                            response.body()!!.results as ArrayList<UpComingMoviesModel.Result>

                    } catch (e: Exception) {

                        e.printStackTrace()

                    }

                } else {

                    Log.d(TAG, "Cancel: ${response.body()}")

                    call.cancel()

                }

            }

            override fun onFailure(call: Call<UpComingMoviesModel>, t: Throwable) {

                Log.v("Retrofit", "Call Failed")

            }
        })

    }

    private fun searchForTopMovies(p0: String) {

        val retrofitData = APIsInterface.create().getSearchedMoviesData(p0, false, "en-US", 1)

        retrofitData.enqueue(object : Callback<UpComingMoviesModel> {

            override fun onResponse(
                call: Call<UpComingMoviesModel>,
                response: Response<UpComingMoviesModel>

            ) {

                if (response.code() == 200) {

                    try {
                        //success

                        val result = response.body()!!

                        callbackTop_Rated!!.invoke(result)
                        //moviesViewModel.searchedMoviesList = response.body()!!.results as ArrayList<UpComingMoviesModel.Result>

                        searchedMoviesList =
                            response.body()!!.results as ArrayList<UpComingMoviesModel.Result>

                    } catch (e: Exception) {

                        e.printStackTrace()

                    }

                } else {

                    Log.d(TAG, "Cancel: ${response.body()}")

                    call.cancel()

                }

            }

            override fun onFailure(call: Call<UpComingMoviesModel>, t: Throwable) {

                Log.v("Retrofit", "Call Failed")

            }

        })

    }

    private fun searchForPopularMovies(p0: String) {

        val retrofitData = APIsInterface.create().getSearchedMoviesData(p0, false, "en-US", 1)

        retrofitData.enqueue(object : Callback<UpComingMoviesModel> {

            override fun onResponse(

                call: Call<UpComingMoviesModel>,

                response: Response<UpComingMoviesModel>

            ) {

                if (response.code() == 200) {

                    try {

                        val result = response.body()!!

                        callbackPo_pular!!.invoke(result)

                        //moviesViewModel.searchedMoviesList = response.body()!!.results as ArrayList<UpComingMoviesModel.Result>

                        searchedMoviesList =
                            response.body()!!.results as ArrayList<UpComingMoviesModel.Result>

                    } catch (e: Exception) {

                        e.printStackTrace()

                    }

                } else {

                    Log.d(TAG, "Cancel: ${response.body()}")

                    call.cancel()

                }

            }

            override fun onFailure(call: Call<UpComingMoviesModel>, t: Throwable) {

                Log.v("Retrofit", "Call Failed")

            }

        })

    }

    private fun showDeleteAccountDialog() {

        val alertDialogBuilder = AlertDialog.Builder(this, R.style.CustomDialogBox)

        alertDialogBuilder.setTitle("Delete Account")

        alertDialogBuilder.setMessage("Are you sure you want to Delete Account")

        alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->

            performDeleteAccount()
            dialog.dismiss()

        }

        alertDialogBuilder.setNegativeButton("No") { dialog, which ->

            dialog.dismiss()

        }

        val alertDialog = alertDialogBuilder.create()

        alertDialog.show()

    }

    private fun showLogoutAlertDialog() {

        val alertDialogBuilder = AlertDialog.Builder(this, R.style.CustomDialogBox)

        alertDialogBuilder.setTitle("Logout")
        alertDialogBuilder.setMessage("Are you sure you want to logout")

        alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->

            performLogout()
            dialog.dismiss()

        }

        alertDialogBuilder.setNegativeButton("No") { dialog, which ->

            dialog.dismiss()

        }

        val alertDialog = alertDialogBuilder.create()

        alertDialog.show()

    }

    private fun performLogout() {

        updateLoginStatus()

    }

    private fun performDeleteAccount() {

        showInfoVerificationDialog()

    }

    private fun showInfoVerificationDialog() {

        val dialogBuilder = AlertDialog.Builder(this, R.style.CustomDialogBox)

        dialogBuilder.setTitle("Email & Password Verification")

        // Inflate a layout for the dialog

        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_info_verification, null)

        // Get references to the EditText fields in the dialog

        val emailEditText = dialogView.findViewById<EditText>(R.id.email_edit_text_field4)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.password_edit_text_field4)

        dialogBuilder.setView(dialogView)
        dialogBuilder.setPositiveButton("Verify") { dialog, which ->

            val dialogEmailEditText = emailEditText.text.toString()
            userEmail = dialogEmailEditText
            val dialogPasswordEditText = passwordEditText.text.toString()

            // Call the verification function with the email and password

            verifyEmailAndPassword(dialogEmailEditText, dialogPasswordEditText)

        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->

            dialog.dismiss()

        }

        val dialog = dialogBuilder.create()

        dialog.show()

    }

    private fun navigationMethod() {
        navigationView.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.logout -> {
                    logout()
                    true
                }

                R.id.delete_account -> {
                    deleteAccount()
                    true
                }

                R.id.change_password -> {
                    changePassword()
                    true
                }

                else -> false

            }

        }

    }

    private fun showFalseInfoDialog() {

        val alertDialogBuilder = AlertDialog.Builder(this, R.style.CustomDialogBox)

        alertDialogBuilder.setTitle("False Info")

        alertDialogBuilder.setMessage("Information provided is wrong.")

        alertDialogBuilder.setPositiveButton("Ok") { dialog, which ->

            performLogout()

        }

        val alertDialog = alertDialogBuilder.create()

        alertDialog.show()

    }

    private fun verifyEmailAndPassword(_email: String, _password: String) {

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val authCredential : AuthCredential = EmailAuthProvider.getCredential(_email, _password)

        firebaseUser?.reauthenticate(authCredential)?.addOnCompleteListener{task ->
            Log.d(TAG, "AuthenticateTheUserSuccess: $task ")
            val userId = mAuth.currentUser?.uid

            if(task.isSuccessful){

                Toast.makeText(
                    applicationContext,
                    "Verification Successful.",
                    Toast.LENGTH_SHORT
                ).show()

                FirebaseDatabase.getInstance().getReference("User")
                    .child(userId!!)
                    .removeValue()

                val user = Firebase.auth.currentUser
                user?.delete()
                    ?.addOnCompleteListener { task1 ->

                        if(task1.isSuccessful){

                            Toast.makeText(
                                applicationContext,
                                "Account Deleted.",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(this@MainActivity, SignupSigninBasicScreen::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)

                        }else{

                            Toast.makeText(
                                applicationContext,
                                "Something went wrong...",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                    }?.addOnFailureListener {

                        it.printStackTrace()

                    }

            }else{

                showFalseInfoDialog()

            }

        }?.addOnFailureListener {
            Log.d(TAG, "AuthenticateTheUserFailure: $it ")
            it.printStackTrace()

        }

    }

    private fun updateLoginStatus() {

        val userId = mAuth.currentUser?.uid
        val database : DatabaseReference = Firebase.database.reference
        database.child("User").child(userId!!).child("loginDone").setValue(false)

        FirebaseAuth.getInstance().signOut()

        Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, SignupSigninBasicScreen::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }

    private fun loadFragmentReplace(tag: String,fragment: Fragment) {

        // load fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_container, fragment,tag)
            .commit()

    }

    companion object {

        const val TAG = "MainActivity"

    }

    private fun showPasswordUpdateDialog(){

        val alertDialogBuilder = AlertDialog.Builder(this, R.style.CustomDialogBox)

        alertDialogBuilder.setTitle("Change Password")

        alertDialogBuilder.setMessage("Are you sure you want to Change Password?")

        alertDialogBuilder.setPositiveButton("Yes"){dialog, which ->

            UpdatePassword()

            dialog.dismiss()

        }

        alertDialogBuilder.setNegativeButton("No"){dialog, which ->

            dialog.dismiss()

        }

        val alertDialog = alertDialogBuilder.create()

        alertDialog.show()

    }

    private fun UpdatePassword(){

        showPasswordVerificationDialog()

    }

    private fun updatePassword(email : String, oldPassword: String, newPassword : String){

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val authCredential : AuthCredential = EmailAuthProvider.getCredential(email, oldPassword)

        firebaseUser?.reauthenticate(authCredential)?.addOnCompleteListener { task ->

            if(task.isSuccessful){

                Toast.makeText(
                    applicationContext,
                    "Verification Successful.",
                    Toast.LENGTH_SHORT
                ).show()

                val user = Firebase.auth.currentUser

                user?.updatePassword(newPassword)?.addOnCompleteListener { task1 ->

                    if(task1.isSuccessful){

                        Toast.makeText(
                            applicationContext,
                            "Password Updated Successful.",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                }?.addOnFailureListener {

                    it.printStackTrace()
                    Toast.makeText(
                        applicationContext,
                        "Password Update Failed",
                        Toast.LENGTH_SHORT
                    ).show()

                }

            }else{

                Toast.makeText(
                    applicationContext,
                    "Authentication Failed.",
                    Toast.LENGTH_SHORT
                ).show()

            }

        }?.addOnFailureListener {

            it.printStackTrace()

        }

    }

    private fun showPasswordVerificationDialog(){

        val dialogBuilder = AlertDialog.Builder(this, R.style.CustomDialogBox)

        dialogBuilder.setTitle("Password Verification")

        // Inflate a layout for the dialog

        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_password_verification, null)

        // Get references to the EditText fields in the dialog

        val dialogEmail = dialogView.findViewById<EditText>(R.id.email_edit_text_field11)
        val oldPasswordEditText = dialogView.findViewById<EditText>(R.id.old_password_edit_text_field)
        val newPasswordEditText = dialogView.findViewById<EditText>(R.id.new_password_edit_text_field)

        dialogBuilder.setView(dialogView)

        dialogBuilder.setPositiveButton("Verify") { dialog, which ->

            val email = dialogEmail.text.toString()
            val oldPassword = oldPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()

            // Call the verification function with the email and password

            updatePassword(email, oldPassword, newPassword)

        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->

            dialog.dismiss()

        }

        val dialog = dialogBuilder.create()

        dialog.show()

    }

    override fun attachInstanceUpComing(

        /*activeFragment: String,*/

        callBackFromFrag: (UpComingMoviesModel) -> Unit

    ) {

        this.callbackUp_coming = callBackFromFrag

    }

    override fun attachInstancePopular(

        /*activeFragment: String,*/

        callBackFromFrag: (UpComingMoviesModel) -> Unit

    ) {

        this.callbackPo_pular = callBackFromFrag

    }

    override fun attachInstanceTopRated(

        /*activeFragment: String,*/

        callBackFromFrag: (UpComingMoviesModel) -> Unit

    ) {

        this.callbackTop_Rated = callBackFromFrag

    }

    override fun logout() {

        showLogoutAlertDialog()

    }

    override fun deleteAccount(){

        showDeleteAccountDialog()

    }

    override fun changePassword() {

        showPasswordUpdateDialog()

    }

}

interface UpComingMoviesFragmentInterface {

    fun attachInstanceUpComing(/*activeFragment: String,*/ callBackFromFrag: (UpComingMoviesModel) -> Unit)

}

interface PopularMoviesFragmentInterface {

    fun attachInstancePopular(/*activeFragment: String,*/ callBackFromFrag: (UpComingMoviesModel) -> Unit)

}

interface TopRatedMoviesFragmentInterface {

    fun attachInstanceTopRated(/*activeFragment: String,*/ callBackFromFrag: (UpComingMoviesModel) -> Unit)

}

interface NavigationDrawerClickListener {

    fun logout()

    fun deleteAccount()

    fun changePassword()

}