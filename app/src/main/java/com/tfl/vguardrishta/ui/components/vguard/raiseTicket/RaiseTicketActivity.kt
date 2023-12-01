package com.tfl.vguardrishta.ui.components.vguard.raiseTicket

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.desmond.squarecamera.CameraActivity
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.custom.CustomSpinner
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.FileUploader
import com.tfl.vguardrishta.models.TicketType
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.ui.components.vguard.ticketHistory.TicketHistoryActivity
import com.tfl.vguardrishta.utils.*
import kotlinx.android.synthetic.main.v_activity_raise_ticket.*
import kotlinx.android.synthetic.main.vguard_toolbar.*
import java.io.File
import javax.inject.Inject

class RaiseTicketActivity : BaseActivity<RaiseTicketContract.View, RaiseTicketContract.Presenter>(),
    RaiseTicketContract.View,
    View.OnClickListener, ActivityFinishListener {
    private var defaultTracker: Tracker? = null


    @Inject
    lateinit var raiseTicketPresenter: RaiseTicketPresenter
    private lateinit var progress: Progress


    override fun initPresenter(): RaiseTicketContract.Presenter {
        return raiseTicketPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.v_activity_raise_ticket
    }

    override fun initUI() {
        defaultTracker = (application as App).getDefaultTracker()
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.raise_ticket), "")
        ivBack.setOnClickListener(this)
        progress = Progress(this, R.string.please_wait)
        CacheUtils.setFileUploader(FileUploader())

        val vu = CacheUtils.getRishtaUser()
        var selfie = vu.kycDetails.selfie
        selfie = AppUtils.getSelfieUrl() + selfie
        Glide.with(this).load(selfie)
            .placeholder(R.drawable.ic_v_guards_user).into(ivUserImage)
        tvDisplayName.text = vu.name
        tvUserCode.text = vu.userCode
        raiseTicketPresenter.getTicketTypes()
        btnProceed.setOnClickListener(this)

        llIssueTypePic.setOnClickListener {
            selectOrCaptureImage(FileUtils.issueType)
        }
        btnTicketHistory.setOnClickListener(this)



        tvContactNo.setOnClickListener {
            AppUtils.showPhoneDialer(this, tvContactNo.text.toString())

        }

        tvSupportMail.setOnClickListener {
            AppUtils.showComposeMail(this, tvSupportMail.text.toString())

        }

        tvWhatsappNo.setOnClickListener {
            AppUtils.launchWhatsApp(this, tvWhatsappNo.text.toString())
        }

        tvTermsAndConditions.setOnClickListener {
            AppUtils.showTerms(this)
        }

        tvFaq.setOnClickListener {
            AppUtils.showFaq(this)
        }


        ivIssuePic.setOnClickListener(this)

        hideKeyBoard()

    }

    override fun getContext(): Context {
        return this
    }

    override fun onResume() {
        super.onResume()
        defaultTracker?.setScreenName("Raise Ticket")
        defaultTracker?.setAppName("APP")
        defaultTracker?.send(HitBuilders.ScreenViewBuilder().build())
    }

    private fun choosePhotoFromGallery(IMAGE_REQUEST: Int) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, IMAGE_REQUEST)
    }

    private fun selectOrCaptureImage(IMAGE_REQUEST: Int) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(getString(R.string.select_action))
        val items = arrayOf(
            getString(R.string.select_from_gallery),
            getString(R.string.capture_from_camera)
        )
        dialog.setItems(
            items
        ) { _, which ->
            when (which) {
                0 -> choosePhotoFromGallery(IMAGE_REQUEST)
                1 -> takePhotoFromCamera(IMAGE_REQUEST)
            }
        }
        dialog.show()
    }

    override fun showTicketTypes(it: List<TicketType>) {
        hideKeyBoard()
        spIssueType?.adapter = CustomSpinner(
            this!!,
            android.R.layout.simple_list_item_1,
            it.toTypedArray(),
            Constants.TICKET_TYPE
        )
    }

    override fun hideKeyBoard() = AppUtils.hideKeyboard(this)


    private fun takePhotoFromCamera(IMAGE_REQUEST: Int) {
        if (Build.VERSION.SDK_INT >= 23) {
            val permissionGranted =
                PermissionUtils.isPermissionGranted(this, Manifest.permission.CAMERA)
            if (permissionGranted) {
                val startCustomCameraIntent = Intent(this!!, CameraActivity::class.java)
                startActivityForResult(startCustomCameraIntent, IMAGE_REQUEST)
            } else {
                ActivityCompat.requestPermissions(
                    this!!,
                    arrayOf(Manifest.permission.CAMERA),
                    PermissionUtils.REQUEST_CAMERA_PERMISSION
                )
            }
        } else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, IMAGE_REQUEST)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return
        var photoUri: Uri?
        photoUri = data!!.data
        var compressImage: String? = null
        var bitmap: Bitmap? = null
        if (photoUri == null) {
            val bitmap = data.extras?.get("data") as Bitmap
            photoUri = AppUtils.getImageUri(this!!, bitmap)
        }
        try {
            compressImage = FileUtils.compressImage(this!!, photoUri.toString())
        } catch (e: Exception) {
            if (bitmap != null)
                compressImage = FileUtils.getFilePath(bitmap, this)
        }
        if (compressImage == null)
            return
        val file = File(compressImage)
        if (requestCode == FileUtils.issueType) {
            AppUtils.checkNullAndSetText(tvIssuePic, file.name, getString(R.string.file))
            Glide.with(this!!).load(photoUri).into(ivIssuePic)
            FileUtils.setToFileUploader(requestCode, compressImage)
            tvCountIssuePic.text = "1"
        }
        super.onActivityResult(requestCode, resultCode, data)

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

            R.id.btnProceed -> {
                getInputs()
            }

            R.id.btnTicketHistory -> {
                launchActivity<TicketHistoryActivity> { }
            }

            R.id.ivIssuePic -> {
                val issuePhotoFIle = CacheUtils.getFileUploader().getIssuePhotoFile()
                if (issuePhotoFIle != null) {
                    FileUtils.zoomFileImage(this!!, issuePhotoFIle)
                } else {

                }
            }
        }
    }

    private fun getInputs() {
        if (spIssueType.selectedItem is TicketType) {
            val ticketType = spIssueType.selectedItem as TicketType
            val issueDesc = etIssueDisc.text.toString()
            raiseTicketPresenter.submit(ticketType, issueDesc)
        }
    }

    override fun showMsgDialog(message: String) {
        AppUtils.showErrorDialog(layoutInflater, this, message)
    }

    override fun showMsgDialogWithFinish(message: String) {
        AppUtils.showErrorDialogWithFinish(layoutInflater, this, message, this)
    }

    override fun finishView() {
        this.finish()
    }
}