package com.tfl.vguardrishta.ui.components.vguard.registerProduct

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
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.desmond.squarecamera.CameraActivity
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.CouponResponse
import com.tfl.vguardrishta.models.FileUploader
import com.tfl.vguardrishta.ui.base.BaseFragment
import com.tfl.vguardrishta.ui.components.vguard.home.RishtaHomeActivity
import com.tfl.vguardrishta.ui.components.vguard.scanCode.ScanCodeActivity
import com.tfl.vguardrishta.utils.*
import kotlinx.android.synthetic.main.fragment_new_user_personal_details.*
import kotlinx.android.synthetic.main.fragment_register_product.*
import kotlinx.android.synthetic.main.fragment_register_product.btnNext
import java.io.File
import javax.inject.Inject

class RegisterProductFragment : BaseFragment<RegisterProductContract.View, RegisterProductContract.Presenter>(), RegisterProductContract.View {

    private lateinit var progress: Progress
    lateinit var gpsTracker: GPSTracker
    @Inject
    lateinit var registerProductPresenter: RegisterProductPresenter

    override fun initPresenter(): RegisterProductContract.Presenter {
        return registerProductPresenter
    }

    override fun injectDependencies() {
        (activity as RegisterProductActivity).getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_register_product
    }

    override fun initUI() {
        progress = Progress(context!!, R.string.please_wait)
        CacheUtils.setFileUploader(FileUploader())
        //if (CacheUtils.getRishtaUser().roleId == "2")
            btnNext.setText("Submit")
        val cdr=CacheUtils.getCustomerDetReg()
        //showCouponPoints(cdr.cresp);
        llBillDetails.setOnClickListener {
            selectOrCaptureImage(FileUtils.billDetails)
        }

        llWarranty.setOnClickListener {
            selectOrCaptureImage(FileUtils.warrantyPhoto)
        }

        btnNext.setOnClickListener {
            getInputsAndNext()
        }
        ivDate.setOnClickListener{
            showToast(getString(R.string.click_year_to_change))
            AppUtils.getDate(activity!!, tvPurchaseDate, false)
        }

        setInputs()

        if (!isWarrantyPicMandatory()) {
            tvWarranty.text = getString(R.string.warranty_detail_optional)
        }

        if (!isBillMadatory()) {
            tvBillDetails.text = getString(R.string.bill_details_optional)
        }
        if(cdr.cresp.anomaly==1 && cdr.dealerCategory!="Sub-Dealer")
        {
            tvBillDetails.text="Bill details *"
        }

        tvPurchaseDate.text = getString(R.string.date_of_scan) + ": " + AppUtils.getCurrentDate()
    }

    private fun getInputsAndNext() {
        val cdr = CacheUtils.getCustomerDetReg()

        gpsTracker = GPSTracker(context!!)
        if (!gpsTracker.canGetLocation()) {
            requestLocationService()
            return
        }
        gpsTracker = GPSTracker(context!!)
        val latitude = gpsTracker.latitude
        val longitude = gpsTracker.longitude
        val lat = latitude.toString()
        val long= longitude.toString()

        cdr.latitude=lat
        cdr.longitude=long

        val address = gpsTracker.getAddress(context!!, latitude, longitude)
        var joinAddress = address?.joinAddress

        if (!joinAddress.isNullOrEmpty()) {
            if (joinAddress.contains("null")) {
                joinAddress = joinAddress.replace("null", "")
            }
            cdr.geolocation=joinAddress
        }

//        cdr.cresp.couponCode = etQrCode.text.toString().trim()
//        cdr.cresp.skuDetail = etSku.text.toString().trim()
        cdr.sellingPrice = etSellingPrice.text.toString().trim()
//        cdr.cresp.purchaseDate = tvPurchaseDate.text.toString()
        val rsp = CacheUtils.getRetSelectedProdForScan()
        val fileUploader = CacheUtils.getFileUploader()

        /*if (fileUploader.getBillDetailsFile() == null && isBillMadatory()) {
            showToast(getString(R.string.capture_bill_details))
            return
        }
        val warrantyFile = fileUploader.getWarrantyFile()
        if (warrantyFile == null && isWarrantyPicMandatory()) {
            showToast(getString(R.string.capture_warranty_details))
            return
        }
*/

//        if (cdr.sellingPrice.isNullOrEmpty()) {
//            showToast(getString(R.string.enter_selling_price))
//            return
//        }

        CacheUtils.setCustomerDetReg(cdr)
        //if (CacheUtils.getRishtaUser().roleId == "2")
            registerProductPresenter.sendCustomerData(cdr, true)
       // else
           // (activity as RegisterProductActivity).navigateToCustomer()

    }

    private fun requestLocationService() {
        val dialog = android.app.AlertDialog.Builder(context)
        android.app.AlertDialog.Builder(context).setMessage(getString(R.string.location_is_disabled))
            .setPositiveButton(getString(R.string.enable)) { dialog, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS ))
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            .setCancelable(false)
            .show()
    }

    private fun isWarrantyPicMandatory(): Boolean {
        val rsp = CacheUtils.getRetSelectedProdForScan()
        if ((rsp.productCode == Constants.RetProdCategory.one_INVERTER_BATTERIES
                    || rsp.productCode == Constants.RetProdCategory.three_FAN
                    || rsp.productCode == Constants.RetProdCategory.four_STABILIZER)
        ) {
            return true
        }
        return false
    }

    private fun isBillMadatory(): Boolean {
        val rsp = CacheUtils.getRetSelectedProdForScan()
        if ((rsp.productCode == Constants.RetProdCategory.one_INVERTER_BATTERIES
                    || rsp.productCode == Constants.RetProdCategory.two_WATER_HEATER)
        ) {
            return true
        }
        return false
    }

    private fun setInputs() {
        val customerDetReg = CacheUtils.getCustomerDetReg()
        etQrCode.setText(customerDetReg.cresp.couponCode)
        etSku.setText(customerDetReg.cresp.skuDetail)
//        tvPurchaseDate.text = customerDetReg.cresp.purchaseDate

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

    private fun selectOrCaptureImage(IMAGE_REQUEST: Int) {
        val dialog = AlertDialog.Builder(context)
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
        try {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, IMAGE_REQUEST)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun takePhotoFromCamera(IMAGE_REQUEST: Int) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                val permissionGranted =
                    PermissionUtils.isPermissionGranted(activity!!, Manifest.permission.CAMERA)
                if (permissionGranted) {
                    val startCustomCameraIntent = Intent(activity!!, CameraActivity::class.java)
                    startActivityForResult(startCustomCameraIntent, IMAGE_REQUEST)
                } else {
                    ActivityCompat.requestPermissions(
                        activity!!,
                        arrayOf(Manifest.permission.CAMERA),
                        PermissionUtils.REQUEST_CAMERA_PERMISSION
                    )
                }
            } else {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, IMAGE_REQUEST)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return
        try {
            var photoUri = data!!.data
            var compressImage: String? = null
            var bitmap: Bitmap? = null
            if (photoUri == null) {
                bitmap = data.extras?.get("data") as Bitmap
                photoUri = AppUtils.getImageUri(activity!!, bitmap)
            }
            try {
                compressImage = FileUtils.compressImage(activity!!, photoUri.toString())
            } catch (e: Exception) {
                if (bitmap != null)
                    compressImage = FileUtils.getFilePath(bitmap, context!!)
            }
            if (compressImage == null)
                return
            val file = File(compressImage)
            when (requestCode) {
                FileUtils.billDetails -> {
                    FileUtils.setToFileUploader(requestCode, compressImage)
                    AppUtils.checkNullAndSetText(tvBillDetails, file.name, getString(R.string.file))
                    Glide.with(activity!!).load(photoUri).into(ivBillDetails)
                }

                FileUtils.warrantyPhoto -> {
                    FileUtils.setToFileUploader(requestCode, compressImage)
                    AppUtils.checkNullAndSetText(tvWarranty, file.name, getString(R.string.file))
                    Glide.with(activity!!).load(photoUri).into(ivWarranty)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun showErrorDialog(errorMsg: String?) {
        AppUtils.showErrorDialog(
            layoutInflater,
            context!!,
            errorMsg ?: getString(R.string.something_wrong)
        )
    }

    override fun showCouponPoints(cresp: CouponResponse) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(context!!)
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
        val registerProduct = dialogView.findViewById<TextView>(R.id.registerProduct)
        registerProduct.visibility = View.GONE;
        tvPointsOwnMsg.visibility = View.GONE;
        tvPointsMsg.text = "Congratulations, your customer have received an additional warranty of 365 days.\n"

        val tvCouponPoints = dialogView.findViewById(R.id.tvCouponPoints) as TextView
        val ivGiftImage = dialogView.findViewById(R.id.ivGiftWrap) as ImageView
        val ivClose = dialogView.findViewById(R.id.ivClose) as ImageView
        val tvScanAgain = dialogView.findViewById(R.id.tvScanAgain) as TextView


        val couponPoints = cresp.couponPoints
        val schemePoints = cresp.schemePoints?.toInt()
        /*       if (couponPoints != null && couponPoints.toInt() == 0 && schemePoints != null && schemePoints == 0) {
                   tvPointsOwnMsg.visibility = View.GONE
                   ivGiftImage.visibility = View.GONE
                   //tvCouponPoints.text = getString(R.string.scan_more)
                   tvCouponPoints.text = cresp.errorMsg
                   tvPointsMsg.visibility = View.GONE
               } else if (couponPoints != null && couponPoints.toInt() == 0) {
                   tvPointsOwnMsg.visibility = View.GONE
                   tvCouponPoints.visibility = View.GONE
                   tvPointsMsg.visibility = View.GONE
               } else {
                   tvPointsOwnMsg.visibility = View.VISIBLE
                   ivGiftImage.visibility = View.VISIBLE
                   tvCouponPoints.text = couponPoints
               }*/

        /*   if (schemePoints != null && schemePoints > 0) {// you won hide
               tvPointsOwnMsg.visibility = View.GONE
               val tvSchemePoints = dialogView.findViewById(R.id.tvSchemePoints) as TextView
               tvSchemePoints.visibility = View.VISIBLE
               // tvSchemePoints.text = getString(R.string.earned_scheme_points, schemePoints.toString(), schemePoints.toString())
               tvSchemePoints.text = cresp.errorMsg
           }*/
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        if (CacheUtils.getRishtaUser().roleId == "2") {
            ivClose.visibility = View.VISIBLE
            tvScanAgain.visibility = View.VISIBLE
            ivGiftImage.visibility = View.VISIBLE
            tvCouponPoints.text = cresp.errorMsg
            tvCouponPoints.visibility = View.VISIBLE

            tvPointsMsg.visibility = View.GONE
            tvPointsOwnMsg.visibility = View.GONE

            // tvSchemePoints.text = getString(R.string.earned_scheme_points, schemePoints.toString(), schemePoints.toString())
            ivClose.setOnClickListener {
                activity?.finish()
                val intent1 = Intent(activity, RishtaHomeActivity::class.java)
                intent1.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent1)
            }
            tvScanAgain.setOnClickListener {
                activity?.finish()
                val intent1 = Intent(activity, ScanCodeActivity::class.java)
                intent1.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent1)
            }
        } else {
            if (couponPoints != null && couponPoints.toInt() == 0 && schemePoints != null && schemePoints == 0) {
                tvPointsOwnMsg.visibility = View.GONE
                ivGiftImage.visibility = View.GONE
                //tvCouponPoints.text = getString(R.string.scan_more)
                tvCouponPoints.text = cresp.errorMsg
                //tvPointsMsg.visibility = View.GONE
            } else if (couponPoints != null && couponPoints.toInt() == 0) {
                tvPointsOwnMsg.visibility = View.GONE
                tvCouponPoints.visibility = View.GONE
                //tvPointsMsg.visibility = View.GONE
            } else {
                tvPointsOwnMsg.visibility = View.GONE
                ivGiftImage.visibility = View.VISIBLE
                tvCouponPoints.visibility = View.GONE;
            }
            ivClose.setOnClickListener{
                dialog.dismiss()
            }
            tvScanAgain.visibility = View.GONE
            dialog.setOnDismissListener {
                Log.d("Kam", "on Dismis")
                activity?.finish()
                val intent1 = Intent(activity, RishtaHomeActivity::class.java)
                intent1.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent1)
            }
        }
    }
}