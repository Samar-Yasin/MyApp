package com.example.myapp

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
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
import com.bumptech.glide.request.RequestOptions
import com.example.myapp.model.UserDetailModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserAccount : Fragment() {

    private var savedEmail: String = ""
    private var savedName: String = ""
    private lateinit var searchViewLayout: ConstraintLayout
    private val PICK_IMAGE_REQUEST = 1

    private lateinit var changePic: Button
    private lateinit var profilePic: ImageView
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var reportProblem: Button
    private var savedProfilePicUri: String = ""

    private lateinit var mAuth: FirebaseAuth

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == RESULT_OK) {

                val picIntent = result?.data!!
                picIntent.data?.let { uri ->

                    savedProfilePicUri = uri.toString()

                    Glide.with(this@UserAccount)
                        .load(savedProfilePicUri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profilePic)

                    val dbRef = Firebase.database.reference
                    val userRef = dbRef.child("User").child(mAuth.uid.toString())
                    val updates = HashMap<String, Any>()
                    updates["profilePicURL"] = savedProfilePicUri

                    userRef.updateChildren(updates)

                }

            }

        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_account, container, false)

        mAuth = Firebase.auth

        changePic = view.findViewById(R.id.change_img1)
        profilePic = view.findViewById(R.id.user_profile)
        reportProblem = view.findViewById(R.id.report_problem)

        val activity = activity as? MainActivity
        searchViewLayout =
            activity?.findViewById(R.id.search_drawer_linear_layout) ?: throw IllegalStateException(
                "TextView not found in activity"
            )

        // Hide the view
        searchViewLayout.visibility = View.GONE

        userName = view.findViewById(R.id.user_name)
        userEmail = view.findViewById(R.id.user_email)

        getUserDetail()

        reportProblem.setOnClickListener {

            Toast.makeText(requireContext(), "Not Applied Yet", Toast.LENGTH_SHORT).show()

        }

        changePic.setOnClickListener {

            requestPermission()

        }

        return view

    }

    private fun getUserDetail() {

        val database : DatabaseReference = Firebase.database.reference
        val userId = mAuth.currentUser?.uid
        database.child("User").child(userId!!).get().addOnSuccessListener {

            val user: UserDetailModel? = it.getValue(UserDetailModel::class.java)

            savedName = user!!.completeName
            savedEmail = user.email
            savedProfilePicUri = user.profilePicURL

            userName.text = savedName
            userEmail.text = savedEmail

            savedPicLoad(savedProfilePicUri.toUri())


        }.addOnFailureListener {

            it.printStackTrace()

        }

    }

    private fun requestPermission() {

        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val permissionTitle = "Permission Required"
        val permissionBody = "Storage Permission needed to upload Profile Picture!"

        if (ContextCompat.checkSelfPermission(requireContext(), permission)
            != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    permission
                )
            ) {

                showRationaleDialog(permissionTitle, permissionBody) { _, _ ->

                    showSettingDialog()

                }

            } else {

                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(permission),
                    PICK_IMAGE_REQUEST
                )

            }

        } else {

            openGallery()

        }

    }

    private fun showSettingDialog() {

        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomDialogBox)
            .setTitle("Permission")
            .setMessage("Go to App Storage Permission Settings...?")
            .setPositiveButton("Yes",) { _, _ ->

                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri

                startActivityForResult(intent, 121)

            }
            .setNegativeButton("No", null)
            .create()

        dialog.show()

    }

    private fun showRationaleDialog(
        permissionTitle: String, permissionBody: String,
        onClickListener: DialogInterface.OnClickListener
    ) {

        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomDialogBox)
            .setTitle(permissionTitle)
            .setMessage(permissionBody)
            .setPositiveButton("OK", onClickListener)
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    private fun openGallery() {

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        requestPermissionLauncher.launch(intent)

    }

    companion object {
        fun newInstance() = UserAccount()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 121 && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                savedProfilePicUri = uri.toString()

                Glide.with(this@UserAccount)
                    .load(uri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePic)

                val dbRef = Firebase.database.reference
                val userRef = dbRef.child("User").child(mAuth.uid.toString())
                val updates = HashMap<String, Any>()
                updates["profilePicURL"] = savedProfilePicUri

                userRef.updateChildren(updates)
                    .addOnCompleteListener {

                        if(it.isSuccessful){

                            Toast.makeText(requireContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show()

                        }else{

                            Toast.makeText(requireContext(), "Profile Update Failed", Toast.LENGTH_SHORT).show()

                        }

                    }.addOnFailureListener {

                        it.printStackTrace()

                    }


            }

        }

    }

    private fun savedPicLoad(uri : Uri) {


        if(!uri.toString().isEmpty()){

            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {

                requestPermission()

            } else {

                Glide.with(this@UserAccount)
                    .load(uri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePic)

            }

        }else{

            profilePic.setImageResource(R.drawable.user_icon)

        }

    }

}