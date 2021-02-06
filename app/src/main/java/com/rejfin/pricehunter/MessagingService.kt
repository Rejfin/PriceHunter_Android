package com.rejfin.pricehunter

import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class MessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val notify = ForegroundNotification().createNotification(
                this,
                getString(R.string.fcm_message_price_alert, message.data["price_alert"]+ message.data["currency"]),
                getString(R.string.fcm_message_title, message.data["name"], message.data["price"] + message.data["currency"]),
                message.data["url"].toString()
        )

        val sumNotify = ForegroundNotification().summary(this)

        var id = Random().nextInt(1000)
        id += Random().nextInt(500)
        NotificationManagerCompat.from(this).notify(message.data["product_id"]!!.toInt(),notify)
        NotificationManagerCompat.from(this).notify(150, sumNotify)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val db = FirebaseDatabase.getInstance()
        val ref = db.getReference("fcm_tokens")
        ref.push().setValue(token)
    }
}