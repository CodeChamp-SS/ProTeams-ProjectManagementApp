package com.saarthak.proteams.ui.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saarthak.proteams.R
import com.saarthak.proteams.model.Card
import kotlinx.android.synthetic.main.item_card.view.*

open class CardListAdapter(private val context: Context, private val cards: ArrayList<Card>): RecyclerView.Adapter<CardListAdapter.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = cards[position]
        holder.apply {
            cardName_tv.text = card.name

            if(card.labelCol.isNotEmpty()) {
                labelCol.visibility = View.VISIBLE
                labelCol.setBackgroundColor(Color.parseColor(card.labelCol))
            }
            else labelCol.visibility = View.GONE

            itemView.setOnClickListener {
                if(onClickListener != null){
                    onClickListener!!.onClick(position)
                }
            }
        }
    }

    override fun getItemCount() = cards.size

    interface OnClickListener{
        fun onClick(pos: Int)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val cardName_tv = v.cardName_tv
        val members_tv = v.members_tv
        val labelCol = v.cardLabelCol_view
    }
}