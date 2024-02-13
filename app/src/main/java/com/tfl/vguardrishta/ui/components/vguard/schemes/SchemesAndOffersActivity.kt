package com.tfl.vguardrishta.ui.components.vguard.schemes

import android.view.View
import androidx.viewpager.widget.ViewPager
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.ui.components.vguard.activeSchemeOffers.ActiveSchemeOffersActivity
import com.tfl.vguardrishta.models.SchemeImages
import com.tfl.vguardrishta.ui.components.vguard.productWiseOffers.ProductWiseOffersActivity
import com.tfl.vguardrishta.ui.components.vguard.specialComboOffers.SpecialComboOffersActivity
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.activity_scheem_and_offers.*
import kotlinx.android.synthetic.main.activity_scheem_and_offers.customToolbar
import kotlinx.android.synthetic.main.activity_scheem_and_offers.indicator
import kotlinx.android.synthetic.main.help_details_layout.*
import kotlinx.android.synthetic.main.vguard_toolbar.*
import java.util.*
import javax.inject.Inject

class SchemesAndOffersActivity :
    BaseActivity<SchemeAndOffersContract.View, SchemeAndOffersContract.Presenter>(),
    SchemeAndOffersContract.View, View.OnClickListener {

    var size: Int = 0
    private lateinit var progress: Progress

    @Inject
    lateinit var schemeAndOffersPresenter: SchemeAndOffersPresenter

    override fun initPresenter(): SchemeAndOffersContract.Presenter {
        return schemeAndOffersPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_scheem_and_offers
    }

    override fun initUI() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.scheem_offers), "")
        ivBack.setOnClickListener(this)
        progress = Progress(this, R.string.please_wait)
        schemeAndOffersPresenter.getSchemesBanners()
        runTimer()
//        val array = arrayListOf<SchemeImages>()
//        for (i in 1..10) {
//            val schemeImages = SchemeImages()
//            schemeImages.imgPath =
//                "https://d2scfqfgqqdgsl.cloudfront.net/idc/home_page/PIov-20.jpg"
//            array.add(schemeImages)
//        }
//        setSchemeViewPager(array)
        llProductWiseOffers.setOnClickListener(this)
        llActiveSchemeOffers.setOnClickListener(this)
        llComboOffers.setOnClickListener(this)

        ivNextBanner.setOnClickListener {
            val currentItem = vpOffers?.currentItem
            if (currentItem!! < size) {
                vpOffers?.currentItem = currentItem+1
            } else if (currentItem >= size) {
                runTimer()
            }
        }
        ivPreviousBanner.setOnClickListener {
            val currentItem = vpOffers?.currentItem
            if (currentItem!! > 0) {
                vpOffers?.currentItem = currentItem-1
            }else if(currentItem<=0){
                runTimer()
            }
        }

        tvContactNo.setOnClickListener {
            AppUtils.showPhoneDialer(this!!, tvContactNo.text.toString())
        }

        tvSupportMail.setOnClickListener {
            AppUtils.showComposeMail(this!!, tvSupportMail.text.toString())
        }

        tvWhatsappNo.setOnClickListener {
            AppUtils.launchWhatsApp(this!!, tvWhatsappNo.text.toString())
        }
    }

    override fun showSchemes(it: List<SchemeImages>?) {
        setSchemeViewPager(it as ArrayList<SchemeImages>)
    }

    private fun setSchemeViewPager(schemeImages: ArrayList<SchemeImages>) {
        size = schemeImages.size
        val mCustomPagerAdapter = SchemeImagesAdapter(this, schemeImages) {}
        vpOffers!!.adapter = mCustomPagerAdapter
        indicator.setViewPager(vpOffers)
        //Set circle indicator radius
        val density = resources.displayMetrics.density
        indicator.radius = 5 * density
        indicator.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
            }

            override fun onPageScrolled(pos: Int, arg1: Float, arg2: Int) {
                vpOffers!!.parent.requestDisallowInterceptTouchEvent(true)
            }

            override fun onPageScrollStateChanged(pos: Int) {

            }
        })
        vpOffers?.adapter?.notifyDataSetChanged()
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
            R.id.llProductWiseOffers -> {
                launchActivity<ProductWiseOffersActivity> { }
            }
            R.id.llActiveSchemeOffers -> {
                launchActivity<ActiveSchemeOffersActivity> { }
            }
            R.id.llComboOffers -> {
              //  AppUtils.showErrorDialog(layoutInflater, this!!, getString(R.string.coming_soon))

                launchActivity<SpecialComboOffersActivity> { }
            }
        }
    }

    private fun runTimer() {
        runOnUiThread {
            var ci = 0
            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    vpOffers?.post {
                        if (size > 0) {
                            vpOffers?.currentItem = ci % size
                            ci++
                        }
                    }
                }
            }, 1000, 5000)
        }
    }
}