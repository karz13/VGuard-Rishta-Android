package com.tfl.vguardrishta.ui.components.vguard.tdsStatement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.R.layout.item_tds_statement
import com.tfl.vguardrishta.models.TdsStatement
import kotlinx.android.synthetic.main.item_tds_statement.view.*

class TDSstatementAdapter (val listener: (TdsStatement) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mList: List<TdsStatement> = arrayListOf()
    var tempList: List<TdsStatement> = arrayListOf()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(item_tds_statement, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = mList.size

    override fun getItemViewType(position: Int) = position


    override fun getItemId(position: Int) = position.toLong()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bindItems(mList[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(tdsData: TdsStatement) = with(itemView) {

            tvRedDate.text=tdsData.redDate
            tvRedAmt.text=tdsData.redAmnt
            tvTdsAmt.text=tdsData.tdsAmnt
            tvTdsPerc.text=tdsData.tdsPerc
        }
    }

}