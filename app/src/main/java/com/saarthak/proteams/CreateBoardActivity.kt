package com.saarthak.proteams

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import com.saarthak.proteams.constants.Constants
import com.saarthak.proteams.model.Board
import com.saarthak.proteams.util.BaseActivity
import com.saarthak.proteams.util.FireStoreClass
import kotlinx.android.synthetic.main.activity_create_board.*

class CreateBoardActivity : BaseActivity() {

    private var imgUri: Uri? = null
    private var imgUrl: String = ""

    private lateinit var curUserName: String

    private val storageReference = FirebaseStorage.getInstance()

    private fun setUpActBar(){
        setSupportActionBar(cb_toolbar)

        val actBar = supportActionBar
        if(actBar != null){
            actBar.setDisplayHomeAsUpEnabled(true)
            actBar.setHomeAsUpIndicator(R.drawable.ic_back)
        }

        cb_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun createBoard(){
        val assignedUsers = ArrayList<String>()
        assignedUsers.add(getCurUserId())

        val name = cb_name_et.text.toString()
        if(name.isNotEmpty()){
            val board = Board(name, imgUrl, curUserName, assignedUsers)
            FireStoreClass().createBoard(this, board)
        }
        else {
            hideProgDialog()
            Toast.makeText(this, "Board Name can't be empty !!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadBoardImg(){
        showProgDialog()

        if(imgUri != null){
            val ref = storageReference.reference.child("BoardImg_${Timestamp.now().seconds}.jpeg")

            ref.putFile(imgUri!!)
                .addOnSuccessListener {
                    it.metadata!!.reference!!
                        .downloadUrl
                        .addOnSuccessListener {
                            imgUrl = it.toString()
                            createBoard()
                        }
                }
                .addOnFailureListener{
                    hideProgDialog()
                    showErrorSnackBar("${it.message}, Please try again later !")
                }
        }

    }

    fun createBoardSuccess(){
        hideProgDialog()
        setResult(RESULT_OK)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)

        if(intent.hasExtra("curUserName")) curUserName = intent.getStringExtra("curUserName")!!

        setUpActBar()

        cb_iv.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                // todo : get image from storage
                getImage(this)
            }
            else{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_CODE)
            }
        }

        cb_createB.setOnClickListener {
            if(imgUri != null) uploadBoardImg()
            else{
                showProgDialog()
                createBoard()
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

                cb_iv.setImageURI(imgUri)
            }
        }
    }
}