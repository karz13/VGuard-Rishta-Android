package com.tfl.vguardrishta.ui.components.vguard.scanCode

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.net.Uri
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.barcode.Barcode
import com.tfl.barcode_reader.BarcodeReaderActivity
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.models.*
import com.tfl.vguardrishta.scratchCard.ScratchCard
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.ui.components.vguard.aircoolerProductRegistration.AirCoolerProductRegistrationActivity
import com.tfl.vguardrishta.ui.components.vguard.registerProduct.RegisterProductActivity
import com.tfl.vguardrishta.ui.components.vguard.scanHistory.ScanCodeHistoryActivity
import com.tfl.vguardrishta.ui.components.vguard.uploadScanError.UploadScanErrorActivity
import com.tfl.vguardrishta.utils.*
import kotlinx.android.synthetic.main.help_details_layout.*
import kotlinx.android.synthetic.main.v_activity_scan_code.*
import kotlinx.android.synthetic.main.vguard_toolbar.*
import javax.inject.Inject


class ScanCodeActivity : BaseActivity<ScanCodeContract.View, ScanCodeContract.Presenter>(),
    ScanCodeContract.View {

    @Inject
    lateinit var scanCodePresenter: ScanCodePresenter
    lateinit var gpsTracker: GPSTracker
    private lateinit var progress: Progress
    private val barCodeActivityRequest = 1208
    private val dealerCategory=CacheUtils.getCustomerDetReg().dealerCategory
    private val contactNo=CacheUtils.getCustomerDetReg().contactNo
    private var type: String? = null

    override fun initUI() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.scan_code), "")
        progress = Progress(this, R.string.please_wait)

        onClicks()
        if (CacheUtils.getRishtaUser().roleId == Constants.RET_USER_TYPE) {
            cvBtnReportCoupon.visibility = View.GONE
        }
        locationPermissionEnabled()
        type = intent?.getStringExtra("type")
        if (!type.isNullOrEmpty() && type.equals("airCooler")) {
            lbCode.text = getString(R.string.product_serial_number)
        } else if (CacheUtils.getRishtaUser().roleId == Constants.RET_USER_TYPE) {
            val cdr = CacheUtils.getCustomerDetReg()
            if (cdr.contactNo.isNullOrEmpty()) {
                showErrorDialog(getString(R.string.enter_mandatory_fields), true)
            }
        }

        if (CacheUtils.getRishtaUser().roleId == Constants.INF_USER_TYPE) {
            val rishtaUser = CacheUtils.getRishtaUser()
           // showErrorDialog("Testing "+rishtaUser.userCode, true)
            scanCodePresenter.getScanPopUp(rishtaUser.userCode)
        }
//testSimulation();

    }

    private fun locationPermissionEnabled(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
               this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                2
            )
            return false
        }
        else
        {
            return true
        }
    }


    private fun testSimulation () {

        val c =CouponResponse();
        c.couponPoints = "50";
        c.couponCode = "XXXXYYYYWWWWDDDD";
        c.skuDetail = "324556234"

        val cdr = CustomerDetailsRegistration();
        cdr.cresp = c;
        CacheUtils.setCustomerDetReg(cdr)
        showNormalScratchCard(c)



    }
    override fun initPresenter() = scanCodePresenter

    override fun injectDependencies() = getApplicationComponent().inject(this)

    override fun getLayoutResId() = R.layout.v_activity_scan_code

    override fun finishView() = finish()

    override fun showProgress() = progress.show()

    override fun stopProgress() = progress.dismiss()

    override fun showToast(message: String) = AppUtils.showToast(this, message)

    override fun showLongToast(message: String) = AppUtils.showLongToast(this, message)

    override fun hideKeyboard() = AppUtils.hideKeyboard(this)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {

        when (requestCode) {
            2 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED && grantResults[1] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED)&&(ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED)) {

                    }
                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Permission Needed")
                    builder.setMessage("Location permission should be enabled to scan the code.")
                    builder.setPositiveButton("Allow") { dialog, which ->
                        val i = Intent()
                        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        i.setData(Uri.parse("package:" + this.getPackageName()))
                        this.startActivity(i)
                    }

                    builder.setNegativeButton("Deny") { dialog, which ->
                        dialog.dismiss()
                    }

                    builder.show()

                }
                return
            }
        }
    }
    private fun onClicks() {

        tvContactNo.setOnClickListener {
            AppUtils.showPhoneDialer(this!!, tvContactNo.text.toString())
        }

        tvSupportMail.setOnClickListener {
            AppUtils.showComposeMail(this!!, tvSupportMail.text.toString())
        }

        tvWhatsappNo.setOnClickListener {
            AppUtils.launchWhatsApp(this!!, tvWhatsappNo.text.toString())
        }

        ivBack.setOnClickListener {
            onBackPressed()
        }

        llScanCodeLbl.setOnClickListener {
            openBarcodeScanner()
        }

        ivScanQrCode.setOnClickListener {
            openBarcodeScanner()
        }

        btnProceed.setOnClickListener {

            sendBarcode()
        }
        ivGotoScanHistory.setOnClickListener {
            if (!type.isNullOrEmpty() && type.equals("airCooler", false))
                launchActivity<ScanCodeHistoryActivity> {
                    putExtra("type", "airCooler")
                }
            else
                launchActivity<ScanCodeHistoryActivity>()
        }
        llGoToScanHistory.setOnClickListener {
            if (!type.isNullOrEmpty() && type.equals("airCooler", false))
                launchActivity<ScanCodeHistoryActivity> {
                    putExtra("type", "airCooler")
                }
            else
                launchActivity<ScanCodeHistoryActivity>()

        }
        btnReportCoupon.setOnClickListener {
            launchActivity<UploadScanErrorActivity> { }
        }
    }

    private fun sendBarcode() {
        val barCode = etBarcode.text.toString().trim()
        val couponData = CouponData()
        couponData.couponCode = barCode
//        couponData.userCode = loginUser.userCode
        couponData.from = "APP"
        gpsTracker = GPSTracker(this)
        if (!gpsTracker.canGetLocation()) {
            requestLocationService()
            return
        }
        if(!locationPermissionEnabled())
        {
            return
        }
        gpsTracker = GPSTracker(this)
        val latitude = gpsTracker.latitude
        val longitude = gpsTracker.longitude
        val lat = latitude.toString()
        val long= longitude.toString()

        couponData.latitude=lat
        couponData.longitude=long
        couponData.userMobileNumber=contactNo

        val address = gpsTracker.getAddress(this, latitude, longitude)
        var joinAddress = address?.joinAddress

        if (!joinAddress.isNullOrEmpty()) {
            if (joinAddress.contains("null")) {
                joinAddress = joinAddress.replace("null", "")
            }
            couponData.geolocation=joinAddress
        }

        if (!type.isNullOrEmpty() && type.equals("airCooler", false))
            scanCodePresenter.isValidBarcode(couponData, 1,"",0,dealerCategory)
        else
            scanCodePresenter.isValidBarcode(couponData, 0,"",0,dealerCategory)
    }
    private fun requestLocationService() {
        val dialog = android.app.AlertDialog.Builder(this)
        android.app.AlertDialog.Builder(this).setMessage(getString(R.string.location_is_disabled))
            .setPositiveButton(getString(R.string.enable)) { dialog, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS ))
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            .setCancelable(false)
            .show()
    }

    override fun verifyCategorySelected(cresp: CouponResponse) {
        val productDetail = ProductDetail()
        productDetail.productCode = cresp.categoryId
        productDetail.productName = cresp.category
        CacheUtils.setRetSelectedProdForScan(productDetail)
        showProceedToProdReg(cresp)
    }

    override fun showAircoolerDialog(cr: CouponResponse?) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater

        val dialogView = inflater.inflate(R.layout.dilog_air_cooler_msg_cancel_ok, null)
        builder.setView(dialogView)
        val dialog = builder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 55)
        dialog.window!!.setBackgroundDrawable(inset)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val ivClose = dialogView.findViewById(R.id.ivClose) as ImageView
        val ivOk = dialogView.findViewById(R.id.ivOk) as ImageView

        val tvErrorMsg = dialogView.findViewById(R.id.tvErrorMsg) as TextView
        // val tvPoints = dialogView.findViewById(R.id.tvPoints) as TextView
        // tvErrorMsg.text = it?.errorMsg
        val points = cr?.couponPoints
        //  val printMsg = "Congratulations!\n" +
        //           "You have won $points stars by scanning the Air cooler."
        //  tvErrorMsg.text = printMsg
        tvErrorMsg.text = getString(R.string.valid_coupon_please_proceed_to_prod_regi)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        dialog.setOnCancelListener {

        }
        ivClose.setOnClickListener {
            dialog.dismiss()
        }

        ivOk.setOnClickListener {
            dialog.dismiss()
            val cdr = CustomerDetailsRegistration()
            cr?.couponCode = etBarcode.text.toString()
            cdr.cresp = cr!!
            CacheUtils.setCustomerDetReg(cdr)
            launchActivity<AirCoolerProductRegistrationActivity>()
            // finishView()

        }
    }

    private fun showProceedToProdReg(cresp: CouponResponse) {
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
        tvErrorMsg.text = getString(R.string.valid_coupon_please_proceed_to_prod_regi)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        dialog.setOnCancelListener {

        }
        ivClose.setOnClickListener {
            dialog.dismiss()
        }

        ivOk.setOnClickListener {
            dialog.dismiss()
            var cdr: CustomerDetailsRegistration? = null
            if (CacheUtils.getRishtaUser().roleId == "2")
                cdr = CacheUtils.getCustomerDetReg()
            else
                cdr = CustomerDetailsRegistration()
            cresp.couponCode = etBarcode.text.toString()
            cdr.cresp = cresp
            CacheUtils.setCustomerDetReg(cdr)
            launchActivity<RegisterProductActivity> {}
        }
    }

    private fun openBarcodeScanner() {
        val launchIntent = BarcodeReaderActivity.getLaunchIntent(this, true, false)
        startActivityForResult(launchIntent, barCodeActivityRequest)
    }


    override fun clearCoupon() {
        etBarcode.setText("")
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
                showToast(getString(R.string.invalidshowScratchCard_barcode))
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun clearMobileNumber() {

    }

    override fun showScratchCard(it: CouponResponse?) {
        if (it!!.couponPoints != null) {
            showNormalScratchCard(it)
        }
    }

    override fun showPINPopup(it: CouponResponse?) {
        if (it!!.couponPoints != null) {
            showPinPopUpDialog()
        }
    }


    private fun showPinPopUpDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater

        val dialogView = inflater.inflate(R.layout.enter_pinpopup, null)
        builder.setView(dialogView)
        val dialog = builder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 55)
        dialog.window!!.setBackgroundDrawable(inset)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val message =dialogView.findViewById(R.id.tvErrorMsg) as TextView
        val ivClose = dialogView.findViewById(R.id.ivClose) as ImageView
        val submit_pin = dialogView.findViewById(R.id.submit_pin) as Button
        val pin_box = dialogView.findViewById(R.id.pin_box) as EditText
        message.setText("Please enter 4-digit pin to scan QR code  "+etBarcode.text.toString().trim())
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)


        dialog.show()

        dialog.setOnCancelListener {

        }
        ivClose.setOnClickListener {
                dialog.dismiss()
        }
        submit_pin.setOnClickListener {

            val barCode = etBarcode.text.toString().trim()
            val pin =pin_box.text.toString().trim()
            if(pin!="")
                dialog.dismiss()
            val couponData = CouponData()
            couponData.couponCode = barCode
//        couponData.userCode = loginUser.userCode
            couponData.from = "APP"
            gpsTracker = GPSTracker(this)
            if (!gpsTracker.canGetLocation()) {
                requestLocationService()
                return@setOnClickListener
            }
            if(!locationPermissionEnabled())
            {
                return@setOnClickListener
            }
            gpsTracker = GPSTracker(this)
            val latitude = gpsTracker.latitude
            val longitude = gpsTracker.longitude
            val lat = latitude.toString()
            val long= longitude.toString()

            couponData.latitude=lat
            couponData.longitude=long
            couponData.userMobileNumber=contactNo

            val address = gpsTracker.getAddress(this, latitude, longitude)
            var joinAddress = address?.joinAddress

            if (!joinAddress.isNullOrEmpty()) {
                if (joinAddress.contains("null")) {
                    joinAddress = joinAddress.replace("null", "")
                }
                couponData.geolocation=joinAddress
            }
            scanCodePresenter.isValidBarcode(couponData, 1, pin, 1, dealerCategory)
        }
    }



    override fun showErrorDialog(s: String, isSingleScan: Boolean) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater

        val dialogView = inflater.inflate(R.layout.dilog_error_msg, null)
        builder.setView(dialogView)
        val dialog = builder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 55)
        dialog.window!!.setBackgroundDrawable(inset)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val ivClose = dialogView.findViewById(R.id.ivClose) as ImageView
        val tvErrorMsg = dialogView.findViewById(R.id.tvErrorMsg) as TextView
        tvErrorMsg.text = s
        if (isSingleScan) {
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
        } else {
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)
        }

        dialog.show()

        dialog.setOnCancelListener {

        }
        ivClose.setOnClickListener {
            if (isSingleScan)
                finishView()
            else
                dialog.dismiss()
        }
    }

    private fun showNormalScratchCard(cresp: CouponResponse) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater

        val dialogView = inflater.inflate(R.layout.dilog_scratch_card, null)
        builder.setView(dialogView)
        val dialog = builder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 55)
        dialog.window!!.setBackgroundDrawable(inset)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val ivScratchCard = dialogView.findViewById(R.id.ivScratchCard) as ScratchCard
        val tvPointsOwnMsg = dialogView.findViewById<TextView>(R.id.tvPointsWonMsg)
        val tvPointsMsg = dialogView.findViewById<TextView>(R.id.tvPointsMsg)
        val tvPointsMsg2 = dialogView.findViewById<TextView>(R.id.tvPointsMsg2)
        val tvClubPoints = dialogView.findViewById<TextView>(R.id.tvClubPoints);
        val registerProduct = dialogView.findViewById<TextView>(R.id.registerProduct)
        val ivClose = dialogView.findViewById<ImageView>(R.id.ivClose);
            tvPointsOwnMsg.text = getString(R.string.you_won)
        tvPointsMsg.text = getString(R.string.points)
        val tvCouponPoints = dialogView.findViewById(R.id.tvCouponPoints) as TextView
        val couponPoints = cresp.couponPoints

        tvCouponPoints.text = couponPoints
        ivClose.setOnClickListener{
            dialog.dismiss();
        }

        if (cresp.clubPoints != null && cresp.clubPoints!!.toDouble() > 0) {
            tvClubPoints.visibility = View.VISIBLE
            tvClubPoints.text = getString(R.string.club_points) + ":" + cresp.clubPoints
        }

        var extraBonusPoint = ""
        if (cresp.schemePoints != null && cresp.schemePoints!!.toDouble() > 0) {
            extraBonusPoint =
                "\n" + getString(R.string.extra_bonus_point) + " : ${cresp.schemePoints}"
        }
        tvPointsMsg2.text =
            getString(R.string.base_points) + " : ${cresp.basePoints}" + extraBonusPoint

        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
        registerProduct.setOnClickListener{
            launchActivity<RegisterProductActivity> {  }
        }
        dialog.setOnCancelListener {
            if (cresp!!.transactId != null && cresp.bitEligibleScratchCard) {
                scanCodePresenter.getBonusPoints(cresp.transactId!!)
            }
        }
    }

    override fun showBonusScratchCard(it: CouponResponse) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dilog_scratch_card, null)
        builder.setView(dialogView)
        val dialog = builder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 55)
        dialog.window!!.setBackgroundDrawable(inset)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val ivScratchCard = dialogView.findViewById(R.id.ivScratchCard) as ScratchCard
        ivScratchCard.visibility = View.VISIBLE
        val tvPointsOwnMsg = dialogView.findViewById<TextView>(R.id.tvPointsWonMsg)
        val tvPointsMsg = dialogView.findViewById<TextView>(R.id.tvPointsMsg)
        tvPointsMsg.setText(getString(R.string.lucky_bonus_points))
        tvPointsMsg.visibility = View.GONE
//        tvPointsOwnMsg.text = getString(R.string.you_won)
        tvPointsOwnMsg.text = it.errorMsg ?: ""
        val tvPoints = dialogView.findViewById(R.id.tvCouponPoints) as TextView
        tvPoints.visibility = View.GONE
        val couponPoints = it?.promotionPoints
        if (!couponPoints.isNullOrEmpty() && couponPoints.contains(".")) {
            val split = couponPoints.split(".")
            tvPoints.text = split[0]
        } else {
            tvPoints.text = couponPoints
        }
        // tvPoints.text = "5000"
        ivScratchCard.setOnScratchListener { scratchCard, visiblePercent ->
            if (visiblePercent > 0.3) {
                scratchCard.visibility = View.GONE
                dialog.setCancelable(true)
                dialog.setCanceledOnTouchOutside(true)
            }
        }
        dialog.setOnCancelListener {
            if (CacheUtils.getRishtaUserType() == Constants.RET_USER_TYPE) {
                launchActivity<RegisterProductActivity> {
                }
            }
        }
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    override fun scanningUnderLocation(isUnderLocation: Boolean) {
        // if (isUnderLocation)
        //     showBarCodeScanner(true)
        /*  else
              showToast(getString(R.string.search_person_out_of_range))*/
    }



    override fun processScanCodePopup( it: MobileValidation?) {
        val builder = AlertDialog.Builder(this)
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
        val ivImage = dialogView.findViewById(R.id.ivWelcome) as ImageView
        val tvVid = dialogView.findViewById(R.id.tvVideo) as TextView
            ivImage.visibility = View.GONE
            tvErrorMsg.visibility = View.VISIBLE
            tvErrorMsg.text = it!!.message


        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
            dialog.show()

        dialog.setOnCancelListener {

        }
        ivClose.setOnClickListener {
            dialog.dismiss()
        }
    }


    /*   private fun locationCheck() {
           val gpsTracker = GPSTracker(this)
           val canGetLocation = gpsTracker.canGetLocation()
           if (!canGetLocation) {
               showToast(getString(R.string.no_location_accessed))
               return
           }
           if (mMechanicLatitude != null && mMechanicLatitude != null) {
               val mechanicAddress =
                   gpsTracker.getAddress(
                       this, mMechanicLatitude!!.toDouble(), mMechanicLongitude!!.toDouble()
                   )
               if (mechanicAddress?.state?.equals("Assam", true)!!
                   || mechanicAddress.state?.equals("Jammu & Kashmir", true)!!
                   || mechanicAddress.state == null
               )
                   scanningUnderLocation(true)
               else {
                   val latitude = gpsTracker.latitude
                   val longitude = gpsTracker.longitude
                   presenter?.checkLocationUnderRadious(
                       longitude,
                       latitude,
                       mMechanicLongitude,
                       mMechanicLatitude
                   )
               }
           } else
               showToast(getString(R.string.something_wrong))
       }*/
}