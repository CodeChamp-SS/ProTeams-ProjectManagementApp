package com.saarthak.proteams

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.saarthak.proteams.constants.Constants.DocId
import com.saarthak.proteams.model.Board
import com.saarthak.proteams.model.Card
import com.saarthak.proteams.model.Task
import com.saarthak.proteams.model.User
import com.saarthak.proteams.ui.adapters.TaskListAdapter
import com.saarthak.proteams.util.BaseActivity
import com.saarthak.proteams.util.FireStoreClass
import kotlinx.android.synthetic.main.activity_task_list.*

class TaskListActivity : BaseActivity() {

    private lateinit var board1: Board
    private lateinit var assignedMembers: ArrayList<User>
    private var boardDocId = ""

    companion object{
        const val MEMBER_REQ_CODE = 5
        const val CARD_DET_REQ_CODE = 6
    }

    private fun setUpActBar(){
        setSupportActionBar(tl_toolbar)

        val actBar = supportActionBar
        if(actBar != null){
            actBar.title = board1.name
            actBar.setDisplayHomeAsUpEnabled(true)
            actBar.setHomeAsUpIndicator(R.drawable.ic_back)
        }

        tl_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun boardDetails(board: Board){
        board1 = board
        setUpActBar()

        val task = Task(resources.getString(R.string.add_list))
        board.tasks.add(task)

        val adapter = TaskListAdapter(this, board.tasks)
        task_rv.apply {
            layoutManager = LinearLayoutManager(this@TaskListActivity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            this.adapter = adapter
        }

        FireStoreClass().getAssignedMembersDetails(this, board1.assignedTo)
    }

    fun assignedMembersDetails(members: ArrayList<User>){
        assignedMembers = members
        hideProgDialog()
    }

    fun getTaskList(){
        FireStoreClass().getBoardDetails(this, board1.docId)
    }

    fun createTaskList(name: String){
        val task = Task(name, FireStoreClass().getCurUserId())
        board1.tasks.add(0, task)
        board1.tasks.removeAt(board1.tasks.size - 1)

        showProgDialog()
        FireStoreClass().addUpdateTaskList(this, board1)
    }

    fun updateTaskList(pos: Int, name: String, task: Task){
        val task1 = Task(name, task.createdBy)
        board1.tasks[pos] = task1
        board1.tasks.removeAt(board1.tasks.size - 1)

        showProgDialog()
        FireStoreClass().addUpdateTaskList(this, board1)
    }

    fun deleteTaskList(pos: Int){
        board1.tasks.removeAt(pos)
        board1.tasks.removeAt(board1.tasks.size - 1)

        showProgDialog()
        FireStoreClass().addUpdateTaskList(this, board1)
    }

    fun addCardToTasks(pos: Int, name: String){
        board1.tasks.removeAt(board1.tasks.size - 1)

        val assignedTo = board1.assignedTo
        val card = Card(name, FireStoreClass().getCurUserId(), assignedTo)
        val cards = board1.tasks[pos].cards
        cards.add(card)

        val task = Task(board1.name, board1.author, cards)
        board1.tasks[pos] = task

        showProgDialog()
        FireStoreClass().addUpdateTaskList(this, board1)
    }

    fun cardDetails(taskPos: Int, cardPos: Int){
        val intent = Intent(this, CardDetailsActivity::class.java)
        intent.putExtra("boardDetails", board1)
        intent.putExtra("taskPos", taskPos)
        intent.putExtra("cardPos", cardPos)
        intent.putExtra("members", assignedMembers)
        startActivityForResult(intent, CARD_DET_REQ_CODE)
    }

    fun updateCardPos(taskPos: Int, cards: ArrayList<Card>){
        board1.tasks.removeAt(board1.tasks.size - 1)
        board1.tasks[taskPos].cards = cards

        showProgDialog()
        FireStoreClass().addUpdateTaskList(this, board1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        if(intent.hasExtra(DocId)) boardDocId = intent.getStringExtra(DocId)!!

        showProgDialog()
        FireStoreClass().getBoardDetails(this, boardDocId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.members_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members -> {
                val intent = Intent(this, MembersActivity::class.java)
                intent.putExtra("boardDetails", board1)
                startActivityForResult(intent, MEMBER_REQ_CODE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if((requestCode == MEMBER_REQ_CODE || requestCode == CARD_DET_REQ_CODE) && resultCode == RESULT_OK){
            showProgDialog()
            FireStoreClass().getBoardDetails(this, boardDocId)
        }
    }
}