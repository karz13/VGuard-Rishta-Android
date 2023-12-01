package com.tfl.vguardrishta.custom

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.models.Category
import java.util.*

class CheckboxSpinnerAdapter(private val mContext: Context, resource: Int, objects: List<Category>) :
    ArrayAdapter<Category?>(mContext, resource, objects) {
    val listCategoryState: ArrayList<Category> = objects as ArrayList<Category>
    private val myAdapter: CheckboxSpinnerAdapter = this
    private var isFromView = false

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    fun getCustomView(
        position: Int, convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView = convertView
        val holder: ViewHolder
        if (convertView == null) {
            val layoutInflator = LayoutInflater.from(mContext)
            convertView = layoutInflator.inflate(R.layout.spinner_checkbox_item, null)
            holder = ViewHolder()
            holder.mTextView = convertView
                .findViewById<View>(R.id.text) as TextView
            holder.mCheckBox = convertView
                .findViewById<View>(R.id.checkbox) as CheckBox
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        holder.mTextView!!.text = listCategoryState[position].prodCatName

        // To check weather checked event fire from getview() or user input
        isFromView = true
        holder.mCheckBox!!.isChecked = listCategoryState[position].isSelected
        isFromView = false
        if (position == 0) {
            holder.mCheckBox?.visibility = View.INVISIBLE
        } else {
            holder.mCheckBox?.visibility = View.VISIBLE
        }
        holder.mCheckBox?.tag = position
        holder.mCheckBox?.setOnCheckedChangeListener { buttonView, isChecked ->
            val getPosition = buttonView.tag as Int
            if (!isFromView) {
                listCategoryState[position].isSelected = isChecked
            }
        }
        return convertView!!
    }

    private inner class ViewHolder {
        var mTextView: TextView? = null
        var mCheckBox: CheckBox? = null
    }

}