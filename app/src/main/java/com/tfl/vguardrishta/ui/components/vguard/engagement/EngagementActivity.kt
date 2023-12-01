package com.tfl.vguardrishta.ui.components.vguard.engagement

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.extensions.visible
import com.tfl.vguardrishta.models.TdsData
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.activity_engagement.*
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject

class EngagementActivity : BaseActivity<EngagementContract.View, EngagementContract.Presenter>(),
    View.OnClickListener, EngagementContract.View {

    private lateinit var progress: Progress
    private  var accessmentYear :String =""
    @Inject
    lateinit var engagementPresenter: EngagementPresenter


    private fun setCustomActionBar() {
        setSupportActionBar(toolbar_main)
        customToolbarTds.updateToolbar("", getString(R.string.engagement), "")
        ivBack.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

    override fun initPresenter(): EngagementContract.Presenter {
        return engagementPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_engagement
    }

    override fun initUI() {
        setCustomActionBar()
        progress = Progress(this, R.string.please_wait)


    }

    override fun showProgress() {
        progress.show()
    }

    override fun stopProgress() {
        progress.dismiss()
    }

    override fun showToast(msg: String) {
        toast(msg)
    }


}