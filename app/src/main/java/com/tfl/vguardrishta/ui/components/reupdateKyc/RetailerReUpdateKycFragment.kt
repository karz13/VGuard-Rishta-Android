package com.tfl.vguardrishta.ui.components.reupdateKyc

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.desmond.squarecamera.CameraActivity
import com.tfl.vguardrishta.App
import com.tfl.vguardrishta.R
import com.tfl.vguardrishta.custom.CustomSpinner
import com.tfl.vguardrishta.extensions.hideSoftKeyBoard
import com.tfl.vguardrishta.extensions.onTextChanged
import com.tfl.vguardrishta.extensions.toast
import com.tfl.vguardrishta.models.CityMaster
import com.tfl.vguardrishta.models.DistrictMaster
import com.tfl.vguardrishta.models.PincodeDetails
import com.tfl.vguardrishta.models.StateMaster
import com.tfl.vguardrishta.ui.base.BaseFragment
import com.tfl.vguardrishta.ui.components.vguard.fragment.newuser.SearchableBaseAdapter
import com.tfl.vguardrishta.utils.*
import kotlinx.android.synthetic.main.fragment_ret_re_update_kyc_and_profile.*
import java.io.File
import java.util.*
import javax.inject.Inject

class RetailerReUpdateKycFragment :
    BaseFragment<ReUpdateKycContract.View, ReUpdateKycContract.Presenter>(),
    ReUpdateKycContract.View,
    View.OnClickListener, ActivityFinishListener {
    private var pinCodeId: String? = null
    private var tempCurrentPincode: String? = ""
    private var tempPermPincode: String? = ""

    private var currPinCodeId: String = ""
    private var pinCodeDetails: PincodeDetails? = null

    private var tempCurrStateId: String? = ""
    private var tempCurrDistId: String? = ""
    private var tempCurrCityId: String? = ""

    private var tempPermStateId: String? = ""
    private var tempPermDistId: String? = ""
    private var tempPermCityId: String? = ""


    private lateinit var searchableCurrPincodeBaseAdapter: SearchableBaseAdapter
    private lateinit var searchableBaseAdapter: SearchableBaseAdapter

    @Inject
    lateinit var reUpdateKycPresenter: ReUpdateKycPresenter

    lateinit var rejectionReasonListAdapter: RejectionReasonListAdapter

    private lateinit var progress: Progress

    override fun initPresenter() = reUpdateKycPresenter

    override fun injectDependencies() {
        (activity?.application as App).applicationComponent.inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_ret_re_update_kyc_and_profile
    }

    override fun initUI() {
        progress = Progress(context!!, R.string.please_wait)
        rejectionReasonListAdapter = RejectionReasonListAdapter()
        svUserDet.scrollTo(0, 0)
        rcvRejectionRemarks.adapter = rejectionReasonListAdapter
        hideKeyBoard()
        val currentFocus = activity?.currentFocus
        currentFocus?.hideSoftKeyBoard()
        setPermPinCodeListeners()
        setCurrentPincodeListeners()
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
                        reUpdateKycPresenter.getPincodeList(etCurrentPinCode.text.toString())
                    } else {
                        llCurrentAdd.visibility = View.GONE
                    }
                } else {
                    llCurrentAdd.visibility = View.GONE
                }
            }
        }
        spGst.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    if (position == 2) {
                        llGst.visibility = View.VISIBLE
                    } else {
                        llGst.visibility = View.GONE
                    }
                } else {
                    llGst.visibility = View.GONE
                }
            }
        }

        setCurrStatesSpinner(CacheUtils.getFirstState(activity!!))
        setCurrDistrictSpinner(CacheUtils.getFirstDistrict(activity!!))
        setCurrCitySpinner(CacheUtils.getFirstCity(activity!!))
        showUserDetails()
        setClicks()
        //reUpdateKycPresenter.getUserDetails();
    }

    private fun setClicks() {
        btnPreview.setOnClickListener(this)
        llAadharBack.setOnClickListener(this)
        llIAadharFront.setOnClickListener(this)
        llGSTPic.setOnClickListener(this)
        llIPanFront.setOnClickListener(this)
    }

    override fun showUserDetails() {
        setInputs()
        svUserDet.scrollTo(0, 0)
    }

    private fun setPermPinCodeListeners() {
        searchableBaseAdapter = SearchableBaseAdapter()

        etPinCode.setAdapter(searchableBaseAdapter)

        etPinCode.setOnItemClickListener { _, _, position, _ ->
            val any = searchableBaseAdapter.mList[position] as PincodeDetails
            etPinCode.setText(any.toString())
            etPinCode.tag = any
            this.pinCodeDetails = any
            etPinCode.setSelection(any.toString().length)
            reUpdateKycPresenter.getStateDistCitiesByPermPincodeId(any.pinCodeId.toString())
            pinCodeId = any.pinCodeId.toString()
            etPinCode.clearFocus()

        }

        etPinCode.onTextChanged {
            if (!etPinCode.text.isNullOrEmpty() && etPinCode.length() > 3) {
                reUpdateKycPresenter.getPermPincodeList(etPinCode.text.toString())
            }
        }

        ivPincode.setOnClickListener {
            val tempList = searchableBaseAdapter.tempList
            searchableBaseAdapter.mList = tempList
            etPinCode.showDropDown()
        }


    }


    private fun setCurrentPincodeListeners() {
        searchableCurrPincodeBaseAdapter = SearchableBaseAdapter()

        etCurrentPinCode.setAdapter(searchableCurrPincodeBaseAdapter)

        etCurrentPinCode.setOnItemClickListener { _, _, position, _ ->
            val any = searchableCurrPincodeBaseAdapter.mList[position] as PincodeDetails
            etCurrentPinCode.setText(any.toString())
            etCurrentPinCode.tag = any
            this.pinCodeDetails = any
            etCurrentPinCode.setSelection(any.toString().length)
            reUpdateKycPresenter.getStateDistCitiesByPincodeId(any.pinCodeId.toString())
            currPinCodeId = any.pinCodeId.toString()
            etCurrentPinCode.clearFocus()

        }

        etCurrentPinCode.onTextChanged {
            if (!etCurrentPinCode.text.isNullOrEmpty() && etCurrentPinCode.length() > 3) {
                reUpdateKycPresenter.getPincodeList(etCurrentPinCode.text.toString())
            }
        }

        ivCurrentPincode.setOnClickListener {
            val tempList = searchableCurrPincodeBaseAdapter.tempList
            searchableCurrPincodeBaseAdapter.mList = tempList
            etCurrentPinCode.showDropDown()
        }
    }

    override fun setPinCodeList(it: List<PincodeDetails>) {
        searchableCurrPincodeBaseAdapter.mList = it
        searchableCurrPincodeBaseAdapter.tempList = it
        searchableCurrPincodeBaseAdapter.notifyDataSetChanged()
        etCurrentPinCode.showDropDown()
        if (it.size == 1) {
            val pincodeDetails = it[0]
            reUpdateKycPresenter.getStateDistCitiesByPincodeId(pincodeDetails.pinCodeId.toString())
            etCurrentPinCode.dismissDropDown()
        }
    }

    override fun setPermPinCodeList(it: List<PincodeDetails>) {
        searchableBaseAdapter.mList = it
        searchableBaseAdapter.tempList = it
        searchableBaseAdapter.notifyDataSetChanged()
        etPinCode.showDropDown()
        if (it.size == 1) {
            val pincodeDetails = it[0]
            reUpdateKycPresenter.getStateDistCitiesByPermPincodeId(pincodeDetails.pinCodeId.toString())
            etPinCode.dismissDropDown()
        }
    }

    override fun setPermStatesSpinner(arrayList: ArrayList<StateMaster>) {
        spState?.adapter =
            CustomSpinner(
                activity!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.STATES
            )

        val rishtaUser = CacheUtils.getNewRishtaUser()

        if (tempPermPincode == etPinCode.text.toString() && rishtaUser.stateId != null && rishtaUser.stateId != "-1") {
            val stateMaster = arrayList.find { it.id == rishtaUser.stateId!!.toLong() }
            val index = arrayList.indexOf(stateMaster)
            spState.setSelection(index)
        } else if (!tempPermStateId.isNullOrEmpty()) {
            val sm = arrayList.find { it.id == tempPermStateId!!.toLong() }
            val index = arrayList.indexOf(sm)
            spState.setSelection(index)
        }

        spState.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
//                if (position != 0) {
//                    val stateMaster = parent?.selectedItem as StateMaster
//
//                    newUserRegPresenter.getDistrict(stateMaster.id)
//                }
            }
        }
    }

    override fun setPermDistrictSpinner(arrayList: ArrayList<DistrictMaster>) {
        spDist?.adapter =
            CustomSpinner(
                activity!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.DISTRICT
            )
        val rishtaUser = CacheUtils.getNewRishtaUser()
        if (tempPermPincode == etPinCode.text.toString() && rishtaUser.distId != null && rishtaUser.distId != "-1") {
            val stateMaster = arrayList.find { it.id == rishtaUser.distId!!.toLong() }
            val index = arrayList.indexOf(stateMaster)
            spDist.setSelection(index)
        } else if (!tempPermDistId.isNullOrEmpty()) {
            val dm = arrayList.find { it.id == tempPermDistId!!.toLong() }
            val index = arrayList.indexOf(dm)
            spDist.setSelection(index)
        }

        spDist.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
//                if (position != 0) {
//                    val distmaster = parent?.selectedItem as DistrictMaster
//                    newUserRegPresenter.getCities(distmaster.id)
//                }
            }
        }
    }

    override fun setPermCitySpinner(arrayList: ArrayList<CityMaster>) {
        spCity?.adapter =
            CustomSpinner(
                activity!!,
                android.R.layout.simple_list_item_1,
                arrayList.toTypedArray(),
                Constants.CITY
            )
        val rishtaUser = CacheUtils.getNewRishtaUser()
        if (tempPermPincode == etPinCode.text.toString() && rishtaUser.cityId != null && rishtaUser.cityId != "-1") {
            val stateMaster = arrayList.find { it.id == rishtaUser.cityId!!.toLong() }
            val index = arrayList.indexOf(stateMaster)
            spCity.setSelection(index)
        } else if (!tempPermCityId.isNullOrEmpty()) {
            val cm = arrayList.find { it.id == tempPermCityId!!.toLong() }
            val index = arrayList.indexOf(cm)
            spCity.setSelection(index)
        }

        spCity.onItemSelectedListener = object : SpinnerUtils() {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    val cityMaster = parent?.selectedItem as CityMaster
                }
                if (position != 0 && parent?.selectedItem is CityMaster) {
                    val cityMaster = parent?.selectedItem as CityMaster
                    if (cityMaster.id == -2L) {
                        val newRishtaUser = CacheUtils.getNewRishtaUser()
                        tlCity.visibility = View.VISIBLE
                        etCity.setText(newRishtaUser.otherCity)
                    }
                    if (cityMaster.id != -2L) {
                        tlCity.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun setCurrCitiesByPinCode(it: List<CityMaster>?) {
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
                tempPermStateId = cityMaster.stateId?.toString()
                tempPermDistId = cityMaster.districtId?.toString()
                tempPermCityId = cityMaster.id?.toString()
            }
        }
    }


    private fun choosePhotoFromGallery(IMAGE_REQUEST: Int) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, IMAGE_REQUEST)
    }

    private fun selectOrCaptureImage(IMAGE_REQUEST: Int) {
        Log.d("kamini", IMAGE_REQUEST.toString())
        val dialog = AlertDialog.Builder(activity)
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

    override fun setCurrStatesSpinner(arrayList: ArrayList<StateMaster>) {
        spCurrState?.adapter =
            CustomSpinner(
                activity!!,
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

    override fun setCurrDistrictSpinner(arrayList: ArrayList<DistrictMaster>) {
        spCurrDist?.adapter =
            CustomSpinner(
                activity!!,
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


    override fun setCurrCitySpinner(arrayList: ArrayList<CityMaster>) {
        spCurrCity?.adapter =
            CustomSpinner(
                activity!!,
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
//                if (position != 0) {
//                    val cityMaster = parent?.selectedItem as CityMaster
//                }

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

    private fun setInputs() {
        val rishtaUser = CacheUtils.getRishtaUser()
        etName.setText(rishtaUser.name)
        val position = AppUtils.getPosition(activity!!, R.array.select_gender, rishtaUser.gender?.trim());

        etOutletName.setText(rishtaUser.firmName)
        etContactNumber.setText(rishtaUser.contactNo)
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
        reUpdateKycPresenter.getPincodeList(etCurrentPinCode.text.toString())
        tempCurrentPincode = rishtaUser.currPinCode
        tempPermPincode = rishtaUser.pinCode

        if (rishtaUser.addDiff == 1) {
            llCurrentAdd.visibility = View.VISIBLE
        } else {
            llCurrentAdd.visibility = View.GONE
        }

        if (!rishtaUser.rejectedReasonsStr.isNullOrEmpty()) {
            if (rishtaUser.rejectedReasonsStr!!.contains(",")) {
                val split = rishtaUser.rejectedReasonsStr!!.split(",")
                rejectionReasonListAdapter.mList = split
                rejectionReasonListAdapter.notifyDataSetChanged()
            } else {
                val strReasons = arrayListOf<String>()
                strReasons.add(rishtaUser.rejectedReasonsStr!!)
                rejectionReasonListAdapter.mList = strReasons
                rejectionReasonListAdapter.notifyDataSetChanged()
            }
        }
        etIDNo.setText(rishtaUser.kycDetails.aadharOrVoterOrDlNo)
        etPanNo.setText(rishtaUser.kycDetails.panCardNo)
        val idFrontUrl = AppUtils.getIdCardUrl() + rishtaUser.kycDetails.aadharOrVoterOrDLFront
        val idProofFront = CacheUtils.getFileUploader().getIdProofFileFront()
        setImageForProofs(idProofFront, idFrontUrl, ivIdFront)

        val idProofBack = CacheUtils.getFileUploader().getIdProofBackFile()
        val idBackUrl = AppUtils.getIdCardUrl() + rishtaUser.kycDetails.aadharOrVoterOrDlBack
        setImageForProofs(idProofBack, idBackUrl, ivIdBack)

        val panFrontUrl = AppUtils.getPanCardUrl() + rishtaUser.kycDetails.panCardFront
        val panFront = CacheUtils.getFileUploader().getPanCardFrontFile()
        setImageForProofs(panFront, panFrontUrl, ivPanFront)


        if (!rishtaUser.gstYesNo.isNullOrEmpty()) {
            if (rishtaUser.gstYesNo.equals("yess", true)) {
                spGst.setSelection(2)
            } else {
                spGst.setSelection(1)
            }
        }
        val gstFile = CacheUtils.getFileUploader().getGstFile()
        val gstUrl = AppUtils.getGSTUrl() + rishtaUser.gstPic
        setImageForProofs(gstFile, gstUrl, ivGstPic)

        etGSTNo.setText(rishtaUser.gstNo)

    }

    private fun setImageForProofs(file: File?, proofUrl: String, imageView: ImageView) {
        if (file != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imageView.setImageURI(Uri.parse(file.toString()));
            } else {
                imageView.setImageURI(Uri.fromFile(file));
            }
        } else {
            Glide.with(this).load(proofUrl)
                .placeholder(R.drawable.no_image).into(imageView)
        }

        imageView.setOnClickListener {
            if (file != null) {
                FileUtils.zoomFileImage(activity!!, file)
            } else
                FileUtils.zoomImage(activity!!, proofUrl!!)
        }
    }


    override fun hideKeyBoard() = AppUtils.hideKeyboard(activity!!)


    private fun takePhotoFromCamera(IMAGE_REQUEST: Int) {
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
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return
        var photoUri: Uri?
        photoUri = data!!.data
        var compressImage: String? = null
        var bitmap: Bitmap? = null
        if (photoUri == null) {
            val bitmap = data.extras?.get("data") as Bitmap
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
        if (requestCode == FileUtils.GSTNo) {
            AppUtils.checkNullAndSetText(tvGstPic, file.name, getString(R.string.file))
            Glide.with(this!!).load(photoUri).into(ivGstPic)
            FileUtils.setToFileUploader(requestCode, compressImage)
            setImageForProofs(file, AppUtils.getGSTUrl(), ivGstPic)
        } else if (requestCode == FileUtils.idCardPhotoFront) {
            AppUtils.checkNullAndSetText(tvAadharFront, file.name, getString(R.string.file))
            Glide.with(this).load(photoUri).into(ivIdFront)
            FileUtils.setToFileUploader(requestCode, compressImage)
            setImageForProofs(file, AppUtils.getIdCardUrl(), ivIdFront)

        } else if (requestCode == FileUtils.idCardPhotoBack) {
            AppUtils.checkNullAndSetText(tvAadharBack, file.name, getString(R.string.file))
            Glide.with(this).load(photoUri).into(ivIdBack)
            FileUtils.setToFileUploader(requestCode, compressImage)
            setImageForProofs(file, AppUtils.getIdCardUrl(), ivIdBack)
        }
         //Upload Pan front
        else if (requestCode == FileUtils.panCardFront) {
            AppUtils.checkNullAndSetText(tvPanFront, file.name, getString(R.string.file))
            Glide.with(this).load(photoUri).into(ivPanFront)
            FileUtils.setToFileUploader(requestCode, compressImage)
            setImageForProofs(file, AppUtils.getPanCardUrl(), ivPanFront)
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
        activity?.toast(toast)
    }

    override fun showError() {
        activity?.toast(resources.getString(R.string.something_wrong))
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.llIAadharFront -> {
                selectOrCaptureImage(FileUtils.idCardPhotoFront)
            }
            R.id.llAadharBack -> {
                selectOrCaptureImage(FileUtils.idCardPhotoBack)
            }
            R.id.llGSTPic -> {
                selectOrCaptureImage(FileUtils.GSTNo)
            }
            R.id.llIPanFront -> {
                selectOrCaptureImage(FileUtils.panCardFront)
            }

            R.id.btnPreview -> {
                getInputs()
            }
        }
    }

    private fun getInputs() {
        val rishtaUser = CacheUtils.getRishtaUser()
        var isAddDiff = -1
        if (spIsCurrentAddSame.selectedItemPosition == 1) {
            isAddDiff = 1
        } else if (spIsCurrentAddSame.selectedItemPosition == 2) {
            isAddDiff = 0
        }

        val permAddress = etPermanentAddress.text.toString()
        val permLandmark = etLandmark.text.toString()
        val permPincode = etPinCode.text.toString()
        val permStreetLocality = etStreetLocality.text.toString()
        val currAddress = etCurrentAddress.text.toString()
        val currStreetLocality = etCurrentStreetLocality.text.toString()
        val currLandmark = etCurrentLandmark.text.toString()

        val currentPinCOde = etCurrentPinCode.text.toString()
        val kycDetails = rishtaUser.kycDetails
        rishtaUser.preferredLanguagePos = AppUtils.getSelectedLangId().toString()
        rishtaUser.addDiff = isAddDiff
        rishtaUser.permanentAddress = permAddress
        rishtaUser.landmark = permLandmark
        rishtaUser.pinCode = permPincode
        rishtaUser.streetAndLocality = permStreetLocality
        rishtaUser.currentAddress = currAddress
        rishtaUser.currStreetAndLocality = currStreetLocality
        rishtaUser.currLandmark = currLandmark

        if (spState.selectedItem is StateMaster) {
            val stateMaster = spState.selectedItem as StateMaster
            rishtaUser.state = stateMaster.stateName.toString()
            rishtaUser.stateId = stateMaster.id.toString()
        }

        if (spDist.selectedItem is DistrictMaster) {
            val distMaster = spDist.selectedItem as DistrictMaster
            rishtaUser.distId = distMaster.id.toString()
            rishtaUser.dist = distMaster.districtName.toString()
        }

        if (spCity.selectedItem is CityMaster) {
            val cityMaster = spCity.selectedItem as CityMaster
            rishtaUser.cityId = cityMaster.id.toString()
            if (cityMaster.id != -2L) {
                rishtaUser.city = cityMaster.cityName.toString()
                rishtaUser.otherCity = null
            }
            if (cityMaster.id == -2L) {
                rishtaUser.city = null
                rishtaUser.otherCity = etCity.text.toString()
            }
        }
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
            rishtaUser.currPinCode = currentPinCOde
            if (spCurrCity.selectedItem is CityMaster) {
                rishtaUser.currCityId = cityMaster.id.toString()

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
            rishtaUser.currStateId = rishtaUser.stateId
            rishtaUser.currDistId = rishtaUser.distId
            rishtaUser.currCityId = rishtaUser.cityId
            rishtaUser.currPinCode = currentPinCOde
        }
        var haveGST = -1
        if (spGst.selectedItemPosition == 1) {
            haveGST = 0
        } else if (spGst.selectedItemPosition == 2) {
            haveGST = 1
        }
        if (haveGST == 1) {
            rishtaUser.gstYesNo = "yess"
            rishtaUser.gstNo = etGSTNo.text.toString().trim()
        } else {
            rishtaUser.gstYesNo = "no"
        }
        kycDetails.aadharOrVoterOrDlNo = etIDNo.text.toString()
        kycDetails.panCardNo = etPanNo.text.toString()

        reUpdateKycPresenter.validate(rishtaUser)
    }

    override fun navigateToRetailerPreview() {
        (activity as ReUpdateKycActivity).navigateToRetailerKYCPreview()
    }

    override fun showMsgDialog(message: String) {
        AppUtils.showErrorDialogWithFinish(layoutInflater, activity!!, message, this)
    }

    override fun finishView() {
        activity!!.finish()
    }
}