package com.tfl.vguardrishta.ui.components.vguard.fragment.newuser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.tfl.vguardrishta.R
import java.util.*

class SearchableBaseAdapter : BaseAdapter(), Filterable {
    var mList: List<Any> = arrayListOf()
    var tempList: List<Any> = arrayListOf()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var itemConvertView = convertView
        val holder: ViewHolder
        if (itemConvertView == null) {
            holder = ViewHolder()
            itemConvertView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.list_item_defect_type, parent, false)
            holder.text1 = itemConvertView.findViewById(R.id.tv_defect_type)
            itemConvertView.tag = holder
        } else {
            holder = itemConvertView.tag as ViewHolder
        }
        val any = mList[position]
        holder.text1?.text = any.toString()
        return itemConvertView
    }

    override fun getItem(position: Int) = mList[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = mList.size

    override fun getFilter() = ValueFilter()

    inner class ValueFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            mList = tempList
            val results = FilterResults()
            if (constraint.isNotEmpty()) {
                val filterList = ArrayList<Any>()
                for (item in mList) {
                    if (item.toString().toLowerCase().contains(constraint, true)) {
                        filterList.add(item)
                    }
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
                    mList = values as List<Any>
                    notifyDataSetChanged()
                }
            }
        }
    }

    inner class ViewHolder() {
        var text1: TextView? = null
    }
}