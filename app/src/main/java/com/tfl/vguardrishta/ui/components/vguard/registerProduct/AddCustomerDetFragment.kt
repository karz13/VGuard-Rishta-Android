package com.tfl.vguardrishta.ui.components.vguard.registerProduct

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.custom.CustomSpinner
import com.tfl.vguardrishta.domain.RegisterProductUseCase
import com.tfl.vguardrishta.extensions.onTextChanged
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.*
import com.tfl.vguardrishta.ui.base.BaseFragment
import com.tfl.vguardrishta.ui.components.vguard.fragment.newuser.SearchableBaseAdapter
import com.tfl.vguardrishta.ui.components.vguard.home.RishtaHomeActivity
import com.tfl.vguardrishta.ui.components.vguard.productRegisteration.ProductRegistrationActivity
import com.tfl.vguardrishta.ui.components.vguard.scanCode.ScanCodeActivity
import com.tfl.vguardrishta.utils.*
import kotlinx.android.synthetic.main.activity_re_update_kyc_and_profile.*
import kotlinx.android.synthetic.main.fragment_add_customer_det.*
import kotlinx.android.synthetic.main.fragment_add_customer_det.etContactNumber
import kotlinx.android.synthetic.main.fragment_add_customer_det.etEmail
import kotlinx.android.synthetic.main.fragment_add_customer_det.etLandmark
import kotlinx.android.synthetic.main.fragment_add_customer_det.etName
import kotlinx.android.synthetic.main.fragment_add_customer_det.etPinCode
import kotlinx.android.synthetic.main.fragment_add_customer_det.ivPincode
import java.util.*
import javax.inject.Inject


class AddCustomerDetFragment :
    BaseFragment<RegisterProductContract.View, RegisterProductContract.Presenter>(),
    RegisterProductContract.View, ActivityFinishListener {

    lateinit var gpsTracker: GPSTracker
    private lateinit var tempCd: CustomerDetailsRegistration
    var stateList = arrayListOf<StateMaster>()
    var distList = arrayListOf<DistrictMaster>()
    var dealerDistList = arrayListOf<DistrictMaster>()
    private lateinit var progress: Progress
    private lateinit var searchableBaseAdapter: SearchableBaseAdapter
    private lateinit var searchableDealerBaseAdapter: SearchableBaseAdapter

    @Inject
    lateinit var registerProductPresenter: RegisterProductPresenter
    lateinit var registerProductAdapter: RegisterProductAdapter
    //  private var pinCodeDetails: PincodeDetails? = null

    override fun initPresenter(): RegisterProductContract.Presenter {
        return registerProductPresenter
    }

    override fun injectDependencies() {
        if (activity is RegisterProductActivity)
            (activity as RegisterProductActivity).getApplicationComponent().inject(this)
        else {
            CacheUtils.clearCustomerDetReg()
            (activity as ProductRegistrationActivity).getApplicationComponent().inject(this)
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_add_customer_det
    }

    override fun setPinCodeList(it: List<PincodeDetails>, isDealer: Boolean) {
        if (isDealer) {
            searchableDealerBaseAdapter.mList = it
            searchableDealerBaseAdapter.tempList = it
            searchableDealerBaseAdapter.notifyDataSetChanged()
            etDealerPinCode.showDropDown()
//        etPinCode.requestFocus()
            if (it.size == 1) {
                val pincodeDetails = it[0]
                registerProductPresenter.getCRMStateDistrictByPincode(pincodeDetails.pinCode.toString(), true)
                etDealerPinCode.dismissDropDown()
            }
        } else {
            searchableBaseAdapter.mList = it
            searchableBaseAdapter.tempList = it
            searchableBaseAdapter.notifyDataSetChanged()
            etPinCode.showDropDown()
//        etPinCode.requestFocus()
            if (it.size == 1) {
                val pincodeDetails = it[0]
                registerProductPresenter.getCRMStateDistrictByPincode(pincodeDetails.pinCode.toString(), false)
                etPinCode.dismissDropDown()
            }
        }
    }

    override fun initUI() {
        progress = Progress(context!!, R.string.please_wait)
        setBtnName()
        btnSubmit.setOnClickListener {
            //validateMobileText()
            val email = etEmail.text.toString()
            if(email.isNullOrEmpty()){
                showEmailDialog(this.layoutInflater, context!!)
            }
            else{
             getInputs()
            }

        }
        btnGetCustDetail.setOnClickListener {
            if (etContactNumber.text.toString().isNotEmpty()) {
                registerProductPresenter.getCustDetByMobile(etContactNumber.text.toString().trim())
            } else {
                showToast(getString(R.string.enter_contact_no))
            }
        }

/*

        if (!isContactMandatory()) {
            tilContactNo.hint = getString(R.string.contact_number_optional)
        }

        if (!isCustomerNameMandatory()) {
            tilName.hint = getString(R.string.name)
        }

        if (isPincodeMandatory()) {
            tilPinCode.hint = getString(R.string.pincode)
        }
*/
        // registerProductPresenter.getStatesFromCrmApi()
        setPinCodeListeners()
        setDealerPinCodeListeners()
        setDealerDetails()
    }



    private fun showEmailDialog(layoutInflater: LayoutInflater, context: Context) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dilog_email_msg, null)
        builder.setView(dialogView)
        val dialog = builder.create()
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 55)
        dialog.window!!.setBackgroundDrawable(inset)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val ivClose = dialogView.findViewById(R.id.ivClose) as ImageView
        val tvErrorMsg = dialogView.findViewById(R.id.tvErrorMsg) as TextView
        val email_ok = dialogView.findViewById(R.id.email_ok) as Button

         tvErrorMsg.setText(getString(R.string.email_mandatory2))
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        dialog.setOnCancelListener {

        }
        ivClose.setOnClickListener {
            dialog.dismiss()
            getInputs()
        }
        email_ok.setOnClickListener {
            dialog.dismiss()
            etEmail.error = "Enter email"
            scrollView.scrollTo(0,scrollView.top)
        }


    }

    private fun setDealerPinCodeListeners() {
        searchableDealerBaseAdapter = SearchableBaseAdapter()

        etDealerPinCode.setAdapter(searchableDealerBaseAdapter)

        etDealerPinCode.setOnItemClickListener { _, _, position, _ ->
            val any = searchableDealerBaseAdapter.mList[position] as PincodeDetails
            etDealerPinCode.setText(any.toString())
            etDealerPinCode.tag = any
            //    this.pinCodeDetails = any
            etDealerPinCode.setSelection(any.toString().length)
            registerProductPresenter.getCRMStateDistrictByPincode(any.toString(), true)
            etDealerPinCode.clearFocus()

        }

        etDealerPinCode.onTextChanged {
            if (!etDealerPinCode.text.isNullOrEmpty() && etDealerPinCode.length() > 4) {
                registerProductPresenter.getPincodeList(etDealerPinCode.text.toString(), true)

            }
        }

        ivDealerPincode.setOnClickListener {
            val tempList = searchableBaseAdapter.tempList
            searchableBaseAdapter.mList = tempList
            etDealerPinCode.showDropDown()
        }
    }

    private fun setPinCodeListeners() {
        searchableBaseAdapter = SearchableBaseAdapter()

        etPinCode.setAdapter(searchableBaseAdapter)

        etPinCode.setOnItemClickListener { _, _, position, _ ->
            val any = searchableBaseAdapter.mList[position] as PincodeDetails
            etPinCode.setText(any.toString())
            etPinCode.tag = any
            //    this.pinCodeDetails = any
            etPinCode.setSelection(any.toString().length)
            registerProductPresenter.getCRMStateDistrictByPincode(any.toString(), false)
            etPinCode.clearFocus()

        }

        etPinCode.onTextChanged {
            if (!etPinCode.text.isNullOrEmpty() && etPinCode.length() > 4) {
                registerProductPresenter.getPincodeList(etPinCode.text.toString(), false)
            }
        }

        ivPincode.setOnClickListener {
            val tempList = searchableBaseAdapter.tempList
            searchableBaseAdapter.mList = tempList
            etPinCode.showDropDown()
        }
    }

    private fun setBtnName() {
       // if (activity is ProductRegistrationActivity) {
            btnSubmit.setText("Next")
        //}
    }

    override fun setCustDetails(cd: CustomerDetailsRegistration) {
        this.tempCd = cd
        etName.setText(cd.name)
        etEmail.setText(cd.email)
        etAltContactNumber.setText(cd.alternateNo)
        etAddress.setText(cd.currAdd)
        etLandmark.setText(cd.landmark)
        etDistrict.setText(cd.district)
        etState.setText(cd.state)
        etPinCode.setText(cd.pinCode)
        // setStateToSpinner(cd.state, spState)
        //  setDistToSpinner(cd.district, spDist)
        etCustCity.setText(cd.city)
        etDealerAddress.setText(cd.dealerAdd)
        if (activity is ProductRegistrationActivity) {
            setDealerCategory(cd.dealerCategory, spDealerCategory)
        }
        // setStateToSpinner(cd.dealerState, spDealerState)
        // setDistToSpinner(cd.dealerDist, spDealerDist)
        etDealerCity.setText(cd.dealerCity)

    }

    private fun setDealerCategory(dealerCategory: String?, spinner: Spinner) {
        if (!dealerCategory.isNullOrEmpty()) {
            val index = resources.getStringArray(R.array.select_category).indexOf(dealerCategory)
            spinner.setSelection(index)
        }
    }

    private fun setDealerDetails() {
        val rishtaUser = CacheUtils.getRishtaUser()
        etDealerName.setText(rishtaUser.name)
        etDealerContactNo.setText(rishtaUser.contactNo)
        etDealerPinCode.setText(rishtaUser.currPinCode)
    }

    private fun setStateToSpinner(stateName: String?, spinner: Spinner) {
        if (!stateList.isNullOrEmpty()) {
            val stateMaster = stateList.find { it.stateName == stateName }
            val index = stateList.indexOf(stateMaster)
            spinner.setSelection(index)
        }
    }

    private fun setDistToSpinner(distName: String?, spinner: Spinner) {
        /*  if (spinner.id == spDealerDist.id) {
              if (!dealerDistList.isNullOrEmpty()) {
                  val stateMaster = dealerDistList.find { it.districtName == distName }
                  val index = dealerDistList.indexOf(stateMaster)
                  spinner.setSelection(index)
              }
          } else {
              if (!distList.isNullOrEmpty()) {
                  val stateMaster = distList.find { it.districtName == distName }
                  val index = distList.indexOf(stateMaster)
                  spinner.setSelection(index)
              }
          }*/
    }
    private fun validateMobileText() {
     val contactNumber  =  etContactNumber.text.toString()
        if (contactNumber.isNullOrEmpty() /*&& isContactMandatory()*/) {
            showToast(getString(R.string.enter_contact_no))
            return
        }

        if (!contactNumber.isNullOrEmpty() && !AppUtils.isValidMobileNo(contactNumber!!)) {
            showToast(getString(R.string.enter_valid_mobileNo))
            return
        }
       /* val rishtaUser = CacheUtils.getRishtaUser()
        if (contactNumber==rishtaUser.contactNo ) {
            showToast(getString(R.string.mobile_validation1))
            return
        }*/
        var dealerCategory=""
        if (spDealerCategory.selectedItemPosition != 0) {
            dealerCategory = spDealerCategory.selectedItem as String
        } else {
            showToast(getString(R.string.select_dealer_category))
            return
        }
        registerProductPresenter.validateMobile(contactNumber,dealerCategory)
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

    private fun getInputs() {
        val cdr = CacheUtils.getCustomerDetReg()
        cdr.contactNo = etContactNumber.text.toString()
        cdr.name = etName.text.toString()
        cdr.email = etEmail.text.toString()
        cdr.alternateNo = etAltContactNumber.text.toString()
        cdr.currAdd = etAddress.text.toString()
        cdr.landmark = etLandmark.text.toString()
        cdr.city = etCustCity.text.toString()
        cdr.pinCode = etPinCode.text.toString()
        cdr.dealerName = etDealerName.text.toString()
        cdr.dealerAdd = etDealerAddress.text.toString()
        cdr.dealerCity = etDealerCity.text.toString()
        cdr.dealerPinCode = etDealerPinCode.text.toString()
        cdr.dealerNumber = etDealerContactNo.text.toString()
        cdr.dealerState = etDealerState.text.toString()
        cdr.dealerDist = etDealerDistrict.text.toString()
        cdr.state = etState.text.toString()
        cdr.district = etDistrict.text.toString()
        cdr.selectedProd = CacheUtils.getRetSelectedProdForScan()

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


        if (cdr.contactNo.isNullOrEmpty() /*&& isContactMandatory()*/) {
            showToast(getString(R.string.enter_contact_no))
            return
        }

        if (!cdr.contactNo.isNullOrEmpty() && !AppUtils.isValidMobileNo(cdr.contactNo!!)) {
            showToast(getString(R.string.enter_valid_mobileNo))
            return
        }
        val rishtaUser = CacheUtils.getRishtaUser()
        if(rishtaUser.roleId!="2") {
            if (cdr.contactNo == rishtaUser.contactNo) {
                showToast(getString(R.string.mobile_validation1))
                return
            }
        }

        if (cdr.name.isNullOrEmpty() /*&& isCustomerNameMandatory()*/) {
            showToast(getString(R.string.enter_name))
            return
        }

        if (!cdr.email.isNullOrEmpty() && !AppUtils.isValidEmailAddress(cdr.email!!)) {
            showToast(getString(R.string.enter_valid_mail))
            return
        }


        if (!cdr.alternateNo.isNullOrEmpty() && !AppUtils.isValidMobileNo(cdr.alternateNo!!)) {
            showToast(getString(R.string.enter_valid_alternate_contact_number))
            return
        }

        if (cdr.currAdd.isNullOrEmpty()) {
            showToast(getString(R.string.enter_customer_address))
            return
        }

        /*   if (spState.selectedItemPosition != 0) {
               cdr.state = (spState.selectedItem as StateMaster).stateName
           } else {
               showToast(getString(R.string.select_state))
               return
           }
           if (spDist.selectedItemPosition != 0) {
               cdr.district = (spDist.selectedItem as DistrictMaster).districtName
           } else {
               showToast(getString(R.string.select_district))
               return
           }*/


        if (/*isPincodeMandatory() &&*/ cdr.pinCode.isNullOrEmpty()) {
            showToast(getString(R.string.please_enter_pincode))
            return
        }

        if (!cdr.pinCode.isNullOrEmpty() && !AppUtils.isValidPinCode(cdr.pinCode!!)) {
            showToast(getString(R.string.enter_valid_pincode))
            return
        }


        /* if (spDealerState.selectedItemPosition != 0) {
             cdr.dealerState = (spDealerState.selectedItem as StateMaster).stateName
         } else {
             showToast(getString(R.string.select_dealer_state))
             return
         } */
        if (cdr.state.isNullOrEmpty()) {
            showToast(getString(R.string.please_select_state))
            return
        }
        if (cdr.district.isNullOrEmpty()) {
            showToast(getString(R.string.please_select_dist))
            return
        }
        if (cdr.city.isNullOrEmpty()) {
            showToast(getString(R.string.enter_customer_city))
            return
        }
//        if (activity is ProductRegistrationActivity) {
//
//            if (spDealerCategory.selectedItemPosition != 0) {
//                cdr.dealerCategory = spDealerCategory.selectedItem as String
//            } else {
//                showToast(getString(R.string.select_dealer_category))
//                return
//            }
//        }
        if (cdr.dealerPinCode.isNullOrEmpty()) {
            showToast(getString(R.string.please_enter_pincode))
            return
        }
        if (!cdr.dealerPinCode.isNullOrEmpty() && !AppUtils.isValidPinCode(cdr.dealerPinCode!!)) {
            showToast(getString(R.string.enter_valid_dealer_pincode))
            return
        }

        if (cdr.dealerState.isNullOrEmpty()) {
            showToast(getString(R.string.select_dealer_state))
            return
        }
        if (cdr.dealerDist.isNullOrEmpty()) {
            showToast(getString(R.string.select_dealer_district))
            return
        }
        /*  if (spDealerDist.selectedItemPosition != 0) {
              cdr.dealerDist = (spDealerDist.selectedItem as DistrictMaster).districtName
          } else {
              showToast(getString(R.string.select_dealer_district))
              return
          }*/

        if (cdr.dealerCity.isNullOrEmpty()) {
            showToast(getString(R.string.enter_dealer_city))
            return
        }

        if (!cdr.dealerNumber.isNullOrEmpty() && !AppUtils.isValidMobileNo(cdr.dealerNumber!!)) {
            showToast(getString(R.string.enter_valid_dealer_contact_number))
            return
        }
        //if (CacheUtils.getRishtaUser().roleId == "2") {
            CacheUtils.setCustomerDetReg(cdr)
        val otp = OTP();
        otp.mobile=etContactNumber.text.toString().trim()
        otp.eventType="Warranty"
        registerProductPresenter.productOTP(otp)
//        } else
//            registerProductPresenter.sendCustomerData(cdr, false)
    }



    override fun proceedToNextPage(){

        (activity as RegisterProductActivity).showRegisterProductFragment()
    }

    private fun isCustomerNameMandatory(): Boolean {
        val rsp = CacheUtils.getRetSelectedProdForScan()
        if ((rsp.productCode == Constants.RetProdCategory.one_INVERTER_BATTERIES
                    || rsp.productCode == Constants.RetProdCategory.two_WATER_HEATER ||
                    rsp.productCode == Constants.RetProdCategory.three_FAN)
        ) {
            return true
        }
        return false
    }

    private fun isContactMandatory(): Boolean {
        val rsp = CacheUtils.getRetSelectedProdForScan()
        if ((rsp.productCode == Constants.RetProdCategory.one_INVERTER_BATTERIES
                    || rsp.productCode == Constants.RetProdCategory.two_WATER_HEATER
                    || rsp.productCode == Constants.RetProdCategory.three_FAN)
        ) {
            return true
        }
        return false
    }

    private fun isPincodeMandatory(): Boolean {
        val rsp = CacheUtils.getRetSelectedProdForScan()
        if ((rsp.productCode == Constants.RetProdCategory.one_INVERTER_BATTERIES)) {
            return true
        }
        return false
    }

    override fun showErrorDialog(errorMsg: String?) {
        AppUtils.showErrorDialog(
            layoutInflater,
            context!!,
            errorMsg ?: getString(R.string.something_wrong)
        )
    }

    override fun processValidateMobile(validation: MobileValidation) {
        if(validation.code=="1"){
        AppUtils.showErrorDialog(
            layoutInflater,
            context!!,
            validation.message ?: getString(R.string.something_wrong)
        )}
        else {
            (activity as RegisterProductActivity).showRegisterProductFragment()
        }
    }

    override fun showErrorDialogWithFinish(errorMsg: String) {
        AppUtils.showErrorDialogWithFinish(layoutInflater, context!!, errorMsg, this)
    }

    override fun setStatesSpinner(arrayList: ArrayList<StateMaster>) {
        /* this.stateList = arrayList
         spState?.adapter = CustomSpinner(
             context!!,
             android.R.layout.simple_list_item_1,
             arrayList.toTypedArray(),
             Constants.STATES
         )

         spDealerState?.adapter = CustomSpinner(
             context!!,
             android.R.layout.simple_list_item_1,
             arrayList.toTypedArray(),
             Constants.STATES
         )

         spState.onItemSelectedListener = object : SpinnerUtils() {
             override fun onItemSelected(
                 parent: AdapterView<*>?,
                 view: View?,
                 position: Int,
                 id: Long
             ) {
                 if (position != 0) {
                     val state = parent?.selectedItem as StateMaster
                     registerProductPresenter.getDistrictsFromCrmApi(
                         state.stateName!!,
                         "custState"
                     )
                 }
             }
         }

         spDealerState.onItemSelectedListener = object : SpinnerUtils() {
             override fun onItemSelected(
                 parent: AdapterView<*>?,
                 view: View?,
                 position: Int,
                 id: Long
             ) {
                 if (position != 0) {
                     val state = parent?.selectedItem as StateMaster
                     registerProductPresenter.getDistrictsFromCrmApi(
                         state.stateName!!,
                         "dealerState"
                     )
                 }
             }
         }*/

    }

    override fun setDealerDistrictSpinner(arrayList: ArrayList<DistrictMaster>) {
        /*   this.dealerDistList = arrayList
           spDealerDist?.adapter =
               CustomSpinner(
                   context!!,
                   android.R.layout.simple_list_item_1,
                   arrayList.toTypedArray(),
                   Constants.DISTRICT
               )
           if (this::tempCd.isInitialized) {
               setDistToSpinner(tempCd.dealerDist, spDealerDist)
           }
           spDealerDist.onItemSelectedListener = object : SpinnerUtils() {
               override fun onItemSelected(
                   parent: AdapterView<*>?,
                   view: View?,
                   position: Int,
                   id: Long
               ) {
               }
           }*/
    }

    override fun setStateDistrictAndCity(it: Triple<String, String, String>, isDealer: Boolean) {
        if (isDealer) {
            etDealerState.setText(it?.first)
            etDealerDistrict.setText(it?.second)
            etDealerCity.setText(it?.second)
            etAddress.setText(it?.second)
        } else {
            etState.setText(it?.first)
            etDistrict.setText(it?.second)
            etCustCity.setText(it?.second)
            etAddress.setText(it?.second)
        }
    }

    override fun setDistrictSpinner(arrayList: ArrayList<DistrictMaster>) {
        this.distList = arrayList
        spDist?.adapter =
            CustomSpinner(
                context!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.DISTRICT
            )
        if (this::tempCd.isInitialized) {
            setDistToSpinner(tempCd.district, spDist)
        }

//        spDist.onItemSelectedListener = object : SpinnerUtils() {
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//            }
//        }


    }

    override fun showCouponPoints(cresp: CouponResponse) {
        val builder = AlertDialog.Builder(context!!)
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
        tvPointsMsg.text = getString(R.string.points)
        val tvCouponPoints = dialogView.findViewById(R.id.tvCouponPoints) as TextView
        val ivGiftImage = dialogView.findViewById(R.id.ivGiftWrap) as ImageView
        val ivClose = dialogView.findViewById(R.id.ivClose) as ImageView
        val tvScanAgain = dialogView.findViewById(R.id.tvScanAgain) as TextView
        //if couponPoints = 0, schemePoints =0, -> 50 message

        val couponPoints = cresp.couponPoints
        val schemePoints = cresp.schemePoints?.toInt()

        if (couponPoints != null && couponPoints.toInt() == 0 && schemePoints != null && schemePoints == 0) {
            tvPointsOwnMsg.visibility = View.GONE
            ivGiftImage.visibility = View.GONE
            tvCouponPoints.text = getString(R.string.scan_more)
            tvPointsMsg.visibility = View.GONE
        } else if (couponPoints != null && couponPoints.toInt() == 0) {
            tvPointsOwnMsg.visibility = View.GONE
            tvCouponPoints.visibility = View.GONE
            tvPointsMsg.visibility = View.GONE
        } else {
            tvPointsOwnMsg.visibility = View.VISIBLE
            ivGiftImage.visibility = View.VISIBLE
            tvCouponPoints.text = couponPoints
        }

        if (schemePoints != null && schemePoints > 0) {// you won hide
            tvPointsOwnMsg.visibility = View.GONE
            val tvSchemePoints = dialogView.findViewById(R.id.tvSchemePoints) as TextView
            tvSchemePoints.visibility = View.VISIBLE
            tvSchemePoints.text = getString(R.string.earned_scheme_points, schemePoints.toString(), schemePoints.toString())
        }
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
        if (CacheUtils.getRishtaUser().roleId == "2") {
            ivClose.visibility = View.VISIBLE
            tvScanAgain.visibility = View.VISIBLE
            ivClose.setOnClickListener {
                finishView()
                val intent1 = Intent(activity, RishtaHomeActivity::class.java)
                intent1.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent1)
            }
            tvScanAgain.setOnClickListener {
                finishView()
                val intent1 = Intent(activity, ScanCodeActivity::class.java)
                intent1.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent1)
            }
        } else
            dialog.setOnCancelListener {
                finishView()
            }

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

    override fun finishView() {
        activity?.finish()
    }
}