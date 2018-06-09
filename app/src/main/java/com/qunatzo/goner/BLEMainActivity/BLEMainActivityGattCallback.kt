package com.qunatzo.goner.BLEMainActivity

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.location.Address
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import com.qunatzo.goner.MainActivity
import com.qunatzo.goner.deviceBLE.BLE_UUID
import com.qunatzo.goner.deviceBLE.DeviceController
import com.qunatzo.goner.deviceBLE.DeviceStatus
import com.qunatzo.goner.deviceList.DeviceItem
import com.qunatzo.goner.deviceList.DeviceList
import com.qunatzo.goner.firebase.checkDevices
import com.qunatzo.goner.firebase.sendNotification
import java.util.*

class BLEMainActivityGattCallback(val phoneUUID: UUID, val context:MainActivity) :  BluetoothGattCallback(), Handler.Callback {
    val bleHandler:Handler
    var gattDevice:BluetoothGatt? = null
    var deviceIDCharacteristic: BluetoothGattCharacteristic? = null
    var userIDCharacteristic: BluetoothGattCharacteristic? = null
    var statusCharacteristic: BluetoothGattCharacteristic? = null
    init {
        val handlerThread = HandlerThread("Ble-worker")
        handlerThread.start()
        bleHandler = Handler(handlerThread.looper, this)
    }
    fun dispose()
    {
        bleHandler.removeCallbacksAndMessages(null)
        bleHandler.looper.quit()
    }
    override fun handleMessage(p0: Message?): Boolean {
        gattDevice = p0?.obj as BluetoothGatt?
        if(p0?.what == 1)
        {
            gattDevice?.discoverServices()
        }
        else if (p0?.what == 2)
        {


            deviceIDCharacteristic = gattDevice?.getService(BLE_UUID.SERVICE_UUID.uuid)?.getCharacteristic(BLE_UUID.DEVICE_ID_CHARACTERISTIC.uuid)
            userIDCharacteristic = gattDevice?.getService(BLE_UUID.SERVICE_UUID.uuid)?.getCharacteristic(BLE_UUID.USER_ID_CHARACTERISTIC.uuid)
            statusCharacteristic = gattDevice?.getService(BLE_UUID.SERVICE_UUID.uuid)?.getCharacteristic(BLE_UUID.STATUS_CHARACTERISTIC.uuid)

            readCharacteristic()

        }
        else if(p0?.what == 3)
        {
            val connection = DeviceController(gattDevice?.getService(BLE_UUID.SERVICE_UUID.uuid), gattDevice)
            context.deviceConnected = true
            context.connection = connection


            when {
                connection.isUnpaired() -> context.devicePairable = true
                connection.isLost() -> sendNotification(connection.readUserID()?.toString(), getStringAddress())
                else -> {
                    checkDevices(connection.readDeviceID().toString(), { found ->
                        if (found) {
                            sendNotification(connection.readUserID()?.toString(), getStringAddress())
                            connection.changeStatus(DeviceStatus.LOST)
                        }
                    })
                }
            }
            context.deviceInfo = context.compileDeviceInfo()
        }
        return true
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        super.onServicesDiscovered(gatt, status)
        bleHandler.obtainMessage(2, gatt).sendToTarget()
    }

    private fun getStringAddress():String
    {
        return "Location: ${context.address?.locality} ${context.address?.thoroughfare} ${context.address?.subThoroughfare}"
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)


        if(newState == BluetoothProfile.STATE_DISCONNECTED)
        {

        }
        if (newState == BluetoothProfile.STATE_CONNECTED)
        {
            bleHandler.obtainMessage(1, gatt).sendToTarget()
        }

    }

    fun readCharacteristic()
    {
        gattDevice?.readCharacteristic(deviceIDCharacteristic)
    }



    override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
        super.onCharacteristicRead(gatt, characteristic, status)
        if (characteristic == deviceIDCharacteristic)
        {
            gattDevice?.readCharacteristic(userIDCharacteristic)
        }
        else if( characteristic == userIDCharacteristic)
        {
            gattDevice?.readCharacteristic(statusCharacteristic)
        }
        else if( characteristic == statusCharacteristic)
        {
            bleHandler.obtainMessage(3, gatt).sendToTarget()
        }

    }

    override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
        super.onCharacteristicWrite(gatt, characteristic, status)
        if (characteristic == userIDCharacteristic)
        {
            gatt?.writeCharacteristic(statusCharacteristic)
        }
        else if(characteristic == statusCharacteristic)
        {
            gatt?.writeCharacteristic(userIDCharacteristic)
        }
    }
}