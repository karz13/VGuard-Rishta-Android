package com.tfl.vguardrishta.ui.components.vguard.welfare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.models.DownloadData
import com.tfl.vguardrishta.models.WhatsNew
import kotlinx.android.synthetic.main.item_welfare_pdfs.view.*
import java.util.*


class WelfareAdapter(val listener: (DownloadData) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mList: List<DownloadData> = arrayListOf()
    var tempList: List<DownloadData> = arrayListOf()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_welfare_pdfs, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = mList.size

    override fun getItemViewType(position: Int) = position


    override fun getItemId(position: Int) = position.toLong()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bindItems(mList[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(whatsNew: DownloadData) = with(itemView) {
//            tvHeadline.text = whatsNew.headline
//            tvSubHeadline.text = whatsNew.subheadline
//            tvDesc.text = whatsNew.detailDesc
//            Glide.with(this).load(whatsNew.imagePath).into(ivNewsBanner)
            tvFileName.text = whatsNew.description
            setOnClickListener { listener(whatsNew) }
        }
    }

 }