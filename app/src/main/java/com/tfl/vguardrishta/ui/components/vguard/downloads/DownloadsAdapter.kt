package com.tfl.vguardrishta.ui.components.vguard.downloads

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.models.DownloadData
import kotlinx.android.synthetic.main.list_item_download.view.*

/**
 * Created by Shanmuka on 5/9/2019.
 */
class DownloadsAdapter(val listener: (DownloadData) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mList: List<DownloadData> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_download, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = mList.size

    override fun getItemViewType(position: Int) = position

    override fun getItemId(position: Int) = position.toLong()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bindItems(mList[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: DownloadData) = with(itemView) {
            tvFileName.text = data.description
            setOnClickListener { listener(data)
            }
        }
    }

}