package com.saarthak.proteams.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saarthak.proteams.R
import com.saarthak.proteams.constants.Constants.Select
import com.saarthak.proteams.constants.Constants.UnSelect
import com.saarthak.proteams.model.User
import kotlinx.android.synthetic.main.item_member.view.*

open class MembersAdapter(private val context: Context, private val members: ArrayList<User>): RecyclerView.Adapter<MembersAdapter.ViewHolder>() {

    var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_member, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val memb = members[position]
        holder.apply {
            name_tv.text = memb.name
            mail_tv.text = memb.email

            Glide.with(context)
                .load(memb.img)
                .centerCrop()
                .placeholder(R.drawable.ic_edit_img)
                .into(memb_iv)

            if(memb.selected) sel_iv.visibility = View.VISIBLE
            else sel_iv.visibility = View.GONE

            itemView.setOnClickListener {
                if(onClickListener != null){
                    if(memb.selected) onClickListener!!.onClick(position, memb, UnSelect)
                    else onClickListener!!.onClick(position, memb, Select)
                }
            }
        }
    }

    override fun getItemCount() = members.size

    interface OnClickListener{
        fun onClick(pos: Int, user: User, act: String)
    }

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val name_tv = v.membName_tv
        val mail_tv = v.membMail_tv
        val memb_iv = v.memb_iv
        val sel_iv = v.okMember_iv
    }
}