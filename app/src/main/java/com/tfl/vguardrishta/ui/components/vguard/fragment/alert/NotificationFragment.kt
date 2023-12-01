package com.tfl.vguardrishta.ui.components.vguard.fragment.alert

import android.view.View
import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.Notifications
import com.tfl.vguardrishta.ui.base.BaseFragment
import com.tfl.vguardrishta.ui.components.vguard.home.RishtaHomeActivity
import com.tfl.vguardrishta.utils.Constants
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.fragment_notification.*
import javax.inject.Inject

class NotificationFragment :
    BaseFragment<NotificationContract.View, NotificationContract.Presenter>(),
    NotificationContract.View, View.OnClickListener {

    @Inject
    lateinit var notificationPresenter: NotificationPresenter

    lateinit var notificationAdapter: NotificationAdapter

    private lateinit var progress: Progress

    override fun initPresenter(): NotificationContract.Presenter {
        return notificationPresenter
    }

    override fun injectDependencies() {
        (activity?.application as App).applicationComponent.inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_notification
    }

    override fun initUI() {
        progress = Progress(context, R.string.please_wait)
        (activity as RishtaHomeActivity).updateCustomToolbar(
            Constants.NOTIFICATION,
            getString(R.string.notifications),
            ""
        )
        notificationAdapter = NotificationAdapter { notificationPresenter.sendNotificationRead(it) }
        rcvAlerts.adapter = notificationAdapter
        notificationPresenter.getNotifications()
        notificationPresenter.getNotificationCount()
    }

    override fun showNotificationCount(count: Int) {
        (activity as RishtaHomeActivity).showNotificationCount(count)
    }

    override fun showNotifications(it: List<Notifications>) {
        notificationAdapter.mList = it
        notificationAdapter.tempList = it
        notificationAdapter.notifyDataSetChanged()

        tvNoData.visibility = View.GONE

    }

    override fun showNoData() {
        tvNoData.visibility = View.VISIBLE
    }

    override fun showProgress() {
        progress.show()
    }

    override fun stopProgress() {
        progress.dismiss()
    }

    override fun showToast(toast: String) {
        activity?.toast(toast)
    }

    override fun showError() {
        activity?.toast(resources.getString(R.string.something_wrong))
    }

    override fun onClick(v: View?) {
        when (v?.id) {

        }
    }


}