package com.tfl.vguardrishta.ui.components.vguard.ticketHistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.models.TicketType
import kotlinx.android.synthetic.main.item_ticket_history.view.*
import java.util.*


class TicketHistoryAdapter(val listener: (TicketType) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var mList: List<TicketType> = arrayListOf()
    var tempList: List<TicketType> = arrayListOf()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_ticket_history, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = mList.size

    override fun getItemViewType(position: Int) = position

    override fun getFilter(): Filter = ValueFilter()

    override fun getItemId(position: Int) = position.toLong()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bindItems(mList[position], position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(product: TicketType, position: Int) = with(itemView) {
            tvTicketDate.text = product.createdDate
            tvTicketDesc.text = product.name
            btnTicketStatus.text = product.status
            tvTicketNo.text="Ticket No: "+product.ticketNo
            tvStatus.text="Status: "+product.status
            itemView.setOnClickListener {
                if (llMoreInfo.visibility == View.VISIBLE) {
                    llMoreInfo.visibility = View.GONE
                    ivMoreInfoDropDown.setImageResource(R.drawable.ic_ticket_drop_donw1)
                } else {
                    llMoreInfo.visibility = View.VISIBLE
                    ivMoreInfoDropDown.setImageResource(R.drawable.ic_ticket_drop_down2)
                }
            }
        }
    }

    inner class ValueFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
//            mList = tempList
            val results = FilterResults()
            if (constraint.isNotEmpty()) {
                val filterList = ArrayList<TicketType>()
                for (list in mList) {
                    if (list.ticketNo!!.toLowerCase(Locale.ENGLISH).contains(
                            constraint.toString().toLowerCase(
                                Locale.ENGLISH
                            )
                        )
                        || list.status.toString().toLowerCase(Locale.ENGLISH).contains(
                            constraint.toString().toLowerCase(
                                Locale.ENGLISH
                            )
                        )
                    ) filterList.add(list)
                }
                results.count = filterList.size
                results.values = filterList
            } else {
                results.count = mList.size
                results.values = mList
            }
            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {
            if (results != null) {
                val values = results.values
                if (values != null) {
                    mList = values as List<TicketType>
                    notifyDataSetChanged()
                }
            }
        }
    }
}