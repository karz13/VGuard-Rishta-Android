package com.tfl.vguardrishta.ui.components.vguard.specialComboOffers

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.tfl.vguardrishta.R
import kotlinx.android.synthetic.main.v_activity_redeem_points.*
import kotlinx.android.synthetic.main.vguard_toolbar.*

class SpecialComboOffersActivity : AppCompatActivity(), View.OnClickListener {
    private fun setCustomActionBar() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.combo_offers), "")
        ivBack.setOnClickListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_special_combo_offers)
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