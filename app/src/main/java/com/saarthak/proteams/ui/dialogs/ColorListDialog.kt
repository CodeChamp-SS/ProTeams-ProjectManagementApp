package com.saarthak.proteams.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.saarthak.proteams.R
import com.saarthak.proteams.ui.adapters.ColorListAdapter
import kotlinx.android.synthetic.main.col_list_dialog.view.*

abstract class ColorListDialog(
    context: Context,
    private val colors: ArrayList<String>,
    private var title: String = "",
    private var col: String = ""
) : Dialog(context) {

    private lateinit var colorListAdapter: ColorListAdapter

    private fun setUpRv(v: View){
        v.colTitle_tv.text = title

        colorListAdapter = ColorListAdapter(context, colors, col)
        v.colList_rv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = colorListAdapter
        }
        colorListAdapter.onClickListener = object : ColorListAdapter.OnClickListener{
            override fun onClick(pos: Int, col: String) {
                dismiss()
                onItemSelected(col)
            }
        }
    }

    abstract fun onItemSelected(col: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val v = LayoutInflater.from(context).inflate(R.layout.col_list_dialog, null)
        setContentView(v)

        setUpRv(v)
    }

}