package com.saarthak.proteams

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.saarthak.proteams.model.User
import com.saarthak.proteams.util.BaseActivity
import com.saarthak.proteams.util.FireStoreClass
import kotlinx.android.synthetic.main.activity_signin.*

class SigninActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth

//    private val db = FirebaseFirestore.getInstance()
//    private val collectionReference = db.collection("Users")

    fun signInSuccess(user: User){
        Log.d("sign in", "Success !")

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun signInUser(){

        val email = signin_email_et.text.toString().trim()
        val pass = signin_pass_et.text.toString().trim()

        if(validateForm(email, pass)){

            showProgDialog()

            auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener {
                    hideProgDialog()

                    if (it.isSuccessful) {
//                        Toast.makeText(this, "successfully signed in !", Toast.LENGTH_SHORT).show()
                        FireStoreClass().signInUser(this)
                    } else showErrorSnackBar("Invalid Email Id or Password !")
                }
                .addOnFailureListener {
                    hideProgDialog()
//                    showErrorSnackBar(it.message.toString())
                    showErrorSnackBar("Invalid Email Id or Password !")
                }

        }

    }

    private fun validateForm(email: String, pass: String): Boolean{
//        val e = TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)
//
//        if(e) showErrorSnackBar("Fields can't be Empty !")
//
//        return !e

        var b = true;

        if(TextUtils.isEmpty(email)){
            signin_email_et.error = "Please enter your Email Id"
            b = false
        }
        if(TextUtils.isEmpty(pass)){
            signin_pass_et.error = "Please enter a Password"
            b = false
        }

        return b
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setSupportActionBar(signin_act_toolbar)

        val toolBar = supportActionBar
        toolBar!!.title = "Sign In"
        toolBar.setDisplayHomeAsUpEnabled(true)
        toolBar.setHomeAsUpIndicator(R.drawable.ic_back_b)

        auth = FirebaseAuth.getInstance()

        signin_act_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        signin_act_B.setOnClickListener {
            signInUser()
        }

    }
}