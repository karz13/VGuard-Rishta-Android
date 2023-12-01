package com.tfl.vguardrishta.ui.components.vguard.dashboard

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.bumptech.glide.Glide
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.PointsSummary
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.ui.components.vguard.bonusRewards.BonusRewardsActivity
import com.tfl.vguardrishta.ui.components.vguard.productWiseEarnings.ProductWiseEarningActivity
import com.tfl.vguardrishta.ui.components.vguard.schemeWiseEarning.SchemeWiseEarningActivity
import com.tfl.vguardrishta.utils.*
import kotlinx.android.synthetic.main.username_info.*
import kotlinx.android.synthetic.main.v_activity_dashboard.*
import kotlinx.android.synthetic.main.v_activity_view_cart.customToolbar
import kotlinx.android.synthetic.main.vguard_toolbar.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class DashboardActivity : BaseActivity<DashboardContract.View, DashboardContract.Presenter>(),
    DashboardContract.View, View.OnClickListener {

    private lateinit var progress: Progress

    var mYear: Int? = null
    var mMonth: Int? = null
    var sdf: SimpleDateFormat = SimpleDateFormat("MMM yyyy")
    var input: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    var monthYearStr: String? = null

    @Inject
    lateinit var dashboardPresenter: DashboardPresenter

    override fun initPresenter(): DashboardContract.Presenter {
        return dashboardPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.v_activity_dashboard
    }

    override fun initUI() {

        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.dashboard), "")
        val cal = Calendar.getInstance()

        mYear = cal[Calendar.YEAR]
        mMonth = cal[Calendar.MONTH] + 1
        monthYearStr = mYear.toString() + "-" + (mMonth!! + 1) + "-" + 0
        tvYearMonth.setText(formatMonthYear(monthYearStr))
        ivBack.setOnClickListener(this)
        progress = Progress(this, R.string.please_wait)

        llProductWiseEarning.setOnClickListener(this)
        llSchemeWiseEarning.setOnClickListener(this)
        llRewards.setOnClickListener(this)
        dashboardPresenter.getMonthWiseEarning(mMonth!!, mYear!!)

        llYearMonth.setOnClickListener {
            createDialogWithoutDateField()
        }

        if (CacheUtils.getRefreshView()) {
//            dashboardPresenter.getUserDetails()
        }
        showUserDetails()

    }

    override fun showUserDetails() {

        val rishtaUser = CacheUtils.getRishtaUser()
        tvDisplayName.text = rishtaUser.name
        tvUserCode.text = rishtaUser.userCode
        var selfie = rishtaUser.kycDetails.selfie
        selfie = AppUtils.getSelfieUrl() + selfie
        Glide.with(this).load(selfie)
            .placeholder(R.drawable.ic_v_guards_user).into(ivUserImage)

        val pointsSummary = rishtaUser.pointsSummary
//        tvPointsEarned.text = pointsSummary?.pointsBalance ?: "0.00"
//        tvRedeemedPoints.text = pointsSummary?.redeemedPoints ?: "0.00"

        if (rishtaUser.roleId == Constants.RET_USER_TYPE) {
//            ivSchemWise.setColorFilter(ContextCompat.getColor(this, R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);
            ImageViewCompat.setImageTintList(ivSchemWise, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey)));

            tvSchemeWise.setTextColor(resources.getColor(R.color.grey))

            ImageViewCompat.setImageTintList(ivYourRewards, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey)));

            tvYourRewards.setTextColor(resources.getColor(R.color.grey))
            llSchemePoints.visibility=View.VISIBLE
            ivScheme.visibility=View.VISIBLE
            tvDuspScheme.visibility=View.VISIBLE
        }
        if (rishtaUser.professionId == 4) {
            ImageViewCompat.setImageTintList(ivSchemWise, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey)));

            tvSchemeWise.setTextColor(resources.getColor(R.color.grey))
        }
    }

    private fun createDialogWithoutDateField() {

        val pickerDialog = MonthYearPickerDialog()
        pickerDialog.setListener { datePicker, year, month, i2 ->
            monthYearStr = year.toString() + "-" + (month + 1) + "-" + i2
            tvYearMonth.setText(formatMonthYear(monthYearStr))
            mYear = year
            mMonth = month
            dashboardPresenter.getMonthWiseEarning(month, year)
        }
        pickerDialog.show(supportFragmentManager, "MonthYearPickerDialog")
    }

    override fun showPointsSummary(it: PointsSummary) {
        tvPointsEarned.text = it?.totalPointsEarned ?: "0"
        tvRedeemedPoints.text = it?.totalPointsRedeemed ?: "0"
        tvSchemePoints.text = it?.schemePoints ?: "0"
    }

    fun formatMonthYear(str: String?): String? {
        var date: Date? = null
        try {
            date = input.parse(str)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return sdf.format(date)
    }

    override fun showProgress() {
        progress.show()
    }

    override fun stopProgress() {
        progress.dismiss()
    }

    override fun showToast(toast: String) {
        toast(toast)
    }

    override fun showError() {
        toast(resources.getString(R.string.something_wrong))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.llSchemeWiseEarning -> {
                if (CacheUtils.getRishtaUser().roleId == Constants.INF_USER_TYPE && CacheUtils.getRishtaUser().professionId != 4) {
                    launchActivity<SchemeWiseEarningActivity> { }
                } else {
                    AppUtils.showErrorDialog(layoutInflater, this!!, getString(R.string.coming_soon))
                }
            }
            R.id.llRewards -> {
                if (CacheUtils.getRishtaUser().roleId == Constants.INF_USER_TYPE) {
                    launchActivity<BonusRewardsActivity> { }
                } else {
                    AppUtils.showErrorDialog(layoutInflater, this!!, getString(R.string.coming_soon))
                }
            }
            R.id.llProductWiseEarning -> {
                launchActivity<ProductWiseEarningActivity> { }
            }
        }
    }
}