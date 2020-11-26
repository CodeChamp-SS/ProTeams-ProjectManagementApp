package com.saarthak.proteams

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.saarthak.proteams.model.User
import com.saarthak.proteams.util.BaseActivity
import com.saarthak.proteams.util.FireStoreClass
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : BaseActivity() {

    private lateinit var auth : FirebaseAuth

    private lateinit var curUser: FirebaseUser

    fun regUserSuccess(){
        Log.d("success", "Successfully signed up !")

        auth.signOut()

        startActivity(Intent(this, SigninActivity::class.java))
        finish()
    }

    private fun regUser(){
        val name = signup_name_et.text.toString().trim()
        val email = signup_email_et.text.toString().trim()
        val pass = signup_pass_et.text.toString().trim()

        if(validateForm(name, email, pass)){

            showProgDialog()

            // todo : signup
            auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener {
                    hideProgDialog()

                    if(it.isSuccessful){
                        curUser = it.result!!.user!!

                        val user = User(curUser.uid, name, curUser.email!!)

                        FireStoreClass().registerUser(this, user)

//                        finish()

//                        Toast.makeText(this, "Successfully signed up with email id: ${user.email}!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener{
                    hideProgDialog()
                    showErrorSnackBar(it.message.toString())
                }
        }
    }

    private fun validateForm(name: String, email: String, pass: String): Boolean{
//        val e = TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)
//
//        if(e) showErrorSnackBar("Fields can't be Empty !")
//
//        return !e

        var b = true

        if(TextUtils.isEmpty(name)){
            signup_name_et.error = "Please enter your Name"
            b = false
        }
        if(TextUtils.isEmpty(email)){
            signup_email_et.error = "Please enter your Email Id"
            b = false
        }
        if(TextUtils.isEmpty(pass)){
            signup_pass_et.error = "Please enter a Password"
            b = false
        }

        return b
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setSupportActionBar(signup_act_toolbar)

        val actionBar = supportActionBar
        actionBar!!.title = "SIGN UP"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back_b)

        auth = FirebaseAuth.getInstance()

        signup_act_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        signup_act_B.setOnClickListener {
            regUser()
        }

    }
}