package com.saarthak.proteams.util

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.saarthak.proteams.R
import com.saarthak.proteams.constants.Constants

open class BaseActivity : AppCompatActivity() {

    private var doubleBackPressed = false

    private lateinit var progDialog: Dialog

    fun showProgDialog(){
        progDialog = Dialog(this)

        progDialog.setContentView(R.layout.progress_dialog)
        progDialog.setCancelable(false)
        progDialog.show()
    }

    fun hideProgDialog(){
        progDialog.hide()
    }

    fun getCurUserId(): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun getImage(activity: Activity){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(intent, Constants.IMAGE_REQ_CODE)
    }

    fun exit(){
        if(doubleBackPressed){
            super.onBackPressed()
            return
        }

        doubleBackPressed = true
        Snackbar.make(findViewById(android.R.id.content), "Press Back Again to Exit", Snackbar.LENGTH_SHORT).show()

        Handler().postDelayed({
            doubleBackPressed = false
        }, 2000)
    }

    fun showErrorSnackBar(msg: String){
        val snackbar = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)

        // todo : change color of snackbar's bg
        val snackBarView = snackbar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.snackBarErrorColor))

        snackbar.show()
    }

}