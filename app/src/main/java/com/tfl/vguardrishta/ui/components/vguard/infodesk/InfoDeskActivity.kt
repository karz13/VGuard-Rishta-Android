package com.tfl.vguardrishta.ui.components.vguard.infodesk

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.viewpager.widget.ViewPager
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.ui.components.vguard.downloads.DownloadsActivity
import com.tfl.vguardrishta.models.SchemeImages
import com.tfl.vguardrishta.ui.components.vguard.schemes.SchemeImagesAdapter
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.help_details_layout.*
import kotlinx.android.synthetic.main.v_activity_info_desk.*
import kotlinx.android.synthetic.main.v_activity_view_cart.customToolbar
import kotlinx.android.synthetic.main.vguard_toolbar.*
import java.util.*
import javax.inject.Inject

class InfoDeskActivity : BaseActivity<InfoDeskContract.View, InfoDeskContract.Presenter>(),
    InfoDeskContract.View, View.OnClickListener {

    var size: Int = 0

    private lateinit var progress: Progress

    @Inject
    lateinit var infoDeskPresenter: InfoDeskPresenter
    override fun initPresenter(): InfoDeskContract.Presenter {
        return infoDeskPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.v_activity_info_desk
    }

    override fun initUI() {
        progress = Progress(this, R.string.please_wait)
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.info_desk), "")
        ivBack.setOnClickListener(this)
        llVguardInfo.setOnClickListener(this)
        llVguardCatalog.setOnClickListener(this)
        llVguardDownloads.setOnClickListener(this)
        infoDeskPresenter.getInfoDeskBanners()
        runTimer()
        ivNext.setOnClickListener {
            val currentItem = vpInfoDesk?.currentItem
            if (currentItem!! < size) {
                vpInfoDesk?.currentItem = currentItem+1
            } else if (currentItem >= size) {
                runTimer()
            }
        }
        ivPrevious.setOnClickListener {
            val currentItem = vpInfoDesk?.currentItem
            if (currentItem!! > 0) {
                vpInfoDesk?.currentItem = currentItem-1
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
        val rishtaUser = CacheUtils.getRishtaUser()
        if (rishtaUser.roleId == Constants.RET_USER_TYPE) {
//            ivSchemWise.setColorFilter(ContextCompat.getColor(this, R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);
            ImageViewCompat.setImageTintList(ivDownloads, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey)));

            tvDownloads.setTextColor(resources.getColor(R.color.grey))

            ImageViewCompat.setImageTintList(ivProdCatalog, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey)));

            tvProdCatalog.setTextColor(resources.getColor(R.color.grey))
        }
    }


    private fun runTimer() {
        runOnUiThread {
            var ci = 0
            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    vpInfoDesk?.post {
                        if (size > 0) {
                            vpInfoDesk?.currentItem = ci % size
                            ci++
                        }
                    }
                }
            }, 1000, 5000)
        }
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

            R.id.llVguardInfo -> {
                launchActivity<DownloadsActivity> { putExtra("screen","vgInfo") }
            }
            R.id.llVguardCatalog -> {
                if(CacheUtils.getRishtaUser().roleId==Constants.INF_USER_TYPE){
                    launchActivity<DownloadsActivity> { putExtra("screen","catalog") }
                }else{
                    AppUtils.showErrorDialog(layoutInflater, this!!, getString(R.string.coming_soon))
                }
            }

            R.id.llVguardDownloads -> {
                if(CacheUtils.getRishtaUser().roleId==Constants.INF_USER_TYPE){
                    launchActivity<DownloadsActivity> { putExtra("screen","downloads") }
                }else{
                    AppUtils.showErrorDialog(layoutInflater, this!!, getString(R.string.coming_soon))
                }
            }
        }
    }

    override fun showInfoDeskBanner(it: List<SchemeImages>?) {
        setInfoDeskPager(it as ArrayList<SchemeImages>)
    }

    private fun setInfoDeskPager(schemeImages: ArrayList<SchemeImages>) {
        size = schemeImages.size
        val mCustomPagerAdapter = SchemeImagesAdapter(this, schemeImages) {}
        vpInfoDesk!!.adapter = mCustomPagerAdapter
        indicator.setViewPager(vpInfoDesk)
        //Set circle indicator radius
        val density = resources.displayMetrics.density
        indicator.radius = 5 * density
        indicator.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
            }

            override fun onPageScrolled(pos: Int, arg1: Float, arg2: Int) {
                vpInfoDesk!!.parent.requestDisallowInterceptTouchEvent(true)
            }

            override fun onPageScrollStateChanged(pos: Int) {

            }
        })
        vpInfoDesk?.adapter?.notifyDataSetChanged()
    }

}