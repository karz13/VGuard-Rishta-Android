package com.tfl.vguardrishta.ui.components.vguard.specialComboOffers

import android.annotation.SuppressLint

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.models.SchemeProgressData
import com.tfl.vguardrishta.models.SpecialSchemes
import com.tfl.vguardrishta.ui.components.vguard.scanCode.ScanCodeActivity
import com.tfl.vguardrishta.utils.CacheUtils
import kotlinx.android.synthetic.main.item_specialcombo_offer.view.*



class SpecialComboOffersViewAdapter(private val mList2: List<SpecialSchemes>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       fun bindItems(data:SpecialSchemes) {
           itemView.titleText.text=data.schemeTitle
           itemView.Note1.text =data.schemeDesc1
           itemView.Note2.text = data.schemeDesc2
           itemView.Note3.text=data.schemeDesc3
           itemView.btnAction.text=data.btnText
           var milestone=LinkedHashMap<String,String>();
           for ((index,row) in data.tableData.withIndex()){
               val tblRow=TableRow(itemView.context)
               for (value in row){
                   val t = TextView(itemView.context)
                   t.text = value
                   t.setTextColor(Color.BLACK)
                   t.setTypeface(null,Typeface.BOLD)
                   t.textAlignment = View.TEXT_ALIGNMENT_CENTER
                   t.textSize = 15.0f
                   tblRow.addView(t)
               }
               if(index>0){
                   if(data.schemeTitle.equals("Slab Based Scheme")){

                       milestone[data.tableData[index][0]] = data.tableData[index][2]
                   }
               }
               itemView.scheme_table.addView(tblRow)
           }
           itemView.btnAction.setOnClickListener(View.OnClickListener {
               if(itemView.btnAction.text.equals("SCAN")){
                itemView.context.launchActivity<ScanCodeActivity> {  }
               }else {
                val schemeData = SchemeProgressData()
                   schemeData.title=data.schemeTitle;
                   schemeData.currentPoints = data.tableData[1][1].toFloat()
                   schemeData.mileStoneData=milestone
                   CacheUtils.setSchemeProgress(schemeData)
                   itemView.context.launchActivity<SchemeProgressActivity> {  }

               }
           })

       }

    }


    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_specialcombo_offer,parent,false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
      return mList2.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val v = ViewHolder(holder.itemView)
        v.bindItems(mList2[position])

    }



}

