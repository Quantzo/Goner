package com.qunatzo.goner.deviceBLE
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import java.util.UUID

class DeviceController(val gattService: BluetoothGattService?, val gattDevice: BluetoothGatt?){
    val deviceIDCharacteristic: BluetoothGattCharacteristic? = gattService?.getCharacteristic(BLE_UUID.DEVICE_ID_CHARACTERISTIC.uuid)
    val userIDCharacteristic: BluetoothGattCharacteristic? = gattService?.getCharacteristic(BLE_UUID.USER_ID_CHARACTERISTIC.uuid)
    val statusCharacteristic: BluetoothGattCharacteristic? = gattService?.getCharacteristic(BLE_UUID.STATUS_CHARACTERISTIC.uuid)




    fun readDeviceID():UUID?
    {
        return deviceIDCharacteristic?.value?.let { getUUIDFromBytes(it) }
    }

    fun readUserID():UUID?
    {

        return userIDCharacteristic?.value?.let { getUUIDFromBytes(it) }
    }

    fun changeUserID(userId: UUID)
    {
        userIDCharacteristic?.value = getBytesFromUUID(userId)

        gattDevice?.writeCharacteristic(userIDCharacteristic)
    }

    fun readStatus():DeviceStatus?
    {
        return statusCharacteristic?.value?.get(0)?.let { DeviceStatus.from(it) }
    }

    fun changeStatus(status: DeviceStatus)
    {
        statusCharacteristic?.value?.set(0, status.code)

        gattDevice?.writeCharacteristic(statusCharacteristic)
    }

    fun  isUnpaired(): Boolean = readStatus() == DeviceStatus.UNPAIRED
    fun  isOwner(telephoneUUID: UUID): Boolean = readUserID() == telephoneUUID
    fun  isLost():Boolean = readStatus() == DeviceStatus.LOST
    fun  disconnect(){
        gattDevice?.disconnect()
        gattDevice?.close()
    }

}