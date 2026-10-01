package com.paorose.stillinthegame.billing

import android.content.Context
import android.content.SharedPreferences
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.getCustomerInfoWith
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import com.revenuecat.purchases.logInWith
import com.revenuecat.purchases.logOutWith
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Still In The Game+ state.
 *
 * Plus is active when either:
 *  - RevenueCat says the "plus" entitlement is active (a purchase, or a promotional
 *    entitlement granted to the signed-in account from the RevenueCat dashboard), or
 *  - a promo code was redeemed on this phone (works offline and in builds without a key,
 *    so judges and testers can always reach Plus).
 */
object Plus {
    const val ENTITLEMENT = "plus"

    /** Promo codes that unlock Plus on this phone. Not case sensitive. */
    private val CODES = setOf("SHIPATON2026", "STILLPLUS", "JUDGEPLUS")

    enum class Source { NONE, PURCHASE, ACCOUNT, CODE }

    private var prefs: SharedPreferences? = null
    private var storeActive = false
    private var storeFromPurchase = false
    private var codeActive = false

    private val _active = MutableStateFlow(false)
    val active: StateFlow<Boolean> = _active.asStateFlow()

    private val _source = MutableStateFlow(Source.NONE)
    val source: StateFlow<Source> = _source.asStateFlow()

    /** The signed-in username, or null when the user is anonymous. */
    private val _account = MutableStateFlow<String?>(null)
    val account: StateFlow<String?> = _account.asStateFlow()

    private val _redeemedCode = MutableStateFlow<String?>(null)
    val redeemedCode: StateFlow<String?> = _redeemedCode.asStateFlow()

    val configured: Boolean get() = Purchases.isConfigured

    fun update(info: CustomerInfo) {
        val ent = info.entitlements[ENTITLEMENT]
        storeActive = ent?.isActive == true
        // A promotional entitlement from the dashboard shows up with the PROMOTIONAL store.
        storeFromPurchase = storeActive && ent?.store?.name != "PROMOTIONAL"
        refresh()
    }

    private fun refresh() {
        _active.value = storeActive || codeActive
        _source.value = when {
            storeActive && storeFromPurchase -> Source.PURCHASE
            storeActive -> Source.ACCOUNT
            codeActive -> Source.CODE
            else -> Source.NONE
        }
    }

    /** Call once at start: restores a redeemed code and listens to RevenueCat. */
    fun start(context: Context) {
        val p = context.getSharedPreferences("still_plus", Context.MODE_PRIVATE)
        prefs = p
        _redeemedCode.value = p.getString("code", null)
        codeActive = _redeemedCode.value != null
        _account.value = p.getString("account", null)
        refresh()
        if (!Purchases.isConfigured) return
        Purchases.sharedInstance.updatedCustomerInfoListener = UpdatedCustomerInfoListener { update(it) }
        Purchases.sharedInstance.getCustomerInfoWith(onError = { }, onSuccess = { update(it) })
        if (Purchases.sharedInstance.isAnonymous) _account.value = null
    }

    sealed interface Result {
        data object Ok : Result
        data class Error(val message: String) : Result
    }

    /** Redeems a promo code. Works without internet. */
    fun redeem(raw: String, done: (Result) -> Unit) {
        val code = raw.trim().uppercase().replace(" ", "").replace("-", "")
        if (code.isEmpty()) return done(Result.Error("Type your code first."))
        if (code !in CODES) return done(Result.Error("That code didn't work. Check the spelling and try again."))
        prefs?.edit()?.putString("code", code)?.apply()
        _redeemedCode.value = code
        codeActive = true
        refresh()
        // Leave a note on the RevenueCat customer, so redeemed codes show up in the dashboard.
        if (Purchases.isConfigured) {
            runCatching { Purchases.sharedInstance.setAttributes(mapOf("promo_code" to code)) }
        }
        done(Result.Ok)
    }

    fun removeCode() {
        prefs?.edit()?.remove("code")?.apply()
        _redeemedCode.value = null
        codeActive = false
        refresh()
    }

    /**
     * Signs in to RevenueCat with a username, so Plus follows the user to a new phone
     * and can be granted from the RevenueCat dashboard.
     */
    fun signIn(raw: String, done: (Result) -> Unit) {
        val name = raw.trim().lowercase()
        if (name.length < 3) return done(Result.Error("Use at least 3 characters."))
        if (!name.all { it.isLetterOrDigit() || it == '_' || it == '.' }) {
            return done(Result.Error("Use only letters, numbers, dots or underscores."))
        }
        if (!Purchases.isConfigured) {
            return done(Result.Error("Accounts aren't available in this build. You can still use a promo code."))
        }
        Purchases.sharedInstance.logInWith(
            "still_$name",
            onError = { done(Result.Error("Couldn't sign in. Check your connection and try again.")) },
            onSuccess = { info, _ ->
                prefs?.edit()?.putString("account", name)?.apply()
                _account.value = name
                update(info)
                done(Result.Ok)
            }
        )
    }

    fun signOut(done: (Result) -> Unit) {
        prefs?.edit()?.remove("account")?.apply()
        _account.value = null
        if (!Purchases.isConfigured || Purchases.sharedInstance.isAnonymous) return done(Result.Ok)
        Purchases.sharedInstance.logOutWith(
            onError = { done(Result.Error("Couldn't sign out right now.")) },
            onSuccess = {
                update(it)
                done(Result.Ok)
            }
        )
    }
}
