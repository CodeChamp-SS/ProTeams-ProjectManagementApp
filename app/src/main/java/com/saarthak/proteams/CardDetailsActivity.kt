package com.saarthak.proteams

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.saarthak.proteams.constants.Constants.Select
import com.saarthak.proteams.model.Board
import com.saarthak.proteams.model.Card
import com.saarthak.proteams.model.SelectedMembers
import com.saarthak.proteams.model.User
import com.saarthak.proteams.ui.adapters.SelectedMembersAdapter
import com.saarthak.proteams.ui.dialogs.ColorListDialog
import com.saarthak.proteams.ui.dialogs.SelectMembDialog
import com.saarthak.proteams.util.BaseActivity
import com.saarthak.proteams.util.FireStoreClass
import kotlinx.android.synthetic.main.activity_card_details.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardDetailsActivity : BaseActivity() {

    private lateinit var board: Board
    private lateinit var members: ArrayList<User>
    private var taskPos = -1
    private var cardPos = -1
    private var selectedCol = ""
    private var dueData_ts: Long = 0

    private fun setUpActBar() {
        setSupportActionBar(cardDet_toolbar)

        val actBar = supportActionBar
        if (actBar != null) {
            actBar.setDisplayHomeAsUpEnabled(true)
            actBar.setHomeAsUpIndicator(R.drawable.ic_back)
            actBar.title = board.tasks[taskPos].cards[cardPos].name
        }

        cardDet_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun updateCardDet() {
        val name = cardDetName_tv.text.toString()
        if (name.isNotEmpty()) {
            val card = Card(
                name,
                board.tasks[taskPos].cards[cardPos].createdBy,
                board.tasks[taskPos].cards[cardPos].assignedTo,
                selectedCol,
                dueData_ts
            )
            board.tasks[taskPos].cards[cardPos] = card
            board.tasks.removeAt(board.tasks.size - 1)

            showProgDialog()
            FireStoreClass().addUpdateTaskList(this, board)
        } else Toast.makeText(this, "Card Name can't be empty !", Toast.LENGTH_SHORT).show()
    }

    fun updateCardDetSuccess() {
        hideProgDialog()
        setResult(RESULT_OK)
        finish()
    }

    private fun deleteCard() {
        val cards = board.tasks[taskPos].cards
        cards.removeAt(cardPos)

        val tasks = board.tasks
        tasks.removeAt(tasks.size - 1)
        tasks[taskPos].cards = cards

        showProgDialog()
        FireStoreClass().addUpdateTaskList(this, board)
    }

    private fun showAlertDialog(title: String) {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("Alert")
            setMessage("Are you sure you want to delete $title ?")
            setIcon(R.drawable.ic_warning)
            setPositiveButton("Yes") { inter, _ ->
                inter.dismiss()
                deleteCard()
            }
            setNegativeButton("No") { inter, _ ->
                inter.dismiss()
            }
        }

        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun showColorListDialog() {
        val colors = colorsList()
        val dialog = object : ColorListDialog(
            this,
            colors,
            resources.getString(R.string.select_a_colour),
            selectedCol
        ) {
            override fun onItemSelected(col: String) {
                selectedCol = col
                setColor()
            }
        }
        dialog.show()
    }

    private fun showMembDialog() {
        val assignedMembers = board.tasks[taskPos].cards[cardPos].assignedTo

        if (assignedMembers.isNotEmpty()) {
            for (x in members) {
                for (y in assignedMembers) {
                    if (x.id == y) x.selected = true
                }
            }
        } else {
            for (x in members) x.selected = false
        }

        val dialog = object : SelectMembDialog(this, members) {
            override fun onItemSelected(user: User, act: String) {
                if (act == Select) {
                    if (!board.tasks[taskPos].cards[cardPos].assignedTo.contains(user.id)) {
                        board.tasks[taskPos].cards[cardPos].assignedTo.add(user.id)
                    }
                } else {
                    if (board.tasks[taskPos].cards[cardPos].assignedTo.contains(user.id)) {
                        board.tasks[taskPos].cards[cardPos].assignedTo.remove(user.id)
                    }

                    for (x in members) {
                        if (x.id == user.id) x.selected = false
                    }
                }

                setUpSelectedMembers()
            }
        }
        dialog.show()
    }

    private fun colorsList(): ArrayList<String> {
        val colors = ArrayList<String>()
        colors.add("#03DAC5")
        colors.add("#aa71f9")
        colors.add("#dbc4fb")
        colors.add("#ff8a65")
        colors.add("#795548")
        colors.add("#f32194")
        colors.add("#76ff03")
        colors.add("#ffff00")

        return colors
    }

    private fun setColor() {
        labelCol_tv.apply {
            text = ""
            setBackgroundColor(Color.parseColor(selectedCol))
        }

    }

    private fun setUpSelectedMembers() {
        val membList = board.tasks[taskPos].cards[cardPos].assignedTo

        val selectedMembs = ArrayList<SelectedMembers>()

        for (x in members) {
            for (y in membList) {
                if (x.id == y) {
                    val selectedMemb = SelectedMembers(x.id, x.img)
                    selectedMembs.add(selectedMemb)
                }
            }
        }

        if (selectedMembs.isNotEmpty()) {
            selectedMembs.add(SelectedMembers())
            selectMemb_tv.visibility = View.GONE
            selectedMemb_rv.visibility = View.VISIBLE

            val selectMembAdapter = SelectedMembersAdapter(this, selectedMembs)
            selectedMemb_rv.apply {
                layoutManager = GridLayoutManager(this@CardDetailsActivity, 6)
                setHasFixedSize(true)
                adapter = selectMembAdapter
            }
            selectMembAdapter.onClickListener = object : SelectedMembersAdapter.OnClickListener {
                override fun onClick() {
                    showMembDialog()
                }

            }
        } else {
            selectMemb_tv.visibility = View.VISIBLE
            selectedMemb_rv.visibility = View.GONE
        }
    }

    private fun showCalendar() {
        val cal = Calendar.getInstance()
        val yr = cal.get(Calendar.YEAR)
        val month_yr = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val dateDialog = DatePickerDialog(
            this,
            { view, year, month, dayOfMonth ->
                val day_s = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                val month_s = if (month < 10) "0$month" else "$month"
                val date = "$day_s/$month_s/$year"
                dueDate_tv.text = date

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                val Date = sdf.parse(date)
                dueData_ts = Date!!.time
            }, yr, month_yr, day)

        dateDialog.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

        if (intent.hasExtra("boardDetails")) board = intent.getParcelableExtra("boardDetails")!!
        if (intent.hasExtra("members")) members = intent.getParcelableArrayListExtra("members")!!
        if (intent.hasExtra("taskPos")) taskPos = intent.getIntExtra("taskPos", -1)
        if (intent.hasExtra("cardPos")) cardPos = intent.getIntExtra("cardPos", -1)

        setUpActBar()

        if (taskPos != -1 && cardPos != -1) {
            cardDetName_tv.setText(board.tasks[taskPos].cards[cardPos].name)
            cardDetName_tv.setSelection(cardDetName_tv.text.toString().length)
            selectedCol = board.tasks[taskPos].cards[cardPos].labelCol
            if (selectedCol.isNotEmpty()) setColor()
            dueData_ts = board.tasks[taskPos].cards[cardPos].dueData
            if(dueData_ts > 0) {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                val selectedDate = sdf.format(dueData_ts)
                dueDate_tv.text = selectedDate
            }
        }

        updateCardB.setOnClickListener {
            updateCardDet()
        }

        labelCol_tv.setOnClickListener {
            showColorListDialog()
        }

        selectMemb_tv.setOnClickListener {
            showMembDialog()
        }

        setUpSelectedMembers()

        dueDate_tv.setOnClickListener {
            showCalendar()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.delete_card_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteCard -> {
                showAlertDialog(board.tasks[taskPos].cards[cardPos].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}