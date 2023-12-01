package com.tfl.vguardrishta.ui.components.vguard.fragment.retailerProfile

import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.AdapterView
import com.bumptech.glide.Glide
import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.custom.CheckboxSpinnerAdapter
import com.tfl.vguardrishta.custom.CustomSpinner
import com.tfl.vguardrishta.extensions.launchActivity
import com.tfl.vguardrishta.extensions.onTextChanged
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.*
import com.tfl.vguardrishta.remote.ApiService
import com.tfl.vguardrishta.ui.base.BaseFragment
import com.tfl.vguardrishta.ui.components.vguard.fragment.newuser.SearchableBaseAdapter
import com.tfl.vguardrishta.ui.components.vguard.home.RishtaHomeActivity
import com.tfl.vguardrishta.ui.components.vguard.updateRetailerProfile.UpdateRetailerProfileActivity
import com.tfl.vguardrishta.utils.*
import io.paperdb.Paper
import kotlinx.android.synthetic.main.fragment_retailer_user_profile_2.*
import javax.inject.Inject


class RetailerUserProfileFragment :
    BaseFragment<RetailerUserProfileContract.View, RetailerUserProfileContract.Presenter>(),
    RetailerUserProfileContract.View, View.OnClickListener {

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
    lateinit var retUserProfilePresenter: RetailerUserProfilePresenter

    lateinit var vguardRishtaUser: VguardRishtaUser
    private lateinit var progress: Progress

    override fun initPresenter(): RetailerUserProfileContract.Presenter {
        return retUserProfilePresenter
    }

    override fun injectDependencies() {
        (activity?.application as App).applicationComponent.inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_retailer_user_profile_2
    }

    override fun initUI() {
        progress = Progress(context, R.string.please_wait)
        (activity as RishtaHomeActivity).updateCustomToolbar(
            Constants.PROFILE,
            getString(R.string.view_profile),
            ""
        )
        this.myCheckboxSpinnerAdapter = CheckboxSpinnerAdapter(context!!, 0,
            CacheUtils.getFirstRetailerCategoryDealIn(context!!))
        setCitySpinner(CacheUtils.getFirstCity(context!!))
        setDistrictSpinner(CacheUtils.getFirstDistrict(context!!))
        setStatesSpinner(CacheUtils.getFirstState(context!!))
        btnReferralCode.setOnClickListener(this)
        setListeners()
        setCurrentPincodeListeners()
        setPermPincodeListeners()
      //  retUserProfilePresenter.getBanks();
       // retUserProfilePresenter.getRetailerCategoryDealIn()
    }


    private fun setListeners() {
        llDOB.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
        btnEditProfile.setOnClickListener(this)


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
                        retUserProfilePresenter.getPincodeList(etCurrentPinCode.text.toString())
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
            retUserProfilePresenter.getPermStateDistCitiesByPincodeId(any.pinCodeId.toString())
            permPinCodeId = any.pinCodeId.toString()
            etPinCode.clearFocus()
        }

        etPinCode.onTextChanged {
            if (!etPinCode.text.isNullOrEmpty() && etPinCode.length() > 3) {
                retUserProfilePresenter.getPermPincodeList(etPinCode.text.toString())
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
            retUserProfilePresenter.getStateDistCitiesByPincodeId(any.pinCodeId.toString())
            currPinCodeId = any.pinCodeId.toString()
            etCurrentPinCode.clearFocus()
        }

        etCurrentPinCode.onTextChanged {
            if (!etCurrentPinCode.text.isNullOrEmpty() && etCurrentPinCode.length() > 3) {
                retUserProfilePresenter.getPincodeList(etCurrentPinCode.text.toString())
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


    override fun onResume() {
        super.onResume()
        retUserProfilePresenter.getProfile()
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
                context!!,
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
                context!!,
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
                context!!,
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
                context!!,
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
                context!!,
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
                context!!,
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
                        etCity.setText(newRishtaUser.otherCity)
                    }
                    if (cityMaster.id != -2L) {
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
            retUserProfilePresenter.getStateDistCitiesByPincodeId(pincodeDetails.pinCodeId.toString())
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
            retUserProfilePresenter.getPermStateDistCitiesByPincodeId(pincodeDetails.pinCodeId.toString())
            etPinCode.dismissDropDown()
        }
    }

    override fun showMsgDialog(message: String) {
        AppUtils.showErrorDialog(layoutInflater, context!!, message)
    }


    override fun showBanks(arrayList: ArrayList<BankDetail>) {
        spBanks?.adapter =
            CustomSpinner(
                context!!,
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
            AppUtils.getPosition(context!!, R.array.select_gender, rishtaUser.gender?.trim());
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
        etState.setText(rishtaUser.state)
        etDistrict.setText(rishtaUser.dist)
        etCity.setText(rishtaUser.city)
        etPinCodePerm.setText(rishtaUser.pinCode)
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
        if (rishtaUser.maritalStatusId != null) {
            spMaritalStatus.setSelection(rishtaUser.maritalStatusId!!.toInt())
        }
        etMaritalStatus.setText(rishtaUser.maritalStatus)
        etAnnualBusiness.setText(rishtaUser.annualBusinessPotential)

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
                FileUtils.zoomFileImage(context!!, selfie)
            } else
                FileUtils.zoomImage(context!!, selfieUrl!!)
        }




        etGstNo.setText(rishtaUser.gstNo)
        etGstYesNo.setText(rishtaUser.gstYesNo)

        val gstPicUrl = ApiService.gstPicUrl + rishtaUser.gstPic
        Glide.with(this).load(gstPicUrl)
            .placeholder(R.drawable.no_image).into(ivGstPic)
        ivGstPic.setOnClickListener {
            FileUtils.zoomImage(context!!, gstPicUrl)
        }
        etAccountNumber.setText(rishtaUser.bankDetail?.bankAccNo)
        etBankName.setText(rishtaUser.bankDetail?.bankNameAndBranch)
        etAccountHolderName.setText(rishtaUser.bankDetail?.bankAccHolderName)
        etIfscCode.setText(rishtaUser.bankDetail?.bankIfsc)

        val checkPhoto = AppUtils.getChequeUrl() + rishtaUser.bankDetail?.checkPhoto
        Glide.with(this).load(checkPhoto)
            .placeholder(R.drawable.no_image).into(ivChequePhoto)
        ivChequePhoto.setOnClickListener {
            FileUtils.zoomImage(context!!, checkPhoto!!)
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
        this.myCheckboxSpinnerAdapter = CheckboxSpinnerAdapter(context!!, 0,
            it)
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
            R.id.btnReferralCode -> {
//                activity?.launchActivity<ReferralCodeActivity> { }

                AppUtils.showErrorDialog(layoutInflater, context!!, getString(R.string.coming_soon))

            }


            R.id.btnEditProfile -> {
                activity?.launchActivity<UpdateRetailerProfileActivity> { }
            }
        }
    }


}