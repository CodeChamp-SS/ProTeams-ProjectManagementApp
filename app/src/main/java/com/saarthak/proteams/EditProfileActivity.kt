package com.saarthak.proteams

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import com.saarthak.proteams.constants.Constants
import com.saarthak.proteams.constants.Constants.Image
import com.saarthak.proteams.constants.Constants.Mobile
import com.saarthak.proteams.constants.Constants.Name
import com.saarthak.proteams.model.User
import com.saarthak.proteams.util.BaseActivity
import com.saarthak.proteams.util.FireStoreClass
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : BaseActivity() {

    private var imgUri: Uri? = null
    private var downImgUri: String = ""

    private lateinit var userDetails: User

    private val storageReference = FirebaseStorage.getInstance()

    private fun setUpActionBar(){
        setSupportActionBar(ep_toolbar)

        val actBar = supportActionBar
        if(actBar != null){
            actBar.setDisplayHomeAsUpEnabled(true)
            actBar.setHomeAsUpIndicator(R.drawable.ic_back)
        }

        ep_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun showData(curUser: User){
        userDetails = curUser

        ep_name_et.setText(curUser.name)
        ep_email_et.setText(curUser.email)
        if(curUser.mobile != -1L) ep_mob_et.setText(curUser.mobile.toString())

        Glide
            .with(this)
            .load(curUser.img)
            .centerCrop()
            .placeholder(R.drawable.ic_edit_img)
            .into(edit_iv)
    }

    private fun uploadImg(){
        showProgDialog()

        if(imgUri != null){
            val ref = storageReference.reference.child("UserImg_${Timestamp.now().seconds}.jpeg")
            ref.putFile(imgUri!!)
                .addOnSuccessListener {
                    hideProgDialog()
//                    Log.d("img url", "uploadImg: " + it.metadata!!.reference!!.downloadUrl.toString())

                    it.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener {
                            downImgUri = it.toString()
//                            Log.d("img uri", "uploadImg: $it")

                            // todo: update user profile

                            updateProfile()

                        }
                }
                .addOnFailureListener{
                    hideProgDialog()
                    showErrorSnackBar("${it.message}, Please try again later !")
                }
        }
    }

    fun updateProfile(){
        val user = HashMap<String, Any>()

        if(downImgUri.isNotEmpty() && !TextUtils.equals(userDetails.img, downImgUri)){
            user[Image] = downImgUri
        }

        if(! ep_name_et.text.toString().equals(userDetails.name)) user[Name] = ep_name_et.text.toString()
        if(! ep_mob_et.text.toString().equals(userDetails.mobile.toString())) user[Mobile] = ep_mob_et.text.toString().toLong()

        FireStoreClass().updateUserData(this, user)
    }

    fun profileUpdateSuccess(){
        hideProgDialog()
        setResult(RESULT_OK)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        setUpActionBar()

        FireStoreClass().signInUser(this)

        edit_iv.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                // todo : get image from storage
                getImage(this)
            }
            else{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_CODE)
            }
        }

        ep_updateB.setOnClickListener {
            if(imgUri != null) uploadImg()
            else{
                showProgDialog()
                updateProfile()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == Constants.READ_STORAGE_CODE && grantResults.isNotEmpty()){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // todo : get image from storage
                getImage(this)
            }
        }
        else{
            showErrorSnackBar("Permission Denied !")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK && requestCode == Constants.IMAGE_REQ_CODE){
            if(data!!.data != null){
                imgUri = data.data!!

                edit_iv.setImageURI(imgUri)
            }
        }
    }
}