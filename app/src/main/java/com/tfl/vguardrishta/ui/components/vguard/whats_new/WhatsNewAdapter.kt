package com.tfl.vguardrishta.ui.components.vguard.whats_new

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.models.WhatsNew
import kotlinx.android.synthetic.main.list_item_download.view.*
import java.util.*


class WhatsNewAdapter(val listener: (WhatsNew) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var mList: List<WhatsNew> = arrayListOf()
    var tempList: List<WhatsNew> = arrayListOf()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_whats_new_pdfs, parent, false)
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
        fun bindItems(whatsNew: WhatsNew) = with(itemView) {
//            tvHeadline.text = whatsNew.headline
//            tvSubHeadline.text = whatsNew.subheadline
//            tvDesc.text = whatsNew.detailDesc
//            Glide.with(this).load(whatsNew.imagePath).into(ivNewsBanner)
            tvFileName.text = whatsNew.fileName
            setOnClickListener { listener(whatsNew) }
        }
    }

    inner class ValueFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            mList = tempList
            val results = FilterResults()
            if (constraint.isNotEmpty()) {
                val filterList = ArrayList<WhatsNew>()
                for (list in mList) {
                    if (list.headline!!.toLowerCase(Locale.ENGLISH).contains(
                            constraint.toString().toLowerCase(
                                Locale.ENGLISH
                            )
                        )
                        || list.headline!!.toLowerCase(Locale.ENGLISH).contains(
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
                    mList = values as List<WhatsNew>
                    notifyDataSetChanged()
                }
            }
        }
    }
}