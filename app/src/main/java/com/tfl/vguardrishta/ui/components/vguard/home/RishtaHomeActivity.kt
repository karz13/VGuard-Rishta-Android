package com.tfl.vguardrishta.ui.components.vguard.home

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView

import com.google.firebase.messaging.FirebaseMessaging
import com.tfl.vguardrishta.App.Companion.context
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.extensions.replaceFragment
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.Notifications
import com.tfl.vguardrishta.models.VguardRishtaUser
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.ui.components.logIn.LogInActivity
import com.tfl.vguardrishta.ui.components.vguard.fragment.alert.NotificationFragment
import com.tfl.vguardrishta.ui.components.vguard.fragment.contactUs.ContactUsFragment
import com.tfl.vguardrishta.ui.components.vguard.fragment.home.RishtaHomeFragment
import com.tfl.vguardrishta.ui.components.vguard.fragment.profile.RishtaUserProfileFragment
import com.tfl.vguardrishta.ui.components.vguard.fragment.retailerProfile.RetailerUserProfileFragment
import com.tfl.vguardrishta.ui.components.vguard.registerProduct.RegisterProductListActivity
import com.tfl.vguardrishta.ui.components.vguard.scanCode.ScanCodeActivity
import com.tfl.vguardrishta.utils.*
import kotlinx.android.synthetic.main.v_activity_home.*
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject


class RishtaHomeActivity : BaseActivity<HomeContract.View, HomeContract.Presenter>(),
    HomeContract.View, View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {
    lateinit var alertsBadge: BadgeDrawable
    private var userTouch: Boolean = false
    private lateinit var progress: Progress

    @Inject
    lateinit var homePresenter: HomePresenter
    override fun initPresenter(): HomeContract.Presenter {
        return homePresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.v_activity_home
    }

    override fun initUI() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar(Constants.HOME_SCREEN, "", "")
        setRetailerUiAndLogo()
        progress = Progress(this, R.string.please_wait)
        ivBack.setOnClickListener(this)
        bottom_navigation.setOnNavigationItemSelectedListener(this)
        replaceFragment(RishtaHomeFragment(), R.id.content, "")

        spPreferredLanguage.setSelection(AppUtils.getSelectedLangPos())

        spPreferredLanguage.setOnTouchListener { view, motionEvent ->
            userTouch = true
            return@setOnTouchListener false
        }

        spPreferredLanguage.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0 && userTouch) {
                    userTouch = false
                    when (position) {
                        1 -> {
                            CacheUtils.setSelectedLanguage("en")
                        }
                        2 -> {
                            CacheUtils.setSelectedLanguage("hi")
                        }
                        3 -> {
                            CacheUtils.setSelectedLanguage("mr")
                        }
                        4 -> {
                            CacheUtils.setSelectedLanguage("te")
                        }
                        5 -> {
                            CacheUtils.setSelectedLanguage("bn")
                        }
                        6 -> {
                            CacheUtils.setSelectedLanguage("ml")
                        }
                        7 -> {
                            CacheUtils.setSelectedLanguage("kn")
                        }
                        8 -> {
                            CacheUtils.setSelectedLanguage("ta")
                        }

                    }
                    CacheUtils.setSendSelectedLang(true)
                    refreshCurrentActivity()
                }
            }
        }
        sendSelectedLangId()

        showNotificationCountBadge()
        getNotificationKey()
        homePresenter.getPushNotifications()
    }

    private fun setRetailerUiAndLogo() {
        if (CacheUtils.getRishtaUserType() == Constants.RET_USER_TYPE) {
            ivHomeLogo.setImageResource(R.drawable.rishta_retailer_logo)
        }
    }

    private fun getNotificationKey() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(this@RishtaHomeActivity) { task ->
            if (task.isSuccessful) {
                if (task.result != null && !TextUtils.isEmpty(task.result)) {
                    val token: String = task.result!!
            if (token.isNotEmpty()) {
                val vguardRishtaUser = VguardRishtaUser()
                vguardRishtaUser.fcmToken = token
                homePresenter.updateFcmToken(vguardRishtaUser)
            }
        }
    }

        }
    }

    private fun showNotificationCountBadge() {
        val badge = bottom_navigation.getBadge(R.id.action_notification)
        if (badge == null) {
            alertsBadge =
                bottom_navigation.getOrCreateBadge(R.id.action_notification)

            if (alertsBadge != null) {
                alertsBadge.number = 0
                alertsBadge.backgroundColor = resources.getColor(R.color.yellow)
            }
        }
    }

    private fun sendSelectedLangId() {
        val selectedLangId = AppUtils.getSelectedLangId()
        if (CacheUtils.getSendSelectedLang()) {
            homePresenter.sendSelectedLang(selectedLangId)
        }
    }

    private fun refreshCurrentActivity() {
        this.finish()
        startActivity(intent)
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

    override fun showNotificationDialog(it: List<Notifications>) {
        showDialogForNotification(layoutInflater, this, it[0].alertDesc)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        homePresenter.getNotificationCount()
    }

    fun showDialogForNotification(layoutInflater: LayoutInflater, context: Context, s: String?) {
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater

        val dialogView = inflater.inflate(R.layout.dilog_welcome_msg, null)
        builder.setView(dialogView)
        val dialog = builder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 55)
        dialog.window!!.setBackgroundDrawable(inset)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val ivClose = dialogView.findViewById(R.id.ivClose) as ImageView
        val tvErrorMsg = dialogView.findViewById(R.id.tvErrorMsg) as TextView
        tvErrorMsg.text = s
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        dialog.setOnCancelListener {

        }
        ivClose.setOnClickListener {
            dialog.dismiss()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val fragment = supportFragmentManager.findFragmentById(R.id.content)
        homePresenter.getNotificationCount()
        when (item.itemId) {
            R.id.action_home -> {
                if (fragment !is RishtaHomeFragment) {
                    replaceFragment(RishtaHomeFragment(), R.id.content, "")
                }
            }

            R.id.action_notification -> {
                if (fragment !is NotificationFragment) {
                    replaceFragment(NotificationFragment(), R.id.content, "")
                }
            }
            R.id.action_logout -> {
                showLogoutDialog()
            }

            R.id.action_help -> {
                if (fragment !is ContactUsFragment) {
                    replaceFragment(ContactUsFragment(), R.id.content, "")
                }
            }

            R.id.action_profile -> {
                val rishtaUser = CacheUtils.getRishtaUser()

                if (fragment !is RishtaUserProfileFragment) {
                    if (rishtaUser.roleId == "2") {
                        replaceFragment(RetailerUserProfileFragment(), R.id.content, "")
                    } else {
                        replaceFragment(RishtaUserProfileFragment(), R.id.content, "")
                    }
                }
            }
        }
        return true
    }

    override fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater

        val dialogView = inflater.inflate(R.layout.dilog_error_msg_cancel_ok, null)
        builder.setView(dialogView)
        val dialog = builder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 55)
        dialog.window!!.setBackgroundDrawable(inset)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val ivClose = dialogView.findViewById(R.id.ivClose) as ImageView
        val ivOk = dialogView.findViewById(R.id.ivOk) as ImageView

        val tvErrorMsg = dialogView.findViewById(R.id.tvErrorMsg) as TextView
        tvErrorMsg.text = getString(R.string.are_you_sure_logout)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        dialog.setOnCancelListener {

        }
        ivClose.setOnClickListener {
            dialog.dismiss()
        }

        ivOk.setOnClickListener {
            homePresenter.logoutUser()
        }

    }

    override fun logoutUser() {
        logout()
    }

    private fun logout() {
        PrefUtil(this).setIsLoggedIn(false)
        CacheUtils.clearUserDetails()
        launchActivity<LogInActivity> { }
        finish()
        finishAffinity()
    }

    fun updateCustomToolbar(screenName: String, title: String, subTitle: String) {
        customToolbar.updateToolbar(screenName, title, subTitle)
    }

    fun showProfileScreen() {
        val fragment = supportFragmentManager.findFragmentById(R.id.content)
        val rishtaUser = CacheUtils.getRishtaUser()
        if (fragment !is RishtaUserProfileFragment) {
//            replaceFragment(RishtaUserProfileFragment(), R.id.content, "")
            bottom_navigation.selectedItemId = R.id.action_profile
        }
    }

    override fun showNotificationCount(count: Int) {
        alertsBadge.number = count
    }

    fun autoLogoutUser() {
        logout()
    }
}