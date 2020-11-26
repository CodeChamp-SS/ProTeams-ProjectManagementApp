package com.saarthak.proteams.util

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.saarthak.proteams.*
import com.saarthak.proteams.constants.Constants.AssnTo
import com.saarthak.proteams.constants.Constants.Boards
import com.saarthak.proteams.constants.Constants.Email
import com.saarthak.proteams.constants.Constants.Id
import com.saarthak.proteams.constants.Constants.TaskList
import com.saarthak.proteams.constants.Constants.Users
import com.saarthak.proteams.model.Board
import com.saarthak.proteams.model.User

class FireStoreClass {

    private val TAG = "FireStoreClass"

    private val db = FirebaseFirestore.getInstance()
    private val collectionReference1 = db.collection(Users)
    private val collectionReference2 = db.collection(Boards)

    fun registerUser(activity: SignupActivity, userInfo: User) {
        collectionReference1.document(getCurUserId()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.regUserSuccess()
            }
            .addOnFailureListener {
                activity.showErrorSnackBar("Sign Up failed, Please try again later !")
            }
    }

    fun signInUser(activity: Activity, fetchBoards: Boolean = false) {

        collectionReference1.document(getCurUserId())
            .get()
            .addOnSuccessListener {
                val curUser = it.toObject(User::class.java)

                if (curUser != null) {

                    when (activity) {
                        is SigninActivity -> {
                            activity.signInSuccess(curUser)
                        }

                        is MainActivity -> {
                            activity.updateUserDetails(curUser, fetchBoards)
                        }

                        is EditProfileActivity -> {
                            activity.showData(curUser)
                        }
                    }

//                    Log.d(TAG, "signInUser: " + user.name + "\n" + user.email)
                }
            }
            .addOnFailureListener {

                when (activity) {
                    is SigninActivity -> {
                        activity.showErrorSnackBar("Sign In failed, Please try again later !")
                    }

                    is MainActivity -> {
                        activity.showErrorSnackBar("Sign In failed, Please try again later !")
                    }

                    is EditProfileActivity -> {
                        activity.showErrorSnackBar("Sign In failed, Please try again later !")
                    }
                }

            }

    }

    fun updateUserData(activity: Activity, data: HashMap<String, Any>) {
        collectionReference1.document(getCurUserId())
            .update(data)
            .addOnSuccessListener {
                Toast.makeText(activity, "Profile Updated Successfully !", Toast.LENGTH_SHORT)
                    .show()

                when(activity){
                    is EditProfileActivity ->{
                        activity.profileUpdateSuccess()
                    }
                    is MainActivity ->{
                        activity.tokenUpdateSuccess()
                    }
                }

            }
            .addOnFailureListener {
                when(activity){
                    is EditProfileActivity ->{
                        activity.hideProgDialog()
                        activity.showErrorSnackBar("Failed, Please try again later !")
                    }
                    is MainActivity ->{
                        activity.hideProgDialog()
                        activity.showErrorSnackBar("Failed, Please try again later !")
                    }
                }

                Log.d(TAG, "updateUserData: ${it.message}")
            }
    }

    fun createBoard(activity: CreateBoardActivity, board: Board) {

        collectionReference2.document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(activity, "Board Created Successfully !", Toast.LENGTH_SHORT).show()
                activity.createBoardSuccess()
            }
            .addOnFailureListener {
                activity.hideProgDialog()
                Log.d(TAG, "createBoard: ${it.message}")
                activity.showErrorSnackBar("Failed, Please try again later !")
            }

    }

    fun getBoards(activity: MainActivity) {
        collectionReference2
            .whereArrayContains(AssnTo, getCurUserId())
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "getBoards: ${it.documents}")
                val boards = ArrayList<Board>()
                for (i in it.documents) {
                    val board = i.toObject(Board::class.java)!!
                    board.apply {
                        docId = i.id
                        boards.add(this)
                    }
                }

                activity.showBoards(boards)
            }
            .addOnFailureListener {
                activity.hideProgDialog()
                activity.showErrorSnackBar("Something went wrong, Please try again later !")
                Log.d(TAG, "getBoards: ${it.message}")
            }
    }

    fun getBoardDetails(activity: TaskListActivity, docId: String) {
        collectionReference2
            .document(docId)
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "getBoards: ${it.data}")
                val board = it.toObject(Board::class.java)!!
                board.docId = it.id
                activity.boardDetails(board)
            }
            .addOnFailureListener {
                activity.hideProgDialog()
                activity.showErrorSnackBar("Something went wrong, Please try again later !")
                Log.d(TAG, "getBoardDetails: ${it.message}")
            }
    }

    fun addUpdateTaskList(activity: Activity, board: Board) {
        val map = HashMap<String, Any>()
        map[TaskList] = board.tasks

        collectionReference2.document(board.docId)
            .update(map)
            .addOnSuccessListener {
                Log.d(TAG, "addUpdateTaskList: Successful !")
                when (activity) {
                    is TaskListActivity -> {
                        activity.getTaskList()
                    }
                    is CardDetailsActivity -> {
                        activity.updateCardDetSuccess()
                    }
                }
            }
            .addOnFailureListener {
                when (activity) {
                    is TaskListActivity -> {
                        activity.hideProgDialog()
                        activity.showErrorSnackBar("Something went wrong, Please try again later !")
                    }
                    is CardDetailsActivity -> {
                        activity.hideProgDialog()
                        activity.showErrorSnackBar("Something went wrong, Please try again later !")
                    }
                }
                Log.d(TAG, "addUpdateTaskList: ${it.message}")
            }
    }

    fun getAssignedMembersDetails(activity: Activity, assignedTo: ArrayList<String>) {
        collectionReference1.whereIn(Id, assignedTo)
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "getAssignedMembersDetails: ${it.documents}")

                val users = ArrayList<User>()
                for (i in it) {
                    val user = i.toObject(User::class.java)
                    users.add(user)
                }

                when(activity){
                    is MembersActivity ->{
                        activity.setUpMembersList(users)
                    }

                    is TaskListActivity ->{
                        activity.assignedMembersDetails(users)
                    }
                }
            }
            .addOnFailureListener {
                when(activity){
                    is MembersActivity ->{
                        activity.hideProgDialog()
                        activity.showErrorSnackBar("Something went wrong, Please try again later !")
                    }

                    is TaskListActivity ->{
                        activity.hideProgDialog()
                        activity.showErrorSnackBar("Something went wrong, Please try again later !")
                    }
                }

                Log.d(TAG, "getAssignedMembersDetails: ${it.message}")
            }
    }

    fun getMembersDetails(activity: MembersActivity, email: String) {
        collectionReference1.whereEqualTo(Email, email)
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "getMembersDetails: ${it.documents}")

                if (it.size() > 0) {
                    val user = it.documents[0].toObject(User::class.java)!!
                    activity.memberDetails(user)
                } else {
                    activity.hideProgDialog()
                    activity.showErrorSnackBar("No member found with the given email id !")
                }
            }
            .addOnFailureListener {
                activity.hideProgDialog()
                Log.d(TAG, "getMembersDetails: ${it.message}")
                activity.showErrorSnackBar("Something went wrong, Please try again later !")
            }
    }

    fun assignMemberToBoard(activity: MembersActivity, board: Board, user: User) {
        val map = HashMap<String, Any>()
        map[AssnTo] = board.assignedTo

        collectionReference2.document(board.docId)
            .update(map)
            .addOnSuccessListener {
                activity.assignMemberSuccess(user)
            }
            .addOnFailureListener {
                activity.hideProgDialog()
                Log.d(TAG, "assignMemberToBoard: ${it.message}")
                activity.showErrorSnackBar("Something went wrong, Please try again later !")
            }
    }

    fun getCurUserId(): String = FirebaseAuth.getInstance().currentUser?.uid ?: ""
}