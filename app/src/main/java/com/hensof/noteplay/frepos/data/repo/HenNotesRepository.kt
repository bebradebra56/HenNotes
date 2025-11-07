package com.hensof.noteplay.frepos.data.repo

import android.util.Log
import com.hensof.noteplay.frepos.domain.model.HenNotesEntity
import com.hensof.noteplay.frepos.domain.model.HenNotesParam
import com.hensof.noteplay.frepos.presentation.app.HenNotesApplication.Companion.HEN_NOTES_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface HenNotesApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun henNotesGetClient(
        @Body jsonString: JsonObject,
    ): Call<HenNotesEntity>
}


private const val HEN_NOTES_MAIN = "https://hennottes.com/"
class HenNotesRepository {

    suspend fun henNotesGetClient(
        henNotesParam: HenNotesParam,
        henNotesConversion: MutableMap<String, Any>?
    ): HenNotesEntity? {
        val gson = Gson()
        val api = henNotesGetApi(HEN_NOTES_MAIN, null)

        val henNotesJsonObject = gson.toJsonTree(henNotesParam).asJsonObject
        henNotesConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            henNotesJsonObject.add(key, element)
        }
        return try {
            val henNotesRequest: Call<HenNotesEntity> = api.henNotesGetClient(
                jsonString = henNotesJsonObject,
            )
            val henNotesResult = henNotesRequest.awaitResponse()
            Log.d(HEN_NOTES_MAIN_TAG, "Retrofit: Result code: ${henNotesResult.code()}")
            if (henNotesResult.code() == 200) {
                Log.d(HEN_NOTES_MAIN_TAG, "Retrofit: Get request success")
                Log.d(HEN_NOTES_MAIN_TAG, "Retrofit: Code = ${henNotesResult.code()}")
                Log.d(HEN_NOTES_MAIN_TAG, "Retrofit: ${henNotesResult.body()}")
                henNotesResult.body()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(HEN_NOTES_MAIN_TAG, "Retrofit: Get request failed")
            Log.d(HEN_NOTES_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun henNotesGetApi(url: String, client: OkHttpClient?) : HenNotesApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
