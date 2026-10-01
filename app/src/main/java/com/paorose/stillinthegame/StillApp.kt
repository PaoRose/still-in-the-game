package com.paorose.stillinthegame

import android.app.Application
import com.paorose.stillinthegame.billing.Plus
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration

class StillApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val key = BuildConfig.REVENUECAT_API_KEY
        if (key.isNotBlank()) {
            Purchases.logLevel = LogLevel.DEBUG
            Purchases.configure(PurchasesConfiguration.Builder(this, key).build())
            Plus.start()
        }
    }
}
