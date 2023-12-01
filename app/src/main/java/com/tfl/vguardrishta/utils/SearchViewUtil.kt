package com.tfl.vguardrishta.utils

import androidx.appcompat.widget.SearchView

/**
 * Created by Shanmuka on 7/8/2019.
 */
open class SearchViewUtil : SearchView.OnQueryTextListener {

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(newText: String?): Boolean = false
}