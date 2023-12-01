package com.tfl.vguardrishta.ui.components.vguard.aircoolerProductRegistration

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.desmond.squarecamera.CameraActivity
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.CouponResponse
import com.tfl.vguardrishta.models.FileUploader
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.utils.*
import kotlinx.android.synthetic.main.activity_air_cooler_product_registration.*
import kotlinx.android.synthetic.main.fragment_register_product.ivBillDetails
import kotlinx.android.synthetic.main.fragment_register_product.ivWarranty
import kotlinx.android.synthetic.main.fragment_register_product.llBillDetails
import kotlinx.android.synthetic.main.fragment_register_product.llWarranty
import kotlinx.android.synthetic.main.fragment_register_product.tvBillDetails
import kotlinx.android.synthetic.main.fragment_register_product.tvWarranty
import kotlinx.android.synthetic.main.vguard_toolbar.*
import java.io.File
import javax.inject.Inject

class AirCoolerProductRegistrationActivity :
    BaseActivity<AirCoolerProductContract.View, AirCoolerProductContract.Presenter>(),
    AirCoolerProductContract.View, View.OnClickListener {


    private lateinit var progress: Progress

    @Inject
    lateinit var airCoolerProductPresenter: AirCoolerProductPresenter

    private fun setCustomActionBar() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.product_registration), "")
        ivBack.setOnClickListener(this)
    }

    override fun initPresenter(): AirCoolerProductContract.Presenter {
        return airCoolerProductPresenter
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_air_cooler_product_registration
    }

    override fun initUI() {
        setCustomActionBar()
        progress = Progress(this, R.string.please_wait)
        CacheUtils.setFileUploader(FileUploader())

        llBillDetails.setOnClickListener {
            selectOrCaptureImage(FileUtils.billDetails)
        }

        llWarranty.setOnClickListener {
            selectOrCaptureImage(FileUtils.warrantyPhoto)
        }
        btnSubmit.setOnClickListener {
            getInput()
        }
    }

    private fun getInput() {
        val name = etName.text.toString().trim()
        if (name.isEmpty()) {
            showToast(getString(R.string.enter_name))
            return
        }
        val contactNo = etContactNumber.text.toString().trim()
        if (contactNo.isEmpty()) {
            showToast(getString(R.string.enter_contact_no))
            return
        } else if (!AppUtils.isValidMobileNo(contactNo)) {
            showToast(getString(R.string.enter_valid_mobileNo))
            return
        }

        val address = etAddress.text.toString().trim()
        if (address.isEmpty()) {
            showToast(getString(R.string.enter_customer_address))
            return
        }
        val fileUploader = CacheUtils.getFileUploader()
        if (fileUploader.getBillDetailsFile() == null) {
            showToast(getString(R.string.capture_bill_details))
            return
        }
        /*   val warrantyFile = fileUploader.getWarrantyFile()
           if (warrantyFile == null) {
               showToast(getString(R.string.capture_warranty_details))
               return
           }*/
        val cdr = CacheUtils.getCustomerDetReg()
        cdr.name = name
        cdr.contactNo = contactNo
        cdr.currAdd = address
        airCoolerProductPresenter.sendDataToServer(cdr)
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

    private fun choosePhotoFromGallery(IMAGE_REQUEST: Int) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, IMAGE_REQUEST)
    }

    private fun takePhotoFromCamera(IMAGE_REQUEST: Int) {
        if (Build.VERSION.SDK_INT >= 23) {
            val permissionGranted =
                PermissionUtils.isPermissionGranted(this, Manifest.permission.CAMERA)
            if (permissionGranted) {
                val startCustomCameraIntent = Intent(this, CameraActivity::class.java)
                startActivityForResult(startCustomCameraIntent, IMAGE_REQUEST)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    PermissionUtils.REQUEST_CAMERA_PERMISSION
                )
            }
        } else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, IMAGE_REQUEST)
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

    override fun showCouponPoints(it: CouponResponse?) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dilog_scratch_card, null)
        builder.setView(dialogView)
        val dialog = builder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 55)
        dialog.window!!.setBackgroundDrawable(inset)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val tvPointsOwnMsg = dialogView.findViewById<TextView>(R.id.tvPointsWonMsg)
        val tvPointsMsg = dialogView.findViewById<TextView>(R.id.tvPointsMsg)

        tvPointsOwnMsg.text = getString(R.string.you_won)
        tvPointsMsg.text = getString(R.string.stars)
        val tvCouponPoints = dialogView.findViewById(R.id.tvCouponPoints) as TextView
        val couponPoints = it?.couponPoints

        tvCouponPoints.text = couponPoints

        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        dialog.setOnCancelListener {
            finish()
        }
    }

    override fun showErrorDialog(errorMsg: String?) {
        AppUtils.showErrorDialog(
            layoutInflater,
            this,
            errorMsg ?: getString(R.string.something_wrong)
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        var photoUri = data!!.data
        var compressImage: String? = null
        var bitmap: Bitmap? = null
        if (photoUri == null) {
            bitmap = data.extras?.get("data") as Bitmap
            photoUri = AppUtils.getImageUri(this, bitmap)
        }
        try {
            compressImage = FileUtils.compressImage(this, photoUri.toString())
        } catch (e: Exception) {
            if (bitmap != null)
                compressImage = FileUtils.getFilePath(bitmap, this)
        }
        if (compressImage == null)
            return
        val file = File(compressImage)
        when (requestCode) {
            FileUtils.billDetails -> {
                FileUtils.setToFileUploader(requestCode, compressImage)
                AppUtils.checkNullAndSetText(tvBillDetails, file.name, getString(R.string.file))
                Glide.with(this).load(photoUri).into(ivBillDetails)
            }

            FileUtils.warrantyPhoto -> {
                FileUtils.setToFileUploader(requestCode, compressImage)
                AppUtils.checkNullAndSetText(tvWarranty, file.name, getString(R.string.file))
                Glide.with(this).load(photoUri).into(ivWarranty)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }
}