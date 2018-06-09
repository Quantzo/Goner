package com.qunatzo.goner.deviceList

import com.qunatzo.goner.deviceBLE.DeviceController


class DeviceItem(val uuid: String?, var connected:Boolean, var deviceController: DeviceController?, var ownership:Boolean)
{
    fun markAsLost(){

    }
    fun markAsFound(){

    }
    fun pair(){

    }
    fun unPair(){

    }
    fun toJsonDevice(): DeviceList.JsonDevice {
        return  DeviceList.JsonDevice(uuid)
    }

}