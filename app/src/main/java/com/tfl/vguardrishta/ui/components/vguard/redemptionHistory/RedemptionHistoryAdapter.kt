package com.tfl.vguardrishta.ui.components.vguard.redemptionHistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.models.RedemptionHistory
import kotlinx.android.synthetic.main.item_redemption_history.view.*
import java.util.*


class RedemptionHistoryAdapter(val listener: (RedemptionHistory) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var mList: List<RedemptionHistory> = arrayListOf()
    var tempList: List<RedemptionHistory> = arrayListOf()
    var type: String? = null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_redemption_history, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = mList.size

    override fun getItemViewType(position: Int) = position

    override fun getFilter(): Filter = ValueFilter()

    override fun getItemId(position: Int) = position.toLong()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bindItems(mList[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(rht: RedemptionHistory) = with(itemView) {
            tvRedeemDate.text = rht.transactDate
            tvProductName.text = rht.productName
            btnStatus.text = rht.orderStatus
            var points = ""
            tvPoints.text = if (type.isNullOrEmpty()) {
                context.getString(R.string.points) + " : " + rht.points
            } else {
                context.getString(R.string.stars) + " : " + rht.points
            }
            if (!rht.accHolderNo.isNullOrEmpty()) {
                tvAccountNo.text =
                    context.getString(R.string.lbl_account_holder) + " : " + rht.accHolderNo
            } else {
                tvAccountNo.visibility = View.GONE
            }
            if (!rht.mobileNumber.isNullOrEmpty()) {
                tvMobileNumber.text =
                    context.getString(R.string.mobile_no) + " : " + rht.mobileNumber
            } else if (!type.isNullOrEmpty() && !rht.materialCode.isNullOrEmpty()) {
                tvMobileNumber.text = "Material Code : ${rht.materialCode}"
            } else
                tvMobileNumber.visibility = View.GONE
//            itemView.setOnClickListener {
//                listener(rht)
//            }
//            btnStatus.setOnClickListener {
//                listener(rht)
//            }
        }
    }

    inner class ValueFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            mList = tempList
            val results = FilterResults()
            if (constraint.isNotEmpty()) {
                val filterList = ArrayList<RedemptionHistory>()
                for (list in mList) {
                    if (list.transactDate!!.toLowerCase(Locale.ENGLISH).contains(
                            constraint.toString().toLowerCase(
                                Locale.ENGLISH
                            )
                        )
                        || list.productName!!.toLowerCase(Locale.ENGLISH).contains(
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
                    mList = values as List<RedemptionHistory>
                    notifyDataSetChanged()
                }
            }
        }
    }
}