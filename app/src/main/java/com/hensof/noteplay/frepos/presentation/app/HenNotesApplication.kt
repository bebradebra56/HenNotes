package com.hensof.noteplay.frepos.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.hensof.noteplay.frepos.presentation.di.henNotesModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface HenNotesAppsFlyerState {
    data object HenNotesDefault : HenNotesAppsFlyerState
    data class HenNotesSuccess(val henNotesData: MutableMap<String, Any>?) :
        HenNotesAppsFlyerState
    data object HenNotesError : HenNotesAppsFlyerState
}

interface HenNotesAppsApi {
    @Headers("Content-Type: application/json")
    @GET(HEN_NOTES_LIN)
    fun henNotesGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}
private const val HEN_NOTES_APP_DEV = "Lyy5XQBZDPk5kDdogy5TM5"
private const val HEN_NOTES_LIN = "com.hensof.noteplay"
class HenNotesApplication : Application() {
    private var henNotesIsResumed = false
    private var henNotesConversionTimeoutJob: Job? = null
    private var henNotesDeepLinkData: MutableMap<String, Any>? = null
    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        henNotesSetDebufLogger(appsflyer)
        henNotesMinTimeBetween(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(p0: DeepLinkResult) {
                when (p0.status) {
                    DeepLinkResult.Status.FOUND -> {
                        henNotesExtractDeepMap(p0.deepLink)
                        Log.d(HEN_NOTES_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                    }

                    DeepLinkResult.Status.NOT_FOUND -> {
                        Log.d(HEN_NOTES_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                    }

                    DeepLinkResult.Status.ERROR -> {
                        Log.d(HEN_NOTES_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                    }
                }
            }

        })
        appsflyer.init(
            HEN_NOTES_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    henNotesConversionTimeoutJob?.cancel()
                    Log.d(HEN_NOTES_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = henNotesGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.henNotesGetClient(
                                    devkey = HEN_NOTES_APP_DEV,
                                    deviceId = henNotesGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(HEN_NOTES_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic" || resp?.get("af_status") == null) {
                                    henNotesResume(HenNotesAppsFlyerState.HenNotesError)
                                } else {
                                    henNotesResume(
                                        HenNotesAppsFlyerState.HenNotesSuccess(resp)
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(HEN_NOTES_MAIN_TAG, "Error: ${d.message}")
                                henNotesResume(HenNotesAppsFlyerState.HenNotesError)
                            }
                        }
                    } else {
                        henNotesResume(HenNotesAppsFlyerState.HenNotesSuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    henNotesConversionTimeoutJob?.cancel()
                    Log.d(HEN_NOTES_MAIN_TAG, "onConversionDataFail: $p0")
                    henNotesResume(HenNotesAppsFlyerState.HenNotesError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(HEN_NOTES_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(HEN_NOTES_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, HEN_NOTES_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(HEN_NOTES_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(HEN_NOTES_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
            }
        })
        henNotesStartConversionTimeout()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@HenNotesApplication)
            modules(
                listOf(
                    henNotesModule
                )
            )
        }
    }

    private fun henNotesExtractDeepMap(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(HEN_NOTES_MAIN_TAG, "Extracted DeepLink data: $map")
        henNotesDeepLinkData = map
    }

    private fun henNotesStartConversionTimeout() {
        henNotesConversionTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(30000)
            if (!henNotesIsResumed) {
                Log.d(HEN_NOTES_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
                henNotesResume(HenNotesAppsFlyerState.HenNotesError)
            }
        }
    }

    private fun henNotesResume(state: HenNotesAppsFlyerState) {
        henNotesConversionTimeoutJob?.cancel()
        if (state is HenNotesAppsFlyerState.HenNotesSuccess) {
            val convData = state.henNotesData ?: mutableMapOf()
            val deepData = henNotesDeepLinkData ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!henNotesIsResumed) {
                henNotesIsResumed = true
                henNotesConversionFlow.value = HenNotesAppsFlyerState.HenNotesSuccess(merged)
            }
        } else {
            if (!henNotesIsResumed) {
                henNotesIsResumed = true
                henNotesConversionFlow.value = state
            }
        }
    }

    private fun henNotesGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(HEN_NOTES_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun henNotesSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun henNotesMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun henNotesGetApi(url: String, client: OkHttpClient?) : HenNotesAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    companion object {
        var henNotesInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val henNotesConversionFlow: MutableStateFlow<HenNotesAppsFlyerState> = MutableStateFlow(
            HenNotesAppsFlyerState.HenNotesDefault
        )
        var HEN_NOTES_FB_LI: String? = null
        const val HEN_NOTES_MAIN_TAG = "HenNotesMainTag"
    }
}