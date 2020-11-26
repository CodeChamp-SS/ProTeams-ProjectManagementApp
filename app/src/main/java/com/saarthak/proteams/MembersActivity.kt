package com.saarthak.proteams

import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.saarthak.proteams.constants.Constants.FCM_AUTHORIZATION
import com.saarthak.proteams.constants.Constants.FCM_BASE_URL
import com.saarthak.proteams.constants.Constants.FCM_KEY
import com.saarthak.proteams.constants.Constants.FCM_KEY_DATA
import com.saarthak.proteams.constants.Constants.FCM_KEY_MSG
import com.saarthak.proteams.constants.Constants.FCM_KEY_TITLE
import com.saarthak.proteams.constants.Constants.FCM_KEY_TO
import com.saarthak.proteams.constants.Constants.FCM_SERVER_KEY
import com.saarthak.proteams.model.Board
import com.saarthak.proteams.model.User
import com.saarthak.proteams.ui.adapters.MembersAdapter
import com.saarthak.proteams.util.BaseActivity
import com.saarthak.proteams.util.FireStoreClass
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.add_member_dialog.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MembersActivity : BaseActivity() {

    companion object {
        private const val TAG = "Notification"
    }

    private lateinit var boardDetails: Board
    private lateinit var assignedMembers: ArrayList<User>
    private var changesMade = false

    private inner class SendNotificationToUserAsync(val boardName: String, val token: String) :
        AsyncTask<Any, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            showProgDialog()
        }

        override fun doInBackground(vararg params: Any?): String {
            var res = ""
            var connection: HttpURLConnection? = null
            try {
                val url = URL(FCM_BASE_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.apply {
                    doInput = true
                    doOutput = true
                    instanceFollowRedirects = false
                    requestMethod = "POST"

                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("charset", "utf-8")
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty(FCM_AUTHORIZATION, "$FCM_KEY=$FCM_SERVER_KEY")

                    useCaches = false
                }

                val dos = DataOutputStream(connection.outputStream)

                val jsonReq = JSONObject()
                val dataObj = JSONObject()
                dataObj.put(FCM_KEY_TITLE, "New Board $boardName")
                dataObj.put(
                    FCM_KEY_MSG,
                    "You've been assigned to the board $boardName by ${assignedMembers[0].name} !"
                )
                jsonReq.put(FCM_KEY_DATA, dataObj)
                jsonReq.put(FCM_KEY_TO, token)

                Log.d(TAG, "doInBackground: ${jsonReq.toString()}")

                dos.writeBytes(jsonReq.toString())
                dos.flush()
                dos.close()

                val httpRes = connection.responseCode
                if (httpRes == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))

                    Log.d(TAG, "doInBackground: response code OK")

                    val sb = StringBuilder()
                    var s: String?
                    try {
                        while (reader.readLine().also { s = it } != null) sb.append(s + '\n')
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        try {
                            inputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                    res = sb.toString()
                } else {
                    Log.d(TAG, "doInBackground: response code NOT OK")
                    res = connection.responseMessage
                }

            } catch (e: SocketTimeoutException) {
                res = "Connection Timed Out ! Please try again"
            } catch (e: Exception) {
                res = e.message.toString()
            } finally {
                connection?.disconnect()
            }

            Log.d(TAG, "doInBackground: $res")
            return res
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            hideProgDialog()
        }
    }

    private fun setUpActBar() {
        setSupportActionBar(memb_toolbar)

        val actBar = supportActionBar
        if (actBar != null) {
            actBar.setDisplayHomeAsUpEnabled(true)
            actBar.setHomeAsUpIndicator(R.drawable.ic_back)
        }

        memb_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun showAddMemberDialog() {
        val dialog = Dialog(this)
        dialog.apply {
            setContentView(R.layout.add_member_dialog)
            addYes_tv.setOnClickListener {
                val mail = addMail_tv.text.toString()
                if (mail.isNotEmpty()) {
                    dismiss()
                    showProgDialog()
                    FireStoreClass().getMembersDetails(this@MembersActivity, mail)
                } else Toast.makeText(
                    this@MembersActivity,
                    "Please enter an Email Id to search !",
                    Toast.LENGTH_SHORT
                ).show()
            }
            addNo_tv.setOnClickListener {
                dismiss()
            }
            show()
        }
    }

    fun setUpMembersList(members: ArrayList<User>) {
        hideProgDialog()
        assignedMembers = members

        val adapter = MembersAdapter(this, members)
        members_rv.apply {
            layoutManager = LinearLayoutManager(this@MembersActivity)
            setHasFixedSize(true)
            this.adapter = adapter
        }
    }

    fun memberDetails(user: User) {
        boardDetails.assignedTo.add(user.id)
        FireStoreClass().assignMemberToBoard(this, boardDetails, user)
    }

    fun assignMemberSuccess(user: User) {
        hideProgDialog()
        changesMade = true
        assignedMembers.add(user)
        setUpMembersList(assignedMembers)

        SendNotificationToUserAsync(boardDetails.name, user.fcmToken).execute()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        if (intent.hasExtra("boardDetails")) boardDetails =
            intent.getParcelableExtra("boardDetails")!!

        setUpActBar()

        showProgDialog()
        FireStoreClass().getAssignedMembersDetails(this, boardDetails.assignedTo)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (changesMade) setResult(RESULT_OK)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_member_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addMembers -> {
                showAddMemberDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}