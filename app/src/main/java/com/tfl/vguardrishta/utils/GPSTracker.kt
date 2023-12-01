package com.tfl.vguardrishta.utils

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.tfl.vguardrishta.models.Address
import java.io.IOException

class GPSTracker(private val mContext: Context) : Service(), LocationListener {
    private var isGPSEnabled = false
    private var isNetworkEnabled = false
    private var canGetLocation = false
    private var location: Location? = null // location
    var latitude: Double = 0.toDouble() // latitude
    var longitude: Double = 0.toDouble() // longitude
    private var locationManager: LocationManager? = null

    init {
        getLocation()
    }

    companion object {
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters
        private const val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong() // 1 minute
    }

    @SuppressLint("MissingPermission")
    fun getLocation(): Location? {
        try {
            locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

            isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (isGPSEnabled || isNetworkEnabled) {
                this.canGetLocation = true
                if (isGPSEnabled) {
                    locationManager!!.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                    )
                    Log.d("GPS Enabled", "GPS Enabled")
                    if (locationManager != null) {
                        location = locationManager!!
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        if (location != null) {
                            latitude = location!!.latitude
                            longitude = location!!.longitude
                        }
                    }
                }

                if (isNetworkEnabled) {
                    locationManager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                    )
                    Log.d("Network", "Network")
                    if (locationManager != null) {
                        location = locationManager!!
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {
                            latitude = location!!.latitude
                            longitude = location!!.longitude
                        }
                    }
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return location
    }

    override fun onLocationChanged(location: Location) {}

    override fun onProviderDisabled(provider: String) = Unit

    override fun onProviderEnabled(provider: String) = Unit

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

    override fun onBind(arg0: Intent): IBinder? = null


    fun canGetLocation(): Boolean = this.canGetLocation

    fun getAddress(context: Context, latitude: Double, longitude: Double): Address? {

        val networkAvailable = AppUtils.isNetworkAvailable(context)
        if (!networkAvailable) return null
        var addresFromLatLng = ""
        val geocoder = Geocoder(context)
        val addre = Address()
        try {
            val fromLocation = geocoder.getFromLocation(latitude, longitude, 1)
            if (fromLocation != null && fromLocation.isNotEmpty()) {
                val address = fromLocation[0]
                addresFromLatLng = addresFromLatLng + " " + address.getAddressLine(0)
                addresFromLatLng = addresFromLatLng + " " + address.getAddressLine(1)
                addresFromLatLng = addresFromLatLng + " " + address.getAddressLine(2)
                addresFromLatLng = addresFromLatLng + " " + address.getAddressLine(3)
                addre.address = address.getAddressLine(0)
                addre.city = address.locality
                addre.country = address.countryName
                addre.joinAddress = addresFromLatLng
                addre.postalCode = address.postalCode
                addre.knownName = address.featureName
                addre.state = address.adminArea
                addre.countryCode = address.countryCode
                addre.searchArea = address.getAddressLine(1)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return addre
    }

}
