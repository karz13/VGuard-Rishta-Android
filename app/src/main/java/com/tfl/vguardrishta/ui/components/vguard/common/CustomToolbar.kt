package com.tfl.vguardrishta.ui.components.vguard.common

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.visible
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import kotlinx.android.synthetic.main.vguard_toolbar.view.*

class CustomToolbar(context: Context?, attrs: AttributeSet?) : Toolbar(context!!, attrs) {

    init {
        inflate(context, R.layout.vguard_toolbar, this)
    }

    fun updateToolbar(screenName: String, title: String, subTitle: String) {
        if (title.isNotEmpty())
            toolbar_title.text = title
        else
//            toolbar_title.text = AppUtils.getAppName(context)
            if (subTitle.isNotEmpty()) {
                toolbar_sub_title.visible = true
                toolbar_sub_title.text = subTitle
            } else {
                toolbar_sub_title.visibility = View.GONE
            }

        when (screenName) {
            Constants.HOME_SCREEN -> {
                hideViews(ivBack)
                if(CacheUtils.getRishtaUser().roleId==Constants.INF_USER_TYPE){
                    showViews(llLanguage)
                }
                hideViews(toolbar_title)
            }
            Constants.NOTIFICATION->{
                hideViews(ivBack)
                hideViews(llLanguage)
                showViews(toolbar_title)
            }
            Constants.CONTACT_US->{
                hideViews(ivBack)
                hideViews(llLanguage)
                showViews(toolbar_title)
            }
            Constants.PROFILE->{
                hideViews(ivBack)
                hideViews(llLanguage)
                showViews(toolbar_title)
            }
            else -> {
                showViews(ivBack)
                hideViews(ivMenu)
                hideViews(llLanguage)
            }
        }
    }


    private fun hideViews(vararg views: View) {
        for (view in views) {
            view.visible = false
        }
    }

    private fun showViews(vararg views: View) {
        for (view in views) {
            view.visible = true
        }
    }


}