package com.saarthak.proteams.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saarthak.proteams.R
import com.saarthak.proteams.model.Board
import kotlinx.android.synthetic.main.item_board.view.*

open class BoardAdapter(private val context: Context, private val list: ArrayList<Board>):
    RecyclerView.Adapter<BoardAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_board, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val board = list[position]

        Glide.with(context)
            .load(board.img)
            .centerCrop()
            .placeholder(R.drawable.ic_img_holder)
            .into(holder.circ_iv)

        holder.apply {
            name_tv.text = board.name
            create_tv.text = "Created by: ${board.author}"
            itemView.setOnClickListener {
                onClickListener?.onClick(position, board)
            }
        }
    }

    interface OnClickListener{
        fun onClick(position: Int, board: Board)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    override fun getItemCount() = list.size

    class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        val circ_iv = v.board_iv
        val name_tv = v.board_name_tv
        val create_tv = v.board_createdBy_tv
    }
}