package com.tfl.vguardrishta.ui.components.vguard.specialComboOffers

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.SpecialSchemes
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.activity_special_combo_offers.recycler_schemes
import kotlinx.android.synthetic.main.activity_special_combo_offers.tvNoData
import kotlinx.android.synthetic.main.scheme_progress.customToolbar
import kotlinx.android.synthetic.main.vguard_toolbar.ivBack
import kotlinx.android.synthetic.main.vguard_toolbar.toolbar_main
import javax.inject.Inject


class SpecialComboOffersActivity :
    BaseActivity<SpecialComboOffersContract.View, SpecialComboOffersContract.Presenter>(),
    SpecialComboOffersContract.View, View.OnClickListener{
    private lateinit var progress: Progress
    @Inject
    lateinit var specialComboOffersPresenter: SpecialComboOffersPresenter

    override fun initPresenter(): SpecialComboOffersContract.Presenter {
        return specialComboOffersPresenter
    }
    override fun getLayoutResId(): Int {
        return R.layout.activity_special_combo_offers
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun finishView() {
        TODO("Not yet implemented")
    }



    override fun initUI() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("",getString(R.string.combo_offers),"")
        progress = Progress(this, R.string.please_wait)
        showSchemes()
        ivBack.setOnClickListener(this)
    }

    override fun loadCards(list: List<SpecialSchemes>) {
        tvNoData.visibility = View.GONE
        val specialComboOffersViewAdapter = SpecialComboOffersViewAdapter(list)
        recycler_schemes.layoutManager=LinearLayoutManager(this)
        recycler_schemes.adapter=specialComboOffersViewAdapter
    }

    override fun showError() {
        toast(resources.getString(R.string.something_wrong))
    }

    override fun showProgress() {
        progress.show()
    }

    override fun showToast(str: String) {
       toast(str)
    }

    override fun stopProgress() {
        progress.dismiss()
    }

    override fun onResume() {
        super.onResume()
        showSchemes()
    }
    fun showSchemes() {
        specialComboOffersPresenter.getSpecialOffers()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }
}