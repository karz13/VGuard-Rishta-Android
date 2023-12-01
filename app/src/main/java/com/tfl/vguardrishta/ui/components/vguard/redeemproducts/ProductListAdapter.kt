package com.tfl.vguardrishta.ui.components.vguard.redeemproducts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.models.ProductDetail
import com.tfl.vguardrishta.remote.ApiService
import kotlinx.android.synthetic.main.item_product.view.*
import java.util.*


class ProductListAdapter(val listener: (ProductDetail, Int) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var mList: List<ProductDetail> = arrayListOf()
    var tempList: List<ProductDetail> = arrayListOf()
    var type: String? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
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
        fun bindItems(product: ProductDetail, position: Int) = with(itemView) {


            Glide.with(this).load(ApiService.imageBaseUrl + product.imageUrl).into(ivProductImg)

            tvProdDesc.text = product.productName
            var pointsStr = ""
            if (type.isNullOrEmpty())
                pointsStr = "${product.points} Points"
            else
                pointsStr = "${product.points} Stars"
            tvProdPoints.text = pointsStr
            tvProdCode.text = product.productCode

            cbSelected.isChecked = product.isSelected
            setOnClickListener { listener(product, position) }
        }
    }

    inner class ValueFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
//            mList = tempList
            val results = FilterResults()
            if (constraint.isNotEmpty()) {
                val filterList = ArrayList<ProductDetail>()
                for (list in mList) {
                    if (list.productName!!.toLowerCase(Locale.ENGLISH).contains(
                            constraint.toString().toLowerCase(
                                Locale.ENGLISH
                            )
                        )
                        || list.points.toString().toLowerCase(Locale.ENGLISH).contains(
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
                    mList = values as List<ProductDetail>
                    notifyDataSetChanged()
                }
            }
        }
    }
}