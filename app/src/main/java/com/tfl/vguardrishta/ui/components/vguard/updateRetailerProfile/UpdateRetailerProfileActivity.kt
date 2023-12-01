package com.tfl.vguardrishta.ui.components.vguard.updateRetailerProfile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.desmond.squarecamera.CameraActivity
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.custom.CheckboxSpinnerAdapter
import com.tfl.vguardrishta.custom.CustomSpinner
import com.tfl.vguardrishta.extensions.onTextChanged
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.*
import com.tfl.vguardrishta.remote.ApiService
import com.tfl.vguardrishta.ui.base.BaseActivity
import com.tfl.vguardrishta.ui.components.vguard.fragment.newuser.SearchableBaseAdapter
import com.tfl.vguardrishta.utils.*
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_update_retailer_profile.*
import kotlinx.android.synthetic.main.vguard_toolbar.*
import java.io.File
import javax.inject.Inject


class UpdateRetailerProfileActivity :
    BaseActivity<UpdateRetailerProfileContract.View, UpdateRetailerProfileContract.Presenter>(),
    UpdateRetailerProfileContract.View, View.OnClickListener {

    private lateinit var myCheckboxSpinnerAdapter: CheckboxSpinnerAdapter
    private var tempCurrentPincode: String? = ""
    private var currPinCodeId: String = ""
    private var currPinCodeDetails: PincodeDetails? = null

    private var tempCurrStateId: String? = ""
    private var tempCurrDistId: String? = ""
    private var tempCurrCityId: String? = ""

    private var tempPermPincode: String? = ""
    private var permPinCodeId: String = ""
    private var permPinCodeDetails: PincodeDetails? = null

    private var permStateId: String? = ""
    private var permDistId: String? = ""
    private var permCityId: String? = ""

    private lateinit var searchableCurrentPinAdapter: SearchableBaseAdapter
    private lateinit var searchablePermPinAdapter: SearchableBaseAdapter

    @Inject
    lateinit var retProfilePresenterUpdate: UpdateRetailerProfilePresenter

    lateinit var vguardRishtaUser: VguardRishtaUser
    private lateinit var progress: Progress

    override fun initPresenter(): UpdateRetailerProfileContract.Presenter {
        return retProfilePresenterUpdate
    }

    override fun injectDependencies() {
        getApplicationComponent().inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_update_retailer_profile
    }

    override fun initUI() {
        setSupportActionBar(toolbar_main)
        customToolbar.updateToolbar("", getString(R.string.edit_profile), "")
        progress = Progress(this, R.string.please_wait)
        this.myCheckboxSpinnerAdapter = CheckboxSpinnerAdapter(this, 0,
            CacheUtils.getFirstRetailerCategoryDealIn(this))
        setCitySpinner(CacheUtils.getFirstCity(this))
        setDistrictSpinner(CacheUtils.getFirstDistrict(this))
        setStatesSpinner(CacheUtils.getFirstState(this))
        setListeners()
        setCurrentPincodeListeners()
        setPermPincodeListeners()
        retProfilePresenterUpdate.getBanks();
        retProfilePresenterUpdate.getRetailerCategoryDealIn()
        ivBack.setOnClickListener {
            onBackPressed()
        }
        retProfilePresenterUpdate.getProfile()
    }

    private fun setListeners() {
        llDOB.setOnClickListener(this)
        addMore2.setOnClickListener(this)
        addMore3.setOnClickListener(this)
        addMore4.setOnClickListener(this)
        addMore5.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
        llCheckPhoto.setOnClickListener(this)
        spAlreadyEnrolledLoyalty.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    if (position == 1) {
                        llOtherSchemeEnrolledDetails.visibility = View.VISIBLE
                    } else {
                        resetOtherSchemeDetail()
                    }
                } else {
                    resetOtherSchemeDetail()
                }
            }
        }

        spIsCurrentAddSame.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    if (position == 1) {
                        llCurrentAdd.visibility = View.VISIBLE
                        retProfilePresenterUpdate.getPincodeList(etCurrentPinCode.text.toString())
                    } else {
                        llCurrentAdd.visibility = View.GONE
                    }
                } else {
                    llCurrentAdd.visibility = View.GONE
                }
            }
        }
    }

    private fun setPermPincodeListeners() {
        searchablePermPinAdapter = SearchableBaseAdapter()

        etPinCode.setAdapter(searchablePermPinAdapter)

        etPinCode.setOnItemClickListener { _, _, position, _ ->
            val any = searchablePermPinAdapter.mList[position] as PincodeDetails
            etPinCode.setText(any.toString())
            etPinCode.tag = any
            this.permPinCodeDetails = any
            etPinCode.setSelection(any.toString().length)
            retProfilePresenterUpdate.getPermStateDistCitiesByPincodeId(any.pinCodeId.toString())
            permPinCodeId = any.pinCodeId.toString()
            etPinCode.clearFocus()
        }

        etPinCode.onTextChanged {
            if (!etPinCode.text.isNullOrEmpty() && etPinCode.length() > 3) {
                retProfilePresenterUpdate.getPermPincodeList(etPinCode.text.toString())
            }
        }

        ivPincode.setOnClickListener {
            val tempList = searchablePermPinAdapter.tempList
            searchablePermPinAdapter.mList = tempList
            etPinCode.showDropDown()
        }
    }


    private fun setCurrentPincodeListeners() {
        searchableCurrentPinAdapter = SearchableBaseAdapter()

        etCurrentPinCode.setAdapter(searchableCurrentPinAdapter)

        etCurrentPinCode.setOnItemClickListener { _, _, position, _ ->
            val any = searchableCurrentPinAdapter.mList[position] as PincodeDetails
            etCurrentPinCode.setText(any.toString())
            etCurrentPinCode.tag = any
            this.currPinCodeDetails = any
            etCurrentPinCode.setSelection(any.toString().length)
            retProfilePresenterUpdate.getStateDistCitiesByPincodeId(any.pinCodeId.toString())
            currPinCodeId = any.pinCodeId.toString()
            etCurrentPinCode.clearFocus()
        }

        etCurrentPinCode.onTextChanged {
            if (!etCurrentPinCode.text.isNullOrEmpty() && etCurrentPinCode.length() > 3) {
                retProfilePresenterUpdate.getPincodeList(etCurrentPinCode.text.toString())
            }
        }

//        etCurrentPinCode.setOnFocusChangeListener { view, focus ->
//            if (!focus) {
//                if (!etCurrentPinCode.text.isNullOrEmpty() && etCurrentPinCode.length() > 3) {
//                    updateProfilePresenter.getPincodeList(etCurrentPinCode.text.toString())
//                }
//            }
//        }

        ivCurrentPincode.setOnClickListener {
            val tempList = searchableCurrentPinAdapter.tempList
            searchableCurrentPinAdapter.mList = tempList
            etCurrentPinCode.showDropDown()
        }
    }

    private fun resetOtherSchemeDetail() {
        llOtherSchemeEnrolledDetails.visibility = View.GONE
        llOtherSchemeEnrolledDetails2.visibility = View.GONE
        llOtherSchemeEnrolledDetails3.visibility = View.GONE
        llOtherSchemeEnrolledDetails4.visibility = View.GONE
        llOtherSchemeEnrolledDetails5.visibility = View.GONE

        etOtherSchemeBrandName.setText("")
        etWhatLikedAbtOtherScheme.setText("")

        etOtherSchemeBrandName2.setText("")
        etWhatLikedAbtOtherScheme2.setText("")

        etOtherSchemeBrandName3.setText("")
        etWhatLikedAbtOtherScheme3.setText("")

        etOtherSchemeBrandName4.setText("")
        etWhatLikedAbtOtherScheme4.setText("")

        etOtherSchemeBrandName5.setText("")
        etWhatLikedAbtOtherScheme5.setText("")
    }

    override fun onResume() {
        super.onResume()
    }

    override fun showRishtaUser(it: VguardRishtaUser) {
        Paper.book().write(Constants.KEY_RISHTA_USER, it)
        vguardRishtaUser = it

        var selfie = it.kycDetails.selfie
        selfie = AppUtils.getSelfieUrl() + selfie
        Glide.with(this).load(selfie)
            .placeholder(R.drawable.ic_v_guards_user).into(ivUserImage)
        tvDisplayName.text = it.name
        tvUserCode.text = it.userCode
        tvEcard.text = getString(R.string.view_e_card)
        setInputs()
    }

    override fun setStatesSpinner(arrayList: ArrayList<StateMaster>) {
        spCurrState?.adapter =
            CustomSpinner(
                this,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.STATES
            )

        val rishtaUser = CacheUtils.getRishtaUser()

        if (tempCurrentPincode == etCurrentPinCode.text.toString() && rishtaUser.currStateId != null && rishtaUser.currStateId != "-1") {
            val stateMaster = arrayList.find { it.id == rishtaUser.currStateId!!.toLong() }
            val index = arrayList.indexOf(stateMaster)
            spCurrState.setSelection(index)
        } else if (!tempCurrStateId.isNullOrEmpty()) {
            val stateMaster = arrayList.find { it.id == tempCurrStateId!!.toLong() }
            val index = arrayList.indexOf(stateMaster)
            spCurrState.setSelection(index)
        }

        spCurrState.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
//                if (position != 0) {
//                    val stateMaster = parent?.selectedItem as StateMaster
//
//                    updateProfilePresenter.getDistrict(stateMaster.id)
//                }
            }
        }
    }

    override fun setDistrictSpinner(arrayList: ArrayList<DistrictMaster>) {
        spCurrDist?.adapter =
            CustomSpinner(
                this,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.DISTRICT
            )
        val rishtaUser = CacheUtils.getRishtaUser()
        if (tempCurrentPincode == etCurrentPinCode.text.toString() && rishtaUser.currDistId != null && rishtaUser.currDistId != "-1") {
            val stateMaster = arrayList.find { it.id == rishtaUser.currDistId!!.toLong() }
            val index = arrayList.indexOf(stateMaster)
            spCurrDist.setSelection(index)
        } else if (!tempCurrDistId.isNullOrEmpty()) {
            val distMaster = arrayList.find { it.id == tempCurrDistId!!.toLong() }
            val index = arrayList.indexOf(distMaster)
            spCurrDist.setSelection(index)
        }
        spCurrDist.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
//                if (position != 0) {
//                    val distmaster = parent?.selectedItem as DistrictMaster
//                    updateProfilePresenter.getCities(distmaster.id)
//                }
            }
        }
    }

    override fun setCitySpinner(arrayList: ArrayList<CityMaster>) {
        spCurrCity?.adapter =
            CustomSpinner(
                this,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.CITY
            )
        val rishtaUser = CacheUtils.getRishtaUser()
        if (tempCurrentPincode == etCurrentPinCode.text.toString() && rishtaUser.currCityId != null && rishtaUser.currCityId != "-1") {
            val stateMaster = arrayList.find { it.id == rishtaUser.currCityId!!.toLong() }
            val index = arrayList.indexOf(stateMaster)
            spCurrCity.setSelection(index)
        } else if (!tempCurrCityId.isNullOrEmpty()) {
            val cityMaster = arrayList.find { it.id == tempCurrCityId!!.toLong() }
            val index = arrayList.indexOf(cityMaster)
            spCurrCity.setSelection(index)
        }

        spCurrCity.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if (position != 0 && parent?.selectedItem is CityMaster) {
                    val cityMaster = parent?.selectedItem as CityMaster
                    if (cityMaster.id == -2L) {
                        val newRishtaUser = CacheUtils.getNewRishtaUser()
                        tlCurrentCity.visibility = View.VISIBLE
                        etCurrentCity.setText(newRishtaUser.otherCurrCity)

                        if (spIsCurrentAddSame.selectedItemPosition != 0) {
                            if (spIsCurrentAddSame.selectedItemPosition == 2) {
                                etCurrentCity.setText(newRishtaUser.otherCity)
                            }
                        }
                    }
                    if (cityMaster.id != -2L) {
                        tlCurrentCity.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun setPermStatesSpinner(arrayList: ArrayList<StateMaster>) {
        spState?.adapter =
            CustomSpinner(
                this,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.STATES
            )

        val rishtaUser = CacheUtils.getRishtaUser()

        if (tempPermPincode == etPinCode.text.toString()
            && rishtaUser.stateId != null && rishtaUser.stateId != "-1"
        ) {
            val stateMaster = arrayList.find { it.id == rishtaUser.stateId!!.toLong() }
            val index = arrayList.indexOf(stateMaster)
            spState.setSelection(index)
        } else if (!permStateId.isNullOrEmpty()) {
            val stateMaster = arrayList.find { it.id == permStateId!!.toLong() }
            val index = arrayList.indexOf(stateMaster)
            spState.setSelection(index)
        }
    }

    override fun setPermDistrictSpinner(arrayList: ArrayList<DistrictMaster>) {
        spDist?.adapter =
            CustomSpinner(
                this!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.DISTRICT
            )
        val rishtaUser = CacheUtils.getRishtaUser()
        if (tempPermPincode == etPinCode.text.toString()
            && rishtaUser.distId != null && rishtaUser.distId != "-1"
        ) {
            val stateMaster = arrayList.find { it.id == rishtaUser.distId!!.toLong() }
            val index = arrayList.indexOf(stateMaster)
            spDist.setSelection(index)
        } else if (!permDistId.isNullOrEmpty()) {
            val distMaster = arrayList.find { it.id == permDistId!!.toLong() }
            val index = arrayList.indexOf(distMaster)
            spDist.setSelection(index)
        }
    }

    override fun setPermCitySpinner(arrayList: ArrayList<CityMaster>) {
        spCity?.adapter =
            CustomSpinner(
                this!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.CITY
            )
        val rishtaUser = CacheUtils.getRishtaUser()
        if (tempPermPincode == etPinCode.text.toString()
            && rishtaUser.cityId != null && rishtaUser.cityId != "-1"
        ) {
            val stateMaster = arrayList.find { it.id == rishtaUser.cityId!!.toLong() }
            val index = arrayList.indexOf(stateMaster)
            spCity.setSelection(index)
        } else if (!permCityId.isNullOrEmpty()) {
            val cityMaster = arrayList.find { it.id == permCityId!!.toLong() }
            val index = arrayList.indexOf(cityMaster)
            spCity.setSelection(index)
        }

        spCity.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if (position != 0 && parent?.selectedItem is CityMaster) {
                    val cityMaster = parent?.selectedItem as CityMaster
                    if (cityMaster.id == -2L) {
                        val newRishtaUser = CacheUtils.getNewRishtaUser()
                        llCity.visibility = View.VISIBLE
                        etCity.setText(newRishtaUser.otherCity)
                    }
                    if (cityMaster.id != -2L) {
                        llCity.visibility = View.GONE
                    }
                }
            }
        }
    }


    override fun setCitiesByPinCode(it: List<CityMaster>?) {
        if (it != null && it.isNotEmpty()) {
            if (it != null && it.isNotEmpty()) {
                val cityMaster = it[0]
                tempCurrStateId = cityMaster.stateId?.toString()
                tempCurrDistId = cityMaster.districtId?.toString()
                tempCurrCityId = cityMaster.id?.toString()
            }
        }
    }


    override fun setPermCitiesByPinCode(it: List<CityMaster>?) {
        if (it != null && it.isNotEmpty()) {
            if (it != null && it.isNotEmpty()) {
                val cityMaster = it[0]
                permStateId = cityMaster.stateId?.toString()
                permDistId = cityMaster.districtId?.toString()
                permCityId = cityMaster.id?.toString()
            }
        }
    }

    override fun setPinCodeList(it: List<PincodeDetails>) {
        searchableCurrentPinAdapter.mList = it
        searchableCurrentPinAdapter.tempList = it
        searchableCurrentPinAdapter.notifyDataSetChanged()
        etCurrentPinCode.showDropDown()
//        etPinCode.requestFocus()
        if (it.size == 1) {
            val pincodeDetails = it[0]
            retProfilePresenterUpdate.getStateDistCitiesByPincodeId(pincodeDetails.pinCodeId.toString())
            etCurrentPinCode.dismissDropDown()
        }
    }

    override fun setPermPinCodeList(it: List<PincodeDetails>) {
        searchablePermPinAdapter.mList = it
        searchablePermPinAdapter.tempList = it
        searchablePermPinAdapter.notifyDataSetChanged()
        etPinCode.showDropDown()
//        etPinCode.requestFocus()
        if (it.size == 1) {
            val pincodeDetails = it[0]
            retProfilePresenterUpdate.getPermStateDistCitiesByPincodeId(pincodeDetails.pinCodeId.toString())
            etPinCode.dismissDropDown()
        }
    }

    override fun showMsgDialog(message: String) {
        AppUtils.showErrorDialog(layoutInflater, this!!, message)
    }


    override fun showBanks(arrayList: ArrayList<BankDetail>) {
        spBanks?.adapter =
            CustomSpinner(
                this!!,
                R.layout.custom_simple_spinner_item2,
                arrayList.toTypedArray(),
                Constants.BANKS
            )
        val rishtaUser = CacheUtils.getRishtaUser()

        val bd = rishtaUser.bankDetail

        if (bd?.bankId != null) {
            val stateMaster = arrayList.find { it.bankId == bd.bankId!! }
            val index = arrayList.indexOf(stateMaster)
            spBanks.setSelection(index)
        }
    }

    private fun setInputs() {
        val rishtaUser = CacheUtils.getRishtaUser()

        val selfieUrl = AppUtils.getSelfieUrl() + rishtaUser.kycDetails.selfie
        Glide.with(this).load(selfieUrl)
            .placeholder(R.drawable.ic_v_guards_user).into(ivUserImage)
        etPreferredLanguage.setText(rishtaUser.preferredLanguage)
        etReferralCode.setText(rishtaUser.referralCode)
        etNameOfReferee.setText(rishtaUser.nameOfReferee)
        etName.setText(rishtaUser.name)
        val position =
            AppUtils.getPosition(this!!, R.array.select_gender, rishtaUser.gender?.trim());
        spGender.setSelection(position)
        tvDob.setText(rishtaUser.dob)
        etContactNumber.setText(rishtaUser.contactNo)
        etFirmName.setText(rishtaUser.firmName)
        etEmail.setText(rishtaUser.emailId)
        etPermanentAddress.setText(rishtaUser.permanentAddress)
        etStreetLocality.setText(rishtaUser.streetAndLocality)
        etLandmark.setText(rishtaUser.landmark)
        etCity.setText(rishtaUser.city)
        etPinCode.setText(rishtaUser.pinCode)

        if (rishtaUser.addDiff != -1) {
            if (rishtaUser.addDiff == 0) {
                spIsCurrentAddSame.setSelection(1)
            } else {
                spIsCurrentAddSame.setSelection(2)
            }
        }

        etCurrentAddress.setText(rishtaUser.currentAddress)
        etCurrentStreetLocality.setText(rishtaUser.currStreetAndLocality)
        etCurrentLandmark.setText(rishtaUser.currLandmark)
        etCurrentPinCode.setText(rishtaUser.currPinCode)
        etAspireGift.setText(rishtaUser.aspireGift)
        if (rishtaUser.maritalStatusId != null) {
            spMaritalStatus.setSelection(rishtaUser.maritalStatusId!!.toInt())
        }
        if (rishtaUser.enrolledOtherScheme == 1) {
            spAlreadyEnrolledLoyalty.setSelection(1)
            llOtherSchemeEnrolledDetails.visibility = View.VISIBLE
            etOtherSchemeBrandName.setText(rishtaUser.otherSchemeBrand)
            etWhatLikedAbtOtherScheme.setText(rishtaUser.abtOtherSchemeLiked)

            if (!rishtaUser.otherSchemeBrand2.isNullOrEmpty()) {
                llOtherSchemeEnrolledDetails2.visibility = View.VISIBLE
                etOtherSchemeBrandName2.setText(rishtaUser.otherSchemeBrand2)
                etWhatLikedAbtOtherScheme2.setText(rishtaUser.abtOtherSchemeLiked2)

            }

            if (!rishtaUser.otherSchemeBrand3.isNullOrEmpty()) {
                llOtherSchemeEnrolledDetails3.visibility = View.VISIBLE
                etOtherSchemeBrandName3.setText(rishtaUser.otherSchemeBrand3)
                etWhatLikedAbtOtherScheme3.setText(rishtaUser.abtOtherSchemeLiked3)
            }

            if (!rishtaUser.otherSchemeBrand4.isNullOrEmpty()) {
                llOtherSchemeEnrolledDetails4.visibility = View.VISIBLE
                etOtherSchemeBrandName4.setText(rishtaUser.otherSchemeBrand4)
                etWhatLikedAbtOtherScheme4.setText(rishtaUser.abtOtherSchemeLiked4)
            }

            if (!rishtaUser.otherSchemeBrand5.isNullOrEmpty()) {
                llOtherSchemeEnrolledDetails5.visibility = View.VISIBLE
                etOtherSchemeBrandName5.setText(rishtaUser.otherSchemeBrand5)
                etWhatLikedAbtOtherScheme5.setText(rishtaUser.abtOtherSchemeLiked5)
            }
        } else if (rishtaUser.enrolledOtherScheme == 0) {
            spAlreadyEnrolledLoyalty.setSelection(2)
        }

        etAnnualBusiness.setText(rishtaUser.annualBusinessPotential)
        etIdProofType.setText(rishtaUser.kycDetails.kycIdName)

        Glide.with(this).load(selfieUrl)
            .placeholder(R.drawable.no_image).into(ivSelfie)

        ivSelfie.setOnClickListener {
            val selfie = CacheUtils.getFileUploader().getUserPhotoFile()
            if (selfie != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    ivSelfie.setImageURI(Uri.parse(selfie.toString()));
                } else {
                    ivSelfie.setImageURI(Uri.fromFile(selfie));
                }
                FileUtils.zoomFileImage(this!!, selfie)
            } else
                FileUtils.zoomImage(this!!, selfieUrl!!)
        }

        val idFrontUrl = AppUtils.getIdCardUrl() + rishtaUser.kycDetails.aadharOrVoterOrDLFront
        Glide.with(this).load(idFrontUrl)
            .placeholder(R.drawable.no_image).into(ivIdFront)
        ivIdFront.setOnClickListener {
            FileUtils.zoomImage(this!!, idFrontUrl!!)
        }

        val idBackUrl = AppUtils.getIdCardUrl() + rishtaUser.kycDetails.aadharOrVoterOrDlBack

        Glide.with(this).load(idBackUrl)
            .placeholder(R.drawable.no_image).into(ivIdBack)
        ivIdBack.setOnClickListener {
            FileUtils.zoomImage(this!!, idBackUrl!!)
        }
        etIdNoManualEntered.setText(rishtaUser.kycDetails.aadharOrVoterOrDlNo)
        etGstNo.setText(rishtaUser.gstNo)
        etGstYesNo.setText(rishtaUser.gstYesNo)

        val gstPicUrl = ApiService.gstPicUrl + rishtaUser.gstPic
        Glide.with(this).load(gstPicUrl)
            .placeholder(R.drawable.no_image).into(ivGstPic)
        ivGstPic.setOnClickListener {
            FileUtils.zoomImage(this!!, gstPicUrl)
        }
        etAccountNumber.setText(rishtaUser.bankDetail?.bankAccNo)
        etAccountHolderName.setText(rishtaUser.bankDetail?.bankAccHolderName)
        etIfscCode.setText(rishtaUser.bankDetail?.bankIfsc)

        val checkPhoto = AppUtils.getChequeUrl() + rishtaUser.bankDetail?.checkPhoto
        Glide.with(this).load(checkPhoto)
            .placeholder(R.drawable.no_image).into(ivChequePhoto)
        ivChequePhoto.setOnClickListener {
            FileUtils.zoomImage(this!!, checkPhoto!!)
        }
        if (rishtaUser.addDiff == 1) {
            llCurrentAdd.visibility = View.VISIBLE
        } else {
            llCurrentAdd.visibility = View.GONE
        }
     }

    override fun setRetailerCategoryDealIn(it: List<Category>) {
        val rishtaUser = CacheUtils.getRishtaUser()
        var split: ArrayList<String> = arrayListOf()
        if (!rishtaUser.categoryDealInID.isNullOrEmpty()) {
            if (rishtaUser.categoryDealInID!!.contains(",")) {
                split = rishtaUser.categoryDealInID!!.split(",") as ArrayList<String>
            } else {
                split.add(rishtaUser.categoryDealInID!!)
            }
        }
        for (ele in it) {
            ele.isSelected = split.contains(ele.prodCatId?.toString())
        }
        this.myCheckboxSpinnerAdapter = CheckboxSpinnerAdapter(this!!, 0,
            it)
        spCategoryDealIn.adapter = myCheckboxSpinnerAdapter
    }

    private fun getInputs() {
        val rishtaUser = VguardRishtaUser()
        val genderPos = spGender.selectedItemPosition
        val gender = spGender.selectedItem as String
        val dob = tvDob.text.toString()
        val email = etEmail.text.toString()
        var isAddDiff = -1
        if (spIsCurrentAddSame.selectedItemPosition == 1) {
            isAddDiff = 1
        } else if (spIsCurrentAddSame.selectedItemPosition == 2) {
            isAddDiff = 0
        }

        val currAddress = etCurrentAddress.text.toString()
        val streetLocality = etCurrentStreetLocality.text.toString()
        val landmark = etCurrentLandmark.text.toString()
        val pinCode = etCurrentPinCode.text.toString()

        rishtaUser.maritalStatusId = if (spMaritalStatus.selectedItemPosition == 0) {
            null
        } else {
            spMaritalStatus.selectedItemPosition
        }

        if (spAlreadyEnrolledLoyalty.selectedItemPosition != 0) {
            rishtaUser.enrolledOtherScheme = spAlreadyEnrolledLoyalty.selectedItemPosition
        }

        val otherSchemeBrand = etOtherSchemeBrandName.text.toString()
        val abtOtherSchemeLiked = etWhatLikedAbtOtherScheme.text.toString()

        val otherSchemeBrand2 = etOtherSchemeBrandName2.text.toString()
        val abtOtherSchemeLiked2 = etWhatLikedAbtOtherScheme2.text.toString()

        val otherSchemeBrand3 = etOtherSchemeBrandName3.text.toString()
        val abtOtherSchemeLiked3 = etWhatLikedAbtOtherScheme3.text.toString()

        val otherSchemeBrand4 = etOtherSchemeBrandName4.text.toString()
        val abtOtherSchemeLiked4 = etWhatLikedAbtOtherScheme4.text.toString()

        val otherSchemeBrand5 = etOtherSchemeBrandName5.text.toString()
        val abtOtherSchemeLiked5 = etWhatLikedAbtOtherScheme5.text.toString()

        val annualBusinessPotential = etAnnualBusiness.text.toString().replace(",", "")


        val kycDetails = KycDetails()
        rishtaUser.name = etName.text.toString()
        rishtaUser.firmName = etFirmName.text.toString().trim()
        rishtaUser.gender = gender
        rishtaUser.genderPos = genderPos.toString()
        rishtaUser.contactNo = etContactNumber.text.toString().trim()
        rishtaUser.dob = dob
        rishtaUser.emailId = email
        val pCityMaster = spCity.selectedItem as CityMaster
        val pDistMaster = spDist.selectedItem as DistrictMaster
        val pStateMaster = spState.selectedItem as StateMaster
        rishtaUser.permanentAddress = etPermanentAddress.text.toString().trim()
        rishtaUser.streetAndLocality = etStreetLocality.text.toString().trim()
        rishtaUser.pinCode = etPinCode.text.toString().trim()
        rishtaUser.landmark = etLandmark.text.toString().trim()
        rishtaUser.cityId = pCityMaster.id.toString()
        rishtaUser.distId = pDistMaster.id.toString()
        rishtaUser.stateId = pStateMaster.id.toString()
        if (spCity.selectedItem is CityMaster) {
            rishtaUser.cityId = pCityMaster.id.toString()
            if (pCityMaster.id != -2L) {
                rishtaUser.city = pCityMaster.cityName.toString()
                rishtaUser.otherCity = null
            }
            if (pCityMaster.id == -2L) {
                rishtaUser.city = null
                rishtaUser.otherCity = etCity.text.toString()
            }
        }

        rishtaUser.addDiff = isAddDiff
        rishtaUser.currentAddress = currAddress
        rishtaUser.currStreetAndLocality = streetLocality
        rishtaUser.currLandmark = landmark
        if (isAddDiff == 1) {
            val cityMaster = spCurrCity.selectedItem as CityMaster
            val distMaster = spCurrDist.selectedItem as DistrictMaster
            val stateMaster = spCurrState.selectedItem as StateMaster
            rishtaUser.currState = stateMaster.stateName
            rishtaUser.currStateId = stateMaster.id.toString()
            rishtaUser.currDistId = distMaster.id.toString()
            rishtaUser.currDist = distMaster.districtName
            rishtaUser.currCityId = cityMaster.id.toString()
            rishtaUser.currCity = cityMaster.cityName
            rishtaUser.currPinCode = pinCode
            if (spCurrCity.selectedItem is CityMaster) {
                rishtaUser.cityId = cityMaster.id.toString()
                if (cityMaster.id != -2L) {
                    rishtaUser.currCity = cityMaster.cityName.toString()
                    rishtaUser.otherCurrCity = null
                }
                if (cityMaster.id == -2L) {
                    rishtaUser.currCity = null
                    rishtaUser.otherCurrCity = etCurrentCity.text.toString()
                }
            }
        } else if (isAddDiff == 0) {
            rishtaUser.currStateId = CacheUtils.getRishtaUser().stateId
            rishtaUser.currDistId = CacheUtils.getRishtaUser().distId
            rishtaUser.currCityId = CacheUtils.getRishtaUser().cityId
            rishtaUser.currPinCode = pinCode
        }

        rishtaUser.otherSchemeBrand = otherSchemeBrand
        rishtaUser.abtOtherSchemeLiked = abtOtherSchemeLiked
        rishtaUser.otherSchemeBrand2 = otherSchemeBrand2
        rishtaUser.abtOtherSchemeLiked2 = abtOtherSchemeLiked2
        rishtaUser.otherSchemeBrand3 = otherSchemeBrand3
        rishtaUser.abtOtherSchemeLiked3 = abtOtherSchemeLiked3
        rishtaUser.otherSchemeBrand4 = otherSchemeBrand4
        rishtaUser.abtOtherSchemeLiked4 = abtOtherSchemeLiked4
        rishtaUser.otherSchemeBrand5 = otherSchemeBrand5
        rishtaUser.abtOtherSchemeLiked5 = abtOtherSchemeLiked5
        rishtaUser.annualBusinessPotential = annualBusinessPotential

        val listCatSelectState = myCheckboxSpinnerAdapter.listCategoryState
        var selectedCatId = ""
        var selectedCat = ""
        for (cat in listCatSelectState) {
            if (cat.isSelected) {
                selectedCatId += cat.prodCatId.toString() + ","
                selectedCat += cat.prodCatName + ","
            }
        }
        if (selectedCat.endsWith(",")) {
            selectedCat = selectedCat.substring(0, selectedCat.length - 1)
        }
        if (selectedCatId.endsWith(",")) {
            selectedCatId = selectedCatId.substring(0, selectedCatId.length - 1)
        }

        rishtaUser.categoryDealIn = selectedCat
        rishtaUser.categoryDealInID = selectedCatId
        rishtaUser.kycDetails = kycDetails

        var bankDetail = rishtaUser.bankDetail
        if (bankDetail == null) {
            bankDetail = BankDetail()
        }
        try{
        if (spAccountType.selectedItemPosition != 0) {
            bankDetail.bankAccType = spAccountType.selectedItem as String
            bankDetail.bankAccTypePos = spAccountType.selectedItemPosition.toString()
        }
        if (spBanks.selectedItem is BankDetail) {
            val bd = spBanks.selectedItem as BankDetail
            if (bd.bankId != -1) {
                bankDetail.bankNameAndBranch = bd.bankNameAndBranch
                bankDetail.bankId = bd.bankId
            }
            if (bd.bankId == -1) {
                bankDetail.bankNameAndBranch = null
            }
        }}
        catch (e : Exception){

        }

        bankDetail.bankAccNo = etAccountNumber.text.toString()
        bankDetail.bankAccHolderName = etAccountHolderName.text.toString()
        bankDetail.bankIfsc = etIfscCode.text.toString()
        bankDetail.checkPhoto = CacheUtils.getRishtaUser().bankDetail?.checkPhoto
        rishtaUser.bankDetail = bankDetail
        retProfilePresenterUpdate.validate(rishtaUser)
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
            R.id.addMore2 -> {
                llOtherSchemeEnrolledDetails2.visibility = View.VISIBLE
            }
            R.id.addMore3 -> {
                llOtherSchemeEnrolledDetails3.visibility = View.VISIBLE
            }
            R.id.addMore4 -> {
                llOtherSchemeEnrolledDetails4.visibility = View.VISIBLE
            }

            R.id.addMore5 -> {
                llOtherSchemeEnrolledDetails5.visibility = View.VISIBLE
            }

            R.id.btnSubmit -> {
                getInputs()
            }

            R.id.llCheckPhoto -> {
                selectOrCaptureImage(FileUtils.chequeBookPhoto)
            }
            R.id.llDOB -> {
                AppUtils.getDate(this, tvDob, true)
            }
        }
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

        if (requestCode == FileUtils.selfie) {
            AppUtils.checkNullAndSetText(tvSelfie, file.name, getString(R.string.file))
            Glide.with(this!!).load(photoUri).into(ivSelfie)
            FileUtils.setToFileUploader(requestCode, compressImage)
        }

        if (requestCode == FileUtils.chequeBookPhoto) {
            AppUtils.checkNullAndSetText(tvChequePhoto, file.name, getString(R.string.file))
            Glide.with(this!!).load(photoUri).into(ivChequePhoto)
            FileUtils.setToFileUploader(requestCode, compressImage)
        }
        super.onActivityResult(requestCode, resultCode, data)

    }


}