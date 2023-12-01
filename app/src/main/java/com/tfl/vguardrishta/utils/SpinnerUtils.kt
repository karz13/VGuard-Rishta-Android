package com.tfl.vguardrishta.utils

import android.view.View
import android.widget.AdapterView

/**
 * Created by Shanmuka on 3/27/2019.
 */
open class SpinnerUtils : AdapterView.OnItemSelectedListener {

    override fun onNothingSelected(parent: AdapterView<*>?) = Unit

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) = Unit
}