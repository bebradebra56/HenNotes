package com.hensof.noteplay.frepos.data.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.appsflyer.AppsFlyerLib
import com.hensof.noteplay.frepos.presentation.app.HenNotesApplication
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class HenNotesSystemService(private val context: Context) {

    suspend fun henNotesGetGaid() : String  = withContext(Dispatchers.IO){
        val gaid = AdvertisingIdClient.getAdvertisingIdInfo(context).id ?: "00000000-0000-0000-0000-000000000000"
        Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "Gaid: $gaid")
        return@withContext gaid
    }

    fun henNotesGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(context) ?: ""
        Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    fun henNotesGetLocale() : String {
        return  Locale.getDefault().language
    }

    fun henNotesIsOnline(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                return true
            }
        }
        return false
    }

}