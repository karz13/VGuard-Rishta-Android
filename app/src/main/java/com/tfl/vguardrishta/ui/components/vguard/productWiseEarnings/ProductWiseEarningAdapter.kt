package com.tfl.vguardrishta.ui.components.vguard.productWiseEarnings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.models.ProductWiseEarning
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import kotlinx.android.synthetic.main.item_prod_wise_earning.view.*
import java.util.*

class ProductWiseEarningAdapter(val listener: (ProductWiseEarning) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var mList: List<ProductWiseEarning> = arrayListOf()
    var tempList: List<ProductWiseEarning> = arrayListOf()
    var rishtaUser = CacheUtils.getRishtaUser()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_prod_wise_earning, parent, false)
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
        fun bindItems(product: ProductWiseEarning, position: Int) = with(itemView) {
            tvSlNo.text = product.slNo
            tvPartDesc.text = product.partDesc
            tvPoints.text = product.points
            tvBonusPoints.text = product.bonusPoints
            tvCouponCode.text = product.couponCode
            tvCreatedDate.text = product.createdDate
            tvProdCategory.text = product.category
            if (rishtaUser.roleId == Constants.RET_USER_TYPE) {
                tvProdCategory.visibility = View.VISIBLE
                tvBonusPoints.visibility = View.GONE
            }
        }
    }

    inner class ValueFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
//            mList = tempList
            val results = FilterResults()
            if (constraint.isNotEmpty()) {
                val filterList = ArrayList<ProductWiseEarning>()
                for (list in mList) {
                    if (list.bonusPoints!!.toLowerCase(Locale.ENGLISH).contains(
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
                    mList = values as List<ProductWiseEarning>
                    notifyDataSetChanged()
                }
            }
        }
    }
}