package com.tfl.vguardrishta.ui.components.vguard.uploadScanError

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.desmond.squarecamera.CameraActivity
import com.google.android.gms.vision.barcode.Barcode
import com.tfl.barcode_reader.BarcodeReaderActivity
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.FileUploader
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.*
import kotlinx.android.synthetic.main.activity_upload_scan_error.*
import kotlinx.android.synthetic.main.help_details_layout.*
import kotlinx.android.synthetic.main.vguard_toolbar.*
import java.io.File
import javax.inject.Inject

class UploadScanErrorActivity :
    BaseActivity<UploadScanErrorContract.View, UploadScanErrorContract.Presenter>(),
    UploadScanErrorContract.View,
    View.OnClickListener, ActivityFinishListener {
    private val barCodeActivityRequest = 1208

    @Inject
    lateinit var uploadScanErrorPresenter: UploadScanErrorPresenter

    private lateinit var progress: Progress

    override fun initPresenter(): UploadScanErrorContract.Presenter {
        return uploadScanErrorPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_upload_scan_error
    }

    override fun initUI() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.upload_scan_error_), "")
        progress = Progress(this, R.string.please_wait)
        ivBack.setOnClickListener(this)
        CacheUtils.setFileUploader(FileUploader())
        ivClickCamera.setOnClickListener(this)
        btnReportCoupon.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
        tvContactNo.setOnClickListener {
            AppUtils.showPhoneDialer(this!!, tvContactNo.text.toString())
        }

        tvSupportMail.setOnClickListener {
            AppUtils.showComposeMail(this!!, tvSupportMail.text.toString())
        }

        tvWhatsappNo.setOnClickListener {
            AppUtils.launchWhatsApp(this!!, tvWhatsappNo.text.toString())
        }

        ivScanQrCode.setOnClickListener {
            openBarcodeScanner()
        }

    }

    private fun openBarcodeScanner() {
        val launchIntent = BarcodeReaderActivity.getLaunchIntent(this, true, false)
        startActivityForResult(launchIntent, barCodeActivityRequest)
    }


    override fun hideKeyBoard() {

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

    override fun finishView() {
        this.finish()
    }

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
        if (requestCode == barCodeActivityRequest && data != null) {
            val barcode =
                data.getParcelableExtra<Barcode>(BarcodeReaderActivity.KEY_CAPTURED_BARCODE)
            if (!barcode?.rawValue.isNullOrEmpty()) {
                etBarcode.setText("")
                val code = barcode?.rawValue
//                if (isRetailerCoupon!!) {
//                    etBarcode.setText(code)
//                    sendBarcode()
//                } else {
//                    if (code?.length!! > 19) {
//                        val substringCode = barcode.rawValue.substring(9)
//                        etBarcode.setText(substringCode)
//                    } else {
//                        etBarcode.setText(code)
//                    }
//                }
                etBarcode.setText(code)

            } else {
                showToast(getString(R.string.invalid_barcode))
            }
        } else {
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
            if (requestCode == FileUtils.errorCouponPhoto) {
//            AppUtils.checkNullAndSetText(ivClickCamera, file.name, getString(R.string.file))
                Glide.with(this!!).load(photoUri).into(ivClickCamera)
                FileUtils.setToFileUploader(requestCode, compressImage)
            }
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
            R.id.ivClickCamera -> {
                selectOrCaptureImage(FileUtils.errorCouponPhoto)
            }

            R.id.btnReportCoupon -> {
                selectOrCaptureImage(FileUtils.errorCouponPhoto)
            }

            R.id.btnSubmit -> {
                val remarks = etRemarks.text.toString()
                val barCode = etBarcode.text.toString()
                uploadScanErrorPresenter.uploadScanError(remarks,barCode)
            }
        }
    }

    override fun showMsgDialogWithFinish(message: String) {
        AppUtils.showErrorDialogWithFinish(layoutInflater, this, message, this)

    }
}