package com.tfl.vguardrishta.ui.components.vguard.fragment.login

import android.view.View
import android.widget.AdapterView
import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.ui.base.BaseFragment
import com.tfl.vguardrishta.ui.components.logIn.LogInActivity
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.CacheUtils
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.SpinnerUtils
import kotlinx.android.synthetic.main.activity_start_page.*
import javax.inject.Inject

class FirstStartFragment :
    BaseFragment<LogInFragmentContract.View, LogInFragmentContract.Presenter>(),
    LogInFragmentContract.View {
    private var userTouch = false

    @Inject
    lateinit var logInFragmentPresenter: LogInFragmentPresenter
    override fun initPresenter(): LogInFragmentContract.Presenter {
        return logInFragmentPresenter
    }

    override fun injectDependencies() {
        (activity?.application as App).applicationComponent.inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_start_page
    }

    override fun initUI() {
        btnStart.setOnClickListener {

            CacheUtils.setRishtaUserType(Constants.INF_USER_TYPE)

            if (cbRetailer.isChecked) {
                CacheUtils.setRishtaUserType(Constants.RET_USER_TYPE)
                CacheUtils.setSubUserType(Constants.RET_SUB_USER_TYPE)
            } else if(cbElectrical.isChecked){
                CacheUtils.setRishtaUserType(Constants.INF_USER_TYPE)
                CacheUtils.setSubUserType(Constants.EC_SUB_USER_TYPE)
            }
            else if(cbAc.isChecked){
                CacheUtils.setRishtaUserType(Constants.INF_USER_TYPE)
                CacheUtils.setSubUserType(Constants.AC_SUB_USER_TYPE)
            }
            if (!cbRetailer.isChecked && !cbElectrical.isChecked && !cbAc.isChecked) {
                showErrorDialog(getString(R.string.select_user_type))
                return@setOnClickListener
            }

            (activity as LogInActivity).navigateToLoginFragment()
        }

        cbElectrical.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                cbRetailer.isChecked = false
                cbAc.isChecked = false
            }
        }

        cbAc.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                cbRetailer.isChecked = false
                cbElectrical.isChecked = false
            }
        }
        cbRetailer.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                cbElectrical.isChecked = false
                cbAc.isChecked = false
            }
        }
        llInfUserType.setOnClickListener {
            cbElectrical.isChecked = !cbElectrical.isChecked
        }

        llAcUserType.setOnClickListener {
            cbAc.isChecked = !cbAc.isChecked
        }

        llRetailerUserType.setOnClickListener {
            cbRetailer.isChecked = !cbRetailer.isChecked
        }

        spPreferredLanguage.setOnTouchListener { _, _ ->
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
    }

    private fun showErrorDialog(msg: String) {
        AppUtils.showErrorDialog(layoutInflater, context!!, msg)
    }

    private fun refreshCurrentActivity() {
        activity?.finish()
        activity?.startActivity(activity?.intent)
    }
}