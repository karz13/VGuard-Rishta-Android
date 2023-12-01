package com.tfl.vguardrishta.ui.components.vguard.downloads

import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.visible
import com.tfl.vguardrishta.models.Category
import com.tfl.vguardrishta.models.DownloadData
import com.tfl.vguardrishta.models.User
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.AppUtils
import com.tfl.vguardrishta.utils.Progress
import kotlinx.android.synthetic.main.activity_downloads.*
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject

/**
 * Created by Shanmuka on 5/9/2019.
 */
class DownloadsActivity : BaseActivity<DownloadsContract.View, DownloadsContract.Presenter>(),
    DownloadsContract.View {

    @Inject
    lateinit var downloadsPresenter: DownloadsPresenter

    private lateinit var progress: Progress
    private lateinit var adapter: DownloadsAdapter
    private lateinit var user: User

    override fun initUI() {
        setSupportActionBar(toolbar_main)
        val stringExtra = intent.getStringExtra("screen")



        progress = Progress(this, R.string.please_wait)

        ivBack.setOnClickListener {
            onBackPressed()
        }
        adapter = DownloadsAdapter { downloadsPresenter.openDownLoadData(it) }
        if (stringExtra == "vgInfo") {
            customToolbar.updateToolbar("", getString(R.string.v_guard_info), "")
            downloadsPresenter.getVguardInfoDownloads()

        } else if (stringExtra == "catalog") {
            customToolbar.updateToolbar("", getString(R.string.v_guard_product_catalog), "")
            downloadsPresenter.getVguardCatlogDownloads()
        } else if (stringExtra == "downloads") {
            customToolbar.updateToolbar("", getString(R.string.downloads_small), "")

            downloadsPresenter.getDownloads()
        }
//        downloadsPresenter.getCategory()

    }

    override fun initPresenter() = downloadsPresenter

    override fun injectDependencies() = getApplicationComponent().inject(this)

    override fun getLayoutResId() = R.layout.activity_downloads

    override fun finishView() = finish()

    override fun hideKeyboard() = AppUtils.hideKeyboard(this)

    override fun showProgress() = progress.show()

    override fun stopProgress() = progress.dismiss()

    override fun showToast(toast: String) = AppUtils.showToast(this, toast)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun setToSpinner(arrayList: ArrayList<Category>) {

    }

    override fun showNoData() {
        stopProgress()
        rcvDownloads.visible = false
        tvNoData.visible = true
    }

    override fun setDownLoadsToAdapter(list: List<DownloadData>) {
        stopProgress()
        rcvDownloads.visible = true
        tvNoData.visible = false
        rcvDownloads.layoutManager = LinearLayoutManager(this)
        rcvDownloads.adapter = adapter
        adapter.mList = list
        adapter.notifyDataSetChanged()
    }

    override fun openFile(it: DownloadData) {

        AppUtils.openPDFWithBaseUrl(this, it.fileName)
//        if (it.fileName.isNullOrEmpty()) {
//            showToast(getString(R.string.unable_to_open_file))
//        } else {
//            val url = ApiService.downLoadURL + it.fileName
//            try {
//                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//                startActivity(browserIntent)
//            } catch (e: ActivityNotFoundException) {
//                showToast(getString(R.string.no_activity_found))
//            }
//        }
    }

}