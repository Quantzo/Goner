package com.qunatzo.goner.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

fun sendNotification(userid: String?, message: String)
{
    val database = FirebaseDatabase.getInstance().getReference("found_notification")
    database.child(userid).setValue(message)

}

fun checkDevices(uuid: String, action: (found:Boolean) -> Unit) {
    FirebaseDatabase.getInstance().getReference("lost_devices").addListenerForSingleValueEvent(object : ValueEventListener
    {
        override fun onDataChange(p0: DataSnapshot?) {
            action(p0?.hasChild(uuid) == true)
        }
        override fun onCancelled(p0: DatabaseError?) {

        }
    })


}
fun markAsFound(uuid: String)
{
    FirebaseDatabase.getInstance().getReference("lost_devices").child(uuid).removeValue()
}

fun markAsLost(uuid: String)
{
    FirebaseDatabase.getInstance().getReference("lost_devices").child(uuid).setValue(true)
}

