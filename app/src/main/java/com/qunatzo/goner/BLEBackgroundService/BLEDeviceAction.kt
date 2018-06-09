package com.qunatzo.goner.BLEBackgroundService

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService

interface BLEDeviceAction {
    fun actionForDevice(gattService: BluetoothGattService?, gattDevice: BluetoothGatt?)
}