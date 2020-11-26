package com.saarthak.proteams

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.saarthak.proteams.constants.Constants.DocId
import com.saarthak.proteams.constants.Constants.FCM_TOKEN
import com.saarthak.proteams.constants.Constants.FCM_TOKEN_UPDATE
import com.saarthak.proteams.constants.Constants.SharedPref
import com.saarthak.proteams.model.Board
import com.saarthak.proteams.model.User
import com.saarthak.proteams.ui.IntroActivity
import com.saarthak.proteams.ui.adapters.BoardAdapter
import com.saarthak.proteams.util.BaseActivity
import com.saarthak.proteams.util.FireStoreClass
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.main_app_bar.*
import kotlinx.android.synthetic.main.main_nav_header.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object{
        private const val UPDATE_REQ_CODE = 1
        private const val LOAD_BOARD_REQ_CODE = 2
    }

    private lateinit var curUserName: String
    private lateinit var sharedPref: SharedPreferences

    private fun toggleDrawer(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START)
        else drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun setUpActionBar(){
        setSupportActionBar(main_toolBar)

        main_toolBar.setNavigationIcon(R.drawable.ic_menu)
        main_toolBar.setNavigationOnClickListener {
            // todo : toggle nav drawer
            toggleDrawer()
        }
    }

    fun updateUserDetails(curUser: User, fetchBoards: Boolean){
        hideProgDialog()

        if(fetchBoards){
            showProgDialog()
            FireStoreClass().getBoards(this)
        }
        curUserName = curUser.name

        Glide
            .with(this)
            .load(curUser.img)
            .centerCrop()
            .placeholder(R.drawable.ic_img_24)
            .into(nav_header_main_iv)

        nav_header_main_name_tv.text = curUser.name
    }

    fun showBoards(boards: ArrayList<Board>){
        if(boards.isNotEmpty()) {
            main_tv_noBoard.visibility = View.GONE
            main_rv.visibility = View.VISIBLE

            val adapter = BoardAdapter(this, boards)
            main_rv.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                setHasFixedSize(true)
                this.adapter = adapter
            }

            adapter.setOnClickListener(object: BoardAdapter.OnClickListener{
                override fun onClick(position: Int, board: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(DocId, board.docId)
                    startActivity(intent)
                }
            })
        }
        else {
            main_rv.visibility = View.GONE
            main_tv_noBoard.visibility = View.VISIBLE
        }
        hideProgDialog()
    }

    fun tokenUpdateSuccess(){
        val editor = sharedPref.edit()
        editor.putBoolean(FCM_TOKEN_UPDATE, true)
        editor.apply()

        FireStoreClass().signInUser(this, true)
    }

    private fun updateToken(token: String){
        val userData = HashMap<String, Any>()
        userData[FCM_TOKEN] = token

        showProgDialog()
        FireStoreClass().updateUserData(this, userData)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpActionBar()

        nav_view.setNavigationItemSelectedListener(this)

        sharedPref = this.getSharedPreferences(SharedPref, Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        val tokenUpdated = sharedPref.getBoolean(FCM_TOKEN_UPDATE, false)
        if(tokenUpdated) {
            showProgDialog()
            FireStoreClass().signInUser(this, true)
        }
        else{
            FirebaseMessaging.getInstance().token.addOnCompleteListener {task ->
                if(! task.isSuccessful) {
                    Log.d("Token", "onCreate: ${task.exception}")
                    return@addOnCompleteListener
                }

                val token = task.result
                updateToken(token.toString())
            }
        }

        FireStoreClass().signInUser(this, true)

        main_fab.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra("curUserName", curUserName)

            startActivityForResult(intent, LOAD_BOARD_REQ_CODE)
        }

    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START)
        else exit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.nav_myProfile -> {
                startActivityForResult(Intent(this, EditProfileActivity::class.java), UPDATE_REQ_CODE)
            }
            R.id.nav_signOut -> {
                FirebaseAuth.getInstance().signOut()

                sharedPref.edit().clear().apply()

                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

                startActivity(intent)
                finish()
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK){
            if(requestCode == UPDATE_REQ_CODE) FireStoreClass().signInUser(this)
            else if(requestCode == LOAD_BOARD_REQ_CODE) FireStoreClass().getBoards(this)
        }
    }
}