package com.qunatzo.goner.deviceList

import android.content.Context
import android.content.SharedPreferences
import com.qunatzo.goner.deviceBLE.DeviceController
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.*



class DeviceList(context: Context){
    val sharedPref: SharedPreferences? = context.getSharedPreferences("DEVICES_DATA", Context.MODE_PRIVATE)
    val devicesList = mutableListOf<DeviceItem>()
    val mapper: ObjectMapper? = jacksonObjectMapper().configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
    init {
        loadOwnedDevicesFromSharedPreferences()
    }

    fun intializeData(){

    }


    fun getOwnedDevices(): List<DeviceItem> {
        return devicesList.filter { it.ownership }
    }
    fun getUnpairedDevices(): List<DeviceItem>
    {
        return devicesList.filter { !it.ownership }
    }
    fun  add(deviceItem: DeviceItem) {
        devicesList.add(deviceItem)

    }

    fun  connected(uuid: String?, connection: DeviceController) {
        devicesList.first { it.uuid == uuid }.let {
            it.connected = true
            it.deviceController = connection
        }

    }

    fun  connectionLost(uuid: String?) {
        devicesList.first { it.uuid == uuid }.connected = false
    }

    fun  remove(uuid: String?) {
        devicesList.removeAll { it.uuid == uuid }

    }
    private fun  saveOwnedDevicesInSharedPreferences()
    {
        val ownedDevicesList = getOwnedDevices().map { it.toJsonDevice() }
        val jsonString = mapper?.writeValueAsString(ownedDevicesList)
        val editor = sharedPref?.edit()
        editor?.putString("DEVICES_DATA", jsonString)
        editor?.apply()

    }
    private fun  loadOwnedDevicesFromSharedPreferences()
    {
        val devices = sharedPref?.getString("DEVICES_DATA", null)
        if (devices != null)
        {
            val dev =  mapper?.readValue<List<JsonDevice>>(devices)
            val ownedDevices = dev?.map { it.toDeviceItem() }
            if(ownedDevices != null)
            {
                devicesList.addAll(ownedDevices)
            }


        }
    }


    data class JsonDevice(val uuid: String?)
    {
        fun toDeviceItem(): DeviceItem {
            return DeviceItem(uuid, false, null, true)
        }
    }


}