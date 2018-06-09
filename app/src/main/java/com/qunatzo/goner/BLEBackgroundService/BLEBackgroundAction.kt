package com.qunatzo.goner.BLEBackgroundService

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import android.content.Context
import com.qunatzo.goner.deviceBLE.DeviceController
import com.qunatzo.goner.deviceBLE.DeviceStatus
import com.qunatzo.goner.deviceBLE.getTelephoneUUID


class BLEBackgroundAction(val context: Context) : BLEDeviceAction {
    override fun actionForDevice(gattService: BluetoothGattService?, gattDevice: BluetoothGatt?) {


        val connection = DeviceController(gattService, gattDevice)

        if(!connection.isUnpaired())
        {
            if(!connection.isOwner(getTelephoneUUID(context)))
            {
                val status = connection.readStatus()
                if(status == DeviceStatus.LOST)
                {
                    //aws push notification
                }
                else
                {
                    // aws database
                    //if exist
                    //{
                    connection.changeStatus(DeviceStatus.LOST)
                    //aws push notification
                    //}
                }
            }
        }



    }








}