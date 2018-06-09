package com.qunatzo.goner.deviceBLE

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import com.qunatzo.goner.BLEBackgroundService.BLEBackgroundGattCallback
import com.qunatzo.goner.MainActivity

class BLEScanCallback(val context: MainActivity, val gattCallback: BluetoothGattCallback): ScanCallback()
{
    var gatt:BluetoothGatt? = null
    var deviceFound = false

    override fun onBatchScanResults(results: MutableList<ScanResult>?) {
        super.onBatchScanResults(results)
    }

    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
    }
    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        super.onScanResult(callbackType, result)
        if(!deviceFound)
        {
            gatt = result?.device?.connectGatt(context, true, gattCallback)
            deviceFound = true
            context.stopScanning()

        }



    }

}