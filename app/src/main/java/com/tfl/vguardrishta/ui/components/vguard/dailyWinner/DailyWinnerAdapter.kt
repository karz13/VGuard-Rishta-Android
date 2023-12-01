package com.tfl.vguardrishta.ui.components.vguard.dailyWinner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.models.DailyWinner
import com.tfl.vguardrishta.utils.AppUtils
import kotlinx.android.synthetic.main.item_daily_winner.view.*
import kotlinx.android.synthetic.main.v_activity_raise_ticket.*
import java.util.*


class DailyWinnerAdapter(val listener: (DailyWinner) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var mList: List<DailyWinner> = arrayListOf()
    var tempList: List<DailyWinner> = arrayListOf()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_daily_winner, parent, false)
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
        fun bindItems(dw: DailyWinner) = with(itemView) {
            var selfie = dw.profile
            selfie = AppUtils.getSelfieUrl() + selfie
            Glide.with(this).load(selfie)
                .placeholder(R.drawable.ic_v_guards_user).into(ivProfile)

            tvName.text=dw.name
            tvBranch.text=dw.branch
            tvDist.text=dw.district

        }
    }

    inner class ValueFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            mList = tempList
            val results = FilterResults()
            if (constraint.isNotEmpty()) {
                val filterList = ArrayList<DailyWinner>()
                for (list in mList) {
                    if (list.branch!!.toLowerCase(Locale.ENGLISH).contains(
                            constraint.toString().toLowerCase(
                                Locale.ENGLISH
                            )
                        )
                        || list.name!!.toLowerCase(Locale.ENGLISH).contains(
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
                    mList = values as List<DailyWinner>
                    notifyDataSetChanged()
                }
            }
        }
    }
}