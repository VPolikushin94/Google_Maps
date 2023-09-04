package com.example.mapstest.data.network

import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.mapstest.BuildConfig.MAPS_API_KEY
import okhttp3.OkHttpClient

object RetrofitInstance {
    private const val baseUrl = "https://maps.googleapis.com/"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain -> return@addInterceptor addApiKeyToRequests(chain) }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: MapsApi = retrofit.create(MapsApi::class.java)

    private fun addApiKeyToRequests(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        val originalHttpUrl = chain.request().url()
        val newUrl = originalHttpUrl.newBuilder()
            .addQueryParameter("key", MAPS_API_KEY).build()
        request.url(newUrl)
        return chain.proceed(request.build())
    }
}