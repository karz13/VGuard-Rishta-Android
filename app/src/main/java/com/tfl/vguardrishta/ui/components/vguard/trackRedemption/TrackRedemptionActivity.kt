package com.tfl.vguardrishta.ui.components.vguard.trackRedemption

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.tfl.vguardrishta.R
import kotlinx.android.synthetic.main.v_activity_redeem_points.*
import kotlinx.android.synthetic.main.vguard_toolbar.*

class TrackRedemptionActivity : AppCompatActivity(), View.OnClickListener {

    private fun setCustomActionBar() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.track_your_redemption), "")
        ivBack.setOnClickListener(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_redemption)
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