package com.example.myapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.room.Room
import com.example.myapp.model.UpComingMoviesModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), UpComingMoviesFragmentInterface, TopRatedMoviesFragmentInterface,
    PopularMoviesFragmentInterface, NavigationDrawerClickListener  {

    private var tag: String = ""

    private var isLoginDone : Boolean = false

//    private lateinit var mySharedPreference : SharedPreferences

    private lateinit var bottom_nevigation : BottomNavigationView

    private lateinit var tollbar : Toolbar

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var navigationView: NavigationView

    private lateinit var searchedMoviesList : ArrayList<UpComingMoviesModel.Result>

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

        /* if (searchView.isNotEmpty()) {
            var searchJob: Job? = null
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }
                override fun onQueryTextChange(p0: String?): Boolean {
                    searchJob?.cancel()
                    searchJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(1000L)
                        Log.d(TAG, "onQueryTextChange: ")
                        if(tag.equals("Popular",true)){
                            searchForPopularMovies(p0!!)
                        }else if(tag.equals("Top",true)){
                            searchForTopMovies(p0!!)
                        }else if(tag.equals("upComing",true)){
                            searchForUpComingMovies(p0!!)
                        }
                    }
                    return true
                }
            })
        }*/

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

            /*if(searchView.text.isNotEmpty()){
            searchJob?.cancel()
            searchView.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    TODO("Not yet implemented")
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    searchJob?.cancel()
                    val searchText = p0.toString()
                    if (tag.equals("Popular", true)) {
                        searchForPopularMovies(searchText)
                    } else if (tag.equals("Top", true)) {
                        searchForTopMovies(searchText)
                    } else if (tag.equals("upComing", true)) {
                        searchForUpComingMovies(searchText)
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                    TODO("Not yet implemented")
                }
            })
        }*/

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

                        searchedMoviesList = response.body()!!.results as ArrayList<UpComingMoviesModel.Result>

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

                        searchedMoviesList = response.body()!!.results as ArrayList<UpComingMoviesModel.Result>

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

                        searchedMoviesList = response.body()!!.results as ArrayList<UpComingMoviesModel.Result>

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

    private fun showDeleteAccountDialog(){

        val alertDialogBuilder = AlertDialog.Builder(this, R.style.CustomDialogBox)

        alertDialogBuilder.setTitle("Delete Account")

        alertDialogBuilder.setMessage("Are you sure you want to Delete Account")

        alertDialogBuilder.setPositiveButton("Yes"){dialog, which ->

            performDeleteAccount()

            dialog.dismiss()

        }

        alertDialogBuilder.setNegativeButton("No"){dialog, which ->

            dialog.dismiss()

        }

        val alertDialog = alertDialogBuilder.create()

        alertDialog.show()
    }

    private fun showLogoutAlertDialog() {

        val alertDialogBuilder = AlertDialog.Builder(this, R.style.CustomDialogBox)

        alertDialogBuilder.setTitle("Logout")

        alertDialogBuilder.setMessage("Are you sure you want to logout")

        alertDialogBuilder.setPositiveButton("Yes"){dialog, which ->

            performLogout()

            dialog.dismiss()

        }

        alertDialogBuilder.setNegativeButton("No"){dialog, which ->

            dialog.dismiss()

        }

        val alertDialog = alertDialogBuilder.create()

        alertDialog.show()

    }

    private fun performLogout(){

        updateLoginStatus(false)

    }

    private fun performDeleteAccount(){

        showInfoVerificationDialog()

    }

    private fun showInfoVerificationDialog(){

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

    fun navigationMethod(){
        navigationView.setNavigationItemSelectedListener {menuItem ->

            when(menuItem.itemId){
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

    private fun showFalseInfoDialog(){

        val alertDialogBuilder = AlertDialog.Builder(this, R.style.CustomDialogBox)

        alertDialogBuilder.setTitle("False Info")

        alertDialogBuilder.setMessage("Information provided is wrong.")

        alertDialogBuilder.setPositiveButton("Ok"){dialog, which ->

            performLogout()

        }

        val alertDialog = alertDialogBuilder.create()

        alertDialog.show()

    }

    private fun verifyEmailAndPassword(_email: String, _password: String){

        val email = _email

        val password = _password

        val appDataBase = Room.databaseBuilder(

            applicationContext,

            MyAppDataBase::class.java, "MyAppDB"

        ).build()

        val userDao = appDataBase.userInfoDao()

        CoroutineScope(Dispatchers.IO).launch {

            val userAbc = userDao.getUserInfoByEmail(email)

            withContext(Dispatchers.Main) {

                if(userAbc == null){

                    showFalseInfoDialog()

                } else {

                    val savedEmail = userAbc.email

                    val savedPassword = userAbc.password

                    if(savedEmail != email || savedPassword != password){

                        showFalseInfoDialog()

                    }else{

                        Toast.makeText(applicationContext, "Verification Successful.", Toast.LENGTH_SHORT).show()

                        userDao.deleterUserInfo(userAbc)

                        clearSharedPreferences()

                        Toast.makeText(applicationContext, "Account Deleted.", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@MainActivity, SignupSigninBasicScreen::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)

                    }

                }

            }

        }

    }

    private fun clearSharedPreferences(){

       /* mySharedPreference = this.getSharedPreferences("myAppPref", Context.MODE_PRIVATE)

        val editor = mySharedPreference.edit()

        editor.remove("Email")
        editor.remove("isLoginDone")

        editor.apply()*/

        val myAppSharedPreference = this.getSharedPreferences("myAppPref", Context.MODE_PRIVATE)
        myAppSharedPreference.edit().remove("isLoginDone").apply()
        myAppSharedPreference.edit().remove("Email").apply()

    }

    private fun updateLoginStatus(boolean: Boolean) {

        /*mySharedPreference = this.getSharedPreferences("myAppPref", Context.MODE_PRIVATE)

        val editor = mySharedPreference.edit()

        editor.apply {

            putBoolean("isLoginDone", boolean)

        }.apply()*/


        val myAppSharedPreference = getSharedPreferences("myAppPref", Context.MODE_PRIVATE)

        if (myAppSharedPreference.edit().putBoolean("isLoginDone", boolean).commit())

        {

            val intent = Intent(this, SignupSigninBasicScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        }else{

            Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show()

        }

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

    private fun updatePassword(oldPassword: String, newPassword : String){

        /*mySharedPreference = this.getSharedPreferences("myAppPref", Context.MODE_PRIVATE)

        val email = mySharedPreference.getString("Email", "")

*/

        val myAppSharedPreference = this.getSharedPreferences("myAppPref", Context.MODE_PRIVATE)
        val email = myAppSharedPreference.getString("Email","")

        val appDataBase = Room.databaseBuilder(

            applicationContext,

            MyAppDataBase::class.java, "MyAppDB"

        ).build()

        val userDao = appDataBase.userInfoDao()

        CoroutineScope(Dispatchers.IO).launch {

            val user = userDao.getUserInfoByEmail(email!!)

            withContext(Dispatchers.Main) {

                if(user == null){

                    showFalseInfoDialog()

                } else {

                    val savedEmail = user.email

                    val savedPassword = user.password

                    val newPass = newPassword

                    userDao.updatePassword(savedEmail,newPass)

                    Toast.makeText(this@MainActivity, "Password Updated ", Toast.LENGTH_SHORT).show()

                }

            }

        }

    }

    private fun showPasswordVerificationDialog(){

        val dialogBuilder = AlertDialog.Builder(this, R.style.CustomDialogBox)

        dialogBuilder.setTitle("Password Verification")

        // Inflate a layout for the dialog

        val inflater = LayoutInflater.from(this)

        val dialogView = inflater.inflate(R.layout.dialog_password_verification, null)

        // Get references to the EditText fields in the dialog

        val oldPasswordEditText = dialogView.findViewById<EditText>(R.id.old_password_edit_text_field)

        val newPasswordEditText = dialogView.findViewById<EditText>(R.id.new_password_edit_text_field)

        dialogBuilder.setView(dialogView)

        dialogBuilder.setPositiveButton("Verify") { dialog, which ->

            val oldPassword = oldPasswordEditText.text.toString()

            val newPassword = newPasswordEditText.text.toString()

            // Call the verification function with the email and password

            updatePassword(oldPassword, newPassword)

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