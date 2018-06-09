package com.qunatzo.goner

import android.annotation.SuppressLint
import android.app.Activity
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.AppCompatTextView

import com.qunatzo.goner.BLEBackgroundService.BLEScannerJobService
import com.qunatzo.goner.BLEMainActivity.BLEMainActivityGattCallback
import com.qunatzo.goner.deviceBLE.*
import com.qunatzo.goner.firebase.markAsFound
import com.qunatzo.goner.firebase.markAsLost
import com.qunatzo.goner.firebase.sendNotification
import java.util.*


class MainActivity : AppCompatActivity() {

    var geocoder: Geocoder? = null
    var address: Address? = null
    var scanner: BLEScanner? = null
    var connection: DeviceController? = null
    var gattCallback: BLEMainActivityGattCallback? = null
    var scanCallback: BLEScanCallback?  = null

    var connectedCheckbox:AppCompatCheckBox? = null
    var scanningCheckbox:AppCompatCheckBox? = null
    var pairableCheckbox:AppCompatCheckBox? = null

    var deviceInfoTextView:AppCompatTextView? = null
    var geoListener : addresListener? = null


    var deviceConnected:Boolean = false
        set(value) {
            field = value
            runOnUiThread { connectedCheckbox?.isChecked = value }


        }
    var devicePairable:Boolean = false
        set(value) {
            field = value
            runOnUiThread { scanningCheckbox?.isChecked = value }
        }

    var deviceInfo:String = ""
    set(value) {
        field = value
        runOnUiThread { deviceInfoTextView?.text = value }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestLocationPermissions()
        geocoder= Geocoder(this)
        gattCallback  = BLEMainActivityGattCallback(getTelephoneUUID(this),this)
        scanCallback = BLEScanCallback(this, gattCallback!!)
        scanner = BLEScanner((getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter, scanCallback!! )


        connectedCheckbox = findViewById<AppCompatCheckBox>(R.id.connectedCheckbox)
        scanningCheckbox = findViewById<AppCompatCheckBox>(R.id.scanningCheckbox)
        //pairableCheckbox = findViewById<AppCompatCheckBox>(R.id.pairableCheckbox)

        findViewById<AppCompatButton>(R.id.pairButton).setOnClickListener{
            connection?.changeUserID(getTelephoneUUID(this))
            connection?.changeStatus(DeviceStatus.NORMAL)
            deviceInfo = compileDeviceInfo()
        }
        findViewById<AppCompatButton>(R.id.unpairButton).setOnClickListener {
            connection?.changeStatus(DeviceStatus.UNPAIRED)
            deviceInfo = compileDeviceInfo()
        }
        findViewById<AppCompatButton>(R.id.lostButton).setOnClickListener {
            markAsLost("9cd58e7b-a7f7-4725-8730-756f621a44ab")
        }
        findViewById<AppCompatButton>(R.id.foundButton).setOnClickListener{
            connection?.changeStatus(DeviceStatus.NORMAL)
            markAsFound("9cd58e7b-a7f7-4725-8730-756f621a44ab")
        }
        findViewById<AppCompatButton>(R.id.scanButton).setOnClickListener {
            scanningCheckbox?.isChecked = true
            startScanning()
        }
        findViewById<AppCompatButton>(R.id.StopButton).setOnClickListener {
            scanningCheckbox?.isChecked = false
            stopScanning()
        }

        deviceInfoTextView = findViewById<AppCompatTextView>(R.id.info)


        geoListener = addresListener(geocoder, this)

        setupLocationScanning()



    }
    fun compileDeviceInfo():String{
        return if(deviceConnected)
        {
            "User UUID: ${connection?.readUserID().toString()}" + "\n" +
                    "Device UUID: ${connection?.readDeviceID().toString()}" + "\n" +
                    "Status : ${connection?.readStatus().toString()}"
        }else
            ""
    }

    fun stopScanning() {

        scanner?.stopScanning()
        connection?.disconnect()
        deviceConnected = false
        scanningCheckbox?.isChecked = false
        scanCallback?.deviceFound = false

    }

    private fun startScanning(){
        scanner?.scanForBLEDevices()
        scanningCheckbox?.isChecked = true

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
        {
            checkBluetooth()
        }
        else
        {
            System.exit(1)
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //bluetooth
        if(requestCode == 1)
        {
            if (resultCode == Activity.RESULT_OK)
            {
            }
            else
            {
                System.exit(1)
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun startService() {
        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.cancelAll()
        val jobInfo = JobInfo.Builder(1, ComponentName(this, BLEScannerJobService::class.java))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(15000000)
                .build()

        jobScheduler.schedule(jobInfo)

    }



    @SuppressLint("MissingPermission")
    private fun setupLocationScanning()
    {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val dd = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        val dds = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5000,
                20.0f,
                geoListener
        )
    }

    private fun requestLocationPermissions()
    {
        val per = listOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION).toTypedArray()
        requestPermissions(per,1)
    }

    private fun checkBluetooth()
    {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val adapter = bluetoothManager.adapter
        if (adapter == null || !adapter.isEnabled)
        {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, 1)
        }
    }


    class addresListener(val geocoder: Geocoder?, val context: MainActivity) : LocationListener
    {
        override fun onProviderDisabled(p0: String?) {
        }
        override fun onProviderEnabled(p0: String?) {

        }
        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

        }
        override fun onLocationChanged(p0: Location?) {
            val posList = p0?.latitude?.let {
                geocoder?.getFromLocation(it, p0.longitude, 1) }
            context.address = posList?.first()


        }
    }




}

