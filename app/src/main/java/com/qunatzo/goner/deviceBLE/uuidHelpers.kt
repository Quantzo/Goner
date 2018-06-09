package com.qunatzo.goner.deviceBLE

import android.content.Context
import java.nio.ByteBuffer
import java.util.*

fun getBytesFromUUID(uuid: UUID):kotlin.ByteArray{
    val bb = ByteBuffer.wrap(kotlin.ByteArray(16)).let {
        it.putLong(uuid.mostSignificantBits)
        it.putLong(uuid.leastSignificantBits)
    }
    return bb.array()
}

fun getUUIDFromBytes(bytes: kotlin.ByteArray):UUID{
    val bb = ByteBuffer.wrap(bytes)
    return UUID(bb.long, bb.long)
}

fun getTelephoneUUID(context: Context):UUID
{
    val sharedPref = context.getSharedPreferences("USER_UUID", Context.MODE_PRIVATE)
    val uuid = sharedPref.getString("USER_UUID", null)
    if(uuid == null)
    {
        val newUUID = UUID.randomUUID()
        val editor = sharedPref.edit()
        editor.putString("USER_UUID", newUUID.toString())
        editor.apply()
        return newUUID

    }
    return UUID.fromString(uuid)
}