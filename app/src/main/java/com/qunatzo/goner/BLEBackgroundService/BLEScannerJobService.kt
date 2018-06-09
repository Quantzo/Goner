package com.qunatzo.goner.BLEBackgroundService

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.bluetooth.BluetoothManager
import android.content.Context
import android.location.*
import android.os.Bundle
import android.os.Handler
import com.qunatzo.goner.deviceBLE.BLEScanCallback
import com.qunatzo.goner.deviceBLE.BLEScanner


class BLEScannerJobService : JobService() {

    val geocoder = Geocoder(this)
    val action = BLEBackgroundAction(this)
    val gattCallback = BLEBackgroundGattCallback(action)
    //val scanner = BLEScanner((getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter, BLEScanCallback(this, gattCallback))

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        //scanner.stopScanning()
        return false
    }
    @SuppressLint("MissingPermission")
    override fun onStartJob(params: JobParameters?): Boolean {


        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, object : LocationListener
        {
            override fun onProviderDisabled(p0: String?) {}
            override fun onProviderEnabled(p0: String?) {}
            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
            override fun onLocationChanged(p0: Location?) {
                val posList = p0?.latitude?.let { geocoder.getFromLocation(it, p0.longitude, 1) }


            }
        },null)
        //scanner.scanForBLEDevices()



        return true
    }
    
}