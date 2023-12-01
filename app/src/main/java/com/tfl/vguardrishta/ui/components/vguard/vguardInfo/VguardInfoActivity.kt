package com.tfl.vguardrishta.ui.components.vguard.vguardInfo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.tfl.vguardrishta.R
import kotlinx.android.synthetic.main.v_activity_redeem_points.*
import kotlinx.android.synthetic.main.vguard_toolbar.*

class VguardInfoActivity : AppCompatActivity(), View.OnClickListener {

    private fun setCustomActionBar() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.v_guard_info), "")
        ivBack.setOnClickListener(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vguard_info)
        setCustomActionBar()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

}