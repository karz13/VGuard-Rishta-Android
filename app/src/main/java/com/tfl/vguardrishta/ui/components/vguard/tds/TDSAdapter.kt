package com.tfl.vguardrishta.ui.components.vguard.tds

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.models.DownloadData
import com.tfl.vguardrishta.models.TdsData
import kotlinx.android.synthetic.main.item_welfare_pdfs.view.*

class TDSAdapter (val listener: (TdsData) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mList: List<TdsData> = arrayListOf()
    var tempList: List<TdsData> = arrayListOf()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_tds_pdfs, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = mList.size

    override fun getItemViewType(position: Int) = position


    override fun getItemId(position: Int) = position.toLong()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bindItems(mList[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(tdsData: TdsData) = with(itemView) {
//            tvHeadline.text = whatsNew.headline
//            tvSubHeadline.text = whatsNew.subheadline
//            tvDesc.text = whatsNew.detailDesc
//            Glide.with(this).load(whatsNew.imagePath).into(ivNewsBanner)
            tvFileName.text = tdsData.name
            setOnClickListener { listener(tdsData) }
        }
    }

}