package com.saarthak.proteams.ui.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saarthak.proteams.R
import kotlinx.android.synthetic.main.item_label_col.view.*

open class ColorListAdapter(
    private val context: Context,
    private val colors: ArrayList<String>,
    private val col: String
) : RecyclerView.Adapter<ColorListAdapter.ViewHolder>() {

    var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_label_col, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val color = colors[position]
        holder.apply {
            labelCol.setBackgroundColor(Color.parseColor(color))

            if (color == col) ok_iv.visibility = View.VISIBLE
            else ok_iv.visibility = View.GONE

            itemView.setOnClickListener {
                onClickListener?.onClick(position, color)
            }
        }
    }

    override fun getItemCount() = colors.size

    interface OnClickListener {
        fun onClick(pos: Int, col: String)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val labelCol = v.labelCol
        val ok_iv = v.okLabel_iv
    }
}