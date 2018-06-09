package com.qunatzo.goner.deviceBLE

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.os.ParcelUuid

class BLEScanner(val mBluetoothAdapter: BluetoothAdapter, val scanAction: ScanCallback){
    val scanner: BluetoothLeScanner? = mBluetoothAdapter.bluetoothLeScanner

    fun scanForBLEDevices() {



        val filter = ScanFilter.Builder().setServiceUuid(ParcelUuid(BLE_UUID.SERVICE_UUID.uuid)).build()
        val filters = listOf<ScanFilter>(filter)


        val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setMatchMode(ScanSettings.MATCH_MODE_STICKY)
                .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                .build()
        scanner?.startScan(filters, settings, scanAction )

    }

    fun stopScanning()
    {
        scanner?.flushPendingScanResults(scanAction)
        scanner?.stopScan(scanAction)
    }











}