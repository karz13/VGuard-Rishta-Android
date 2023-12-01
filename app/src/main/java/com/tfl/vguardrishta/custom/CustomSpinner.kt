package com.tfl.vguardrishta.custom

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.tfl.vguardrishta.models.Profession
import com.tfl.vguardrishta.models.*
import com.tfl.vguardrishta.models.BankDetail
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.Constants.CATEGORY
import com.tfl.vguardrishta.utils.Constants.CITY
import com.tfl.vguardrishta.utils.Constants.DISTRICT
import com.tfl.vguardrishta.utils.Constants.SORT
import com.tfl.vguardrishta.utils.Constants.STATES
import com.tfl.vguardrishta.utils.Constants.UPLOAD_ORDER
import com.tfl.vguardrishta.utils.Constants.USERS
import com.tfl.vguardrishta.utils.Constants.VEHICLE_SEGMENT

class CustomSpinner(
    context: Context,
    resource: Int,
    private var values: Array<Any>,
    private var type: String
) :
    ArrayAdapter<Any>(context, resource, values) {

    override fun getCount(): Int = values.size

    override fun getItem(position: Int): Any? = values[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getView(position, convertView, parent) as TextView
        val any = values[position]
        when (type) {
            USERS -> label.text = (any as User).username
            CATEGORY -> label.text = (any as Category).prodCatName
            CITY -> label.text = (any as CityMaster).cityName
            STATES -> label.text = (any as StateMaster).stateName
            UPLOAD_ORDER -> label.text = (any as User).companyName
            DISTRICT -> label.text = (any as DistrictMaster).districtName
            SORT -> label.text = (any as ProductSort).strSort
            Constants.TICKET_TYPE -> label.text = (any as TicketType).name

            Constants.ID_TYPES -> label.text = (any as KycIdTypes).kycIdName
            Constants.BANKS -> label.text = (any as BankDetail).bankNameAndBranch
            Constants.PROFESSION -> label.text = (any as Profession).professionName
            Constants.WINEER_DATES -> label.text = (any as DailyWinner).date
            Constants.MONTHS-> label.text = (any as MonthData).month
            VEHICLE_SEGMENT -> label.text = (any as String)
        }

        return label
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getDropDownView(position, convertView, parent) as TextView
        val any = values[position]
        when (type) {
            USERS -> label.text = (any as User).username
            CATEGORY -> label.text = (any as Category).prodCatName
            CITY -> label.text = (any as CityMaster).cityName
            STATES -> label.text = (any as StateMaster).stateName
            UPLOAD_ORDER -> label.text = (any as User).companyName
            DISTRICT -> label.text = (any as DistrictMaster).districtName
            VEHICLE_SEGMENT -> label.text = (any as String)
            Constants.TICKET_TYPE -> label.text = (any as TicketType).name
            Constants.BANKS -> label.text = (any as BankDetail).bankNameAndBranch
            SORT -> label.text = (any as ProductSort).strSort
            Constants.ID_TYPES -> label.text = (any as KycIdTypes).kycIdName
            Constants.PROFESSION -> label.text = (any as Profession).professionName
            Constants.WINEER_DATES -> label.text = (any as DailyWinner).date
            Constants.MONTHS-> label.text = (any as MonthData).month

        }
        return label
    }

}