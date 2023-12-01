package com.tfl.vguardrishta.ui.components.vguard.schemeWiseEarning

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.models.SchemeWiseEarning
import kotlinx.android.synthetic.main.item_scheme_wise_earning.view.*
import java.util.*

class SchemeWiseEarningAdapter(val listener: (SchemeWiseEarning) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var mList: List<SchemeWiseEarning> = arrayListOf()
    var tempList: List<SchemeWiseEarning> = arrayListOf()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_scheme_wise_earning, parent, false)
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
        fun bindItems(product: SchemeWiseEarning, position: Int) = with(itemView) {
            tvSlNo.text=product.slNo
            tvCreatedDate.text = product.createdDate
            tvPartDesc.text = product.partDesc
            tvCouponCode.text = product.couponCode
            tvSchemePoints.text = product.schemePoints
            tvSchemeName.text = product.schemeName
            tvSchemePeriod.text=product.schemePeriod
        }
    }

    inner class ValueFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
//            mList = tempList
            val results = FilterResults()
            if (constraint.isNotEmpty()) {
                val filterList = ArrayList<SchemeWiseEarning>()
                for (list in mList) {
                    if (list.partDesc!!.toLowerCase(Locale.ENGLISH).contains(
                            constraint.toString().toLowerCase(
                                Locale.ENGLISH
                            )
                        )
                        || list.partDesc.toString().toLowerCase(Locale.ENGLISH).contains(
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
                    mList = values as List<SchemeWiseEarning>
                    notifyDataSetChanged()
                }
            }
        }
    }
}