package com.paorose.stillinthegame.billing

import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.getCustomerInfoWith
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Still In The Game+ state, from RevenueCat's "plus" entitlement. */
object Plus {
    const val ENTITLEMENT = "plus"

    private val _active = MutableStateFlow(false)
    val active: StateFlow<Boolean> = _active.asStateFlow()

    val configured: Boolean get() = Purchases.isConfigured

    fun update(info: CustomerInfo) {
        _active.value = info.entitlements[ENTITLEMENT]?.isActive == true
    }

    /** Call once at start: reads the current state and listens for changes. */
    fun start() {
        if (!Purchases.isConfigured) return
        Purchases.sharedInstance.updatedCustomerInfoListener = UpdatedCustomerInfoListener { update(it) }
        Purchases.sharedInstance.getCustomerInfoWith(onError = { }, onSuccess = { update(it) })
    }
}
