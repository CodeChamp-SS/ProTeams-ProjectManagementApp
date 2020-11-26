package com.saarthak.proteams.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saarthak.proteams.R
import com.saarthak.proteams.model.SelectedMembers
import kotlinx.android.synthetic.main.item_selected_member.view.*

open class SelectedMembersAdapter(
    private val context: Context,
    private val members: ArrayList<SelectedMembers>
) : RecyclerView.Adapter<SelectedMembersAdapter.ViewHolder>() {

    var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_selected_member, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val memb = members[position]

        holder.apply {
            if(position == members.size - 1){
                addMemb_iv.visibility = View.VISIBLE
                selMemb_iv.visibility = View.GONE
            }
            else{
                addMemb_iv.visibility = View.GONE
                selMemb_iv.visibility = View.VISIBLE

                Glide.with(context)
                    .load(memb.img)
                    .placeholder(R.drawable.ic_edit_img)
                    .centerCrop()
                    .into(selMemb_iv)
            }

            itemView.setOnClickListener {
                if(onClickListener != null){
                    onClickListener!!.onClick()
                }
            }
        }
    }

    override fun getItemCount() = members.size

    interface OnClickListener{
        fun onClick()
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val addMemb_iv = v.addMemb_iv
        val selMemb_iv = v.selectedMemb_iv
    }
}