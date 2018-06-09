package com.qunatzo.goner.BLEBackgroundService

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import com.qunatzo.goner.deviceBLE.BLE_UUID

class BLEBackgroundGattCallback(private val action: BLEDeviceAction) :  BluetoothGattCallback() {
    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        super.onServicesDiscovered(gatt, status)
        action.actionForDevice(gatt?.getService(BLE_UUID.SERVICE_UUID.uuid), gatt)
        gatt?.close()
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
    }
}