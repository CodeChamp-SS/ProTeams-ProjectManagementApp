package com.saarthak.proteams.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.saarthak.proteams.R
import com.saarthak.proteams.model.User
import com.saarthak.proteams.ui.adapters.MembersAdapter
import kotlinx.android.synthetic.main.assign_memb_dialog.view.*

abstract class SelectMembDialog(
    context: Context,
    private val members: ArrayList<User>
) : Dialog(context) {

    private lateinit var membAdapter: MembersAdapter

    private fun setUpRv(v: View){
        if(members.isNotEmpty()){
            membAdapter = MembersAdapter(context, members)
            v.selectMemb_rv.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = membAdapter
            }

            membAdapter.onClickListener = object : MembersAdapter.OnClickListener{
                override fun onClick(pos: Int, user: User, act: String) {
                    dismiss()
                    onItemSelected(user, act)
                }
            }
        }
    }

    abstract fun onItemSelected(user: User, act: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val v = LayoutInflater.from(context).inflate(R.layout.assign_memb_dialog, null)
        setContentView(v)

        setUpRv(v)
    }

}