package com.tfl.vguardrishta.ui.components.vguard.specialComboOffers

import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.custom.ProgressStepView
import com.tfl.vguardrishta.models.SchemeProgressData
import com.tfl.vguardrishta.models.SpecialSchemes
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.ui.components.vguard.common.CustomToolbar
import com.tfl.vguardrishta.ui.components.vguard.specialComboOffers.SpecialComboOffersContract.View.DefaultImpls.finishView
import com.tfl.vguardrishta.ui.components.vguard.specialComboOffers.SpecialComboOffersContract.View.DefaultImpls.showError
import com.tfl.vguardrishta.ui.components.vguard.specialComboOffers.SpecialComboOffersContract.View.DefaultImpls.showProgress
import com.tfl.vguardrishta.ui.components.vguard.specialComboOffers.SpecialComboOffersContract.View.DefaultImpls.showToast
import com.tfl.vguardrishta.ui.components.vguard.specialComboOffers.SpecialComboOffersContract.View.DefaultImpls.stopProgress
import com.tfl.vguardrishta.utils.CacheUtils
import kotlinx.android.synthetic.main.scheme_progress.main_container
import kotlinx.android.synthetic.main.scheme_progress.tite_Heading
import kotlinx.android.synthetic.main.vguard_toolbar.ivBack
import kotlinx.android.synthetic.main.vguard_toolbar.toolbar_main
import javax.inject.Inject
import kotlin.jvm.internal.Intrinsics


class SchemeProgressActivity :
    BaseActivity<SpecialComboOffersContract.View, SpecialComboOffersContract.Presenter>(),
    SpecialComboOffersContract.View,
    View.OnClickListener{
    @Inject
    lateinit var  specialComboOffersPresenter: SpecialComboOffersPresenter
    private lateinit var schemeProgress:ProgressStepView


    override fun initPresenter(): SpecialComboOffersContract.Presenter {
       return specialComboOffersPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.scheme_progress
    }

    override fun finishView() {
        TODO("Not yet implemented")
    }

    override fun initUI() {
       setSupportActionBar(toolbar_main)
        val s = CacheUtils.getSchemeProgress()
        schemeProgress = ProgressStepView(this,null,1)
        schemeProgress.currentPoints = s.currentPoints;
        schemeProgress.data = s.mileStoneData;
        tite_Heading.text = s.title;
        main_container.addView(schemeProgress)
        ivBack.setOnClickListener(this)
    }

    override fun loadCards(list: List<SpecialSchemes>) {
       return
    }

    override fun showError() {
        return
    }

    override fun showProgress() {
        return
    }

    override fun showToast(str: String) {
        return
    }

    override fun stopProgress() {
        return
    }

    override fun onClick(p0: View?) {
       when(p0?.id){
           R.id.ivBack->{
               onBackPressed()
           }
       }


    }


}