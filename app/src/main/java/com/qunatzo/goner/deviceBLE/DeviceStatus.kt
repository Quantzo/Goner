package com.qunatzo.goner.deviceBLE

enum class DeviceStatus(val code: Byte) {
    NORMAL(0x0),
    LOST(0x1),
    UNPAIRED(0x3);
    companion object {
        fun from(code: Byte): DeviceStatus = DeviceStatus.values().first{ it.code == code}
    }
}