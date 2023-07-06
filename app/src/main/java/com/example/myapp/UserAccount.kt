package com.example.myapp

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import java.io.File

class UserAccount : Fragment() {

    private var selectedImageUri: Uri? = null

    private lateinit var mySharedPreference: SharedPreferences
    private var savedEmail: String = ""
    private var savedName: String = ""
    private lateinit var searchViewLayout: ConstraintLayout
    private val PICK_IMAGE_REQUEST = 1
    private var permissionsGranted = false

    private lateinit var changePic: Button
    private lateinit var profilePic: ImageView
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var reportProblem: Button

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intent = result?.data
                intent?.data?.let { uri ->
                    Glide.with(this@UserAccount).load(uri).into(profilePic)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_account, container, false)

        changePic = view.findViewById(R.id.change_img1)
        profilePic = view.findViewById(R.id.user_profile)
        reportProblem = view.findViewById(R.id.report_problem)

        mySharedPreference = requireActivity().getSharedPreferences(
            "myAppPref",
            Context.MODE_PRIVATE
        )

        selectedImageUri = mySharedPreference.getString("ProfileImageUri", "")?.toUri()

        val activity = activity as? MainActivity
        searchViewLayout =
            activity?.findViewById(R.id.search_drawer_linear_layout) ?: throw IllegalStateException(
                "TextView not found in activity"
            )

        // Hide the view
        searchViewLayout.visibility = View.GONE

        userName = view.findViewById(R.id.user_name)
        userEmail = view.findViewById(R.id.user_email)

        savedEmail = mySharedPreference.getString("Email", "").toString()
        savedName = mySharedPreference.getString("Complete Name", "").toString()

        userName.text = savedName
        userEmail.text = savedEmail

        if (permissionsGranted) {

            if (selectedImageUri.toString().isEmpty()) {

                if (changePic.isActivated) {

                    changeProfilePicture()

                } else {

                    profilePic.setImageResource(R.drawable.user_icon)

                }

            } else {

                profilePic.setImageURI(selectedImageUri)

            }

        } else {

            if (changePic.isActivated) {

                requestStoragePermission()

            } else {

                profilePic.setImageResource(R.drawable.user_icon)

            }

        }

        reportProblem.setOnClickListener {

            Toast.makeText(requireContext(), "Not Applied Yet", Toast.LENGTH_SHORT).show()

        }

        changePic.setOnClickListener {

            requestPermission()

        }

        if (mySharedPreference.getString("ProfileImageUri", "").equals("")) {

            profilePic.setImageURI(mySharedPreference.getString("ProfileImageUri", "")?.toUri())

        }

        return view

    }

    private fun requestPermission() {

        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val permissionTitle = "Permission Required"
        val permissionBody = "Please Enable permission from setting "

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    permission
                )
            ) {
                showRationaleDialog(permissionTitle, permissionBody) { _, _ ->
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(permission),
                        PICK_IMAGE_REQUEST
                    )
                }
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PICK_IMAGE_REQUEST
                )
            }
        } else {
            openGallery()

        }
    }

    private fun showRationaleDialog(
        permissionTitle: String,
        permisionBod: String,
        onClickListener: DialogInterface.OnClickListener
    ) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(permissionTitle)
            .setMessage(permisionBod)
            .setPositiveButton("OK",onClickListener)
            .setNegativeButton("Cancel",null)
            .create()
        dialog.show()
    }

    private fun openGallery() {
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        requestPermissionLauncher.launch(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == PICK_IMAGE_REQUEST){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery()
            }else{
                if(!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                    showSettingDialog()
                }else{

                }
            }

        }
    }

    private fun showSettingDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Permission")
            .setMessage("Give Permission")
            .setPositiveButton("OK",){ _,_ ->
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package",requireContext().packageName,null)
                intent.data = uri
                startActivityForResult(intent,121)
            }
            .setNegativeButton("Cancel",null)
            .create()
        dialog.show()
    }

    private fun changeProfilePicture() {

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)

    }

    private fun requestStoragePermission() {

       /* requestPermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )*/

    }

    companion object {

        fun newInstance() = UserAccount()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 121){
            requestPermission()
        }

//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
//
//            val imageUri: Uri? = data.data
//
//            imageUri.let {
//
//                if (it != null) {
//
//                    saveProfilePic(it)
//
//                }
//
//                mySharedPreference = requireActivity().getSharedPreferences(
//                    "myAppPref",
//                    Context.MODE_PRIVATE
//                )
//                mySharedPreference.edit().putString("ProfileImageUri", imageUri.toString()).apply()
//                profilePic.setImageURI(imageUri)
//
//            }
//
//        } else {
//
//            Toast.makeText(requireContext(), "Image Not Picked", Toast.LENGTH_SHORT).show()
//
//        }
    }

    private fun saveProfilePic(imageUri: Uri) {

        mySharedPreference.edit().putString("ProfileImageUri", imageUri.toString()).commit()

    }

    private fun getProfilePictureFile(): File {

        val directory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(directory, "profile_picture.jpg")

    }

}