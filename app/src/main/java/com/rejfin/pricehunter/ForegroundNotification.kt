package com.rejfin.pricehunter

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat

class ForegroundNotification {
    fun createNotification(context:Context, message:String?, title:String, url:String) : Notification{

        val notificationIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        val intent = PendingIntent.getActivity(context, 0,
        notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val builder = NotificationCompat.Builder(context, "price_alert").apply {
            setSmallIcon(R.drawable.ic_offer)
            setContentText(message)
            setContentTitle(title)
            setContentIntent(intent)
            setStyle(NotificationCompat.BigTextStyle())
            setAutoCancel(true)
            setGroup("alert_price")
            this.priority = NotificationCompat.PRIORITY_HIGH
        }
        return builder.build()
    }

    fun summary(context: Context): Notification{
        val x = NotificationCompat.Builder(context, "price_alert").apply{
            setContentTitle("PriceHunter")
            setSmallIcon(R.drawable.ic_offer)
            setStyle(NotificationCompat.InboxStyle())
            setGroup("alert_price")
            setGroupSummary(true)
        }
        return x.build()
    }
}