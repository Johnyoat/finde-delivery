package com.finde.deliveryapp.api

import com.finde.deliveryapp.models.BusinessModel
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface APIService {

    @GET("/findepages")
    suspend fun getBusinesses():List<BusinessModel>


    companion object {
        fun create(): APIService {
            val client = OkHttpClient.Builder()
                .addInterceptor {
                    it.proceed(
                        it.request().newBuilder()
                            .build()
                    )
                }.build()


            return Retrofit.Builder()
                .baseUrl("https://finde-africa.uc.r.appspot.com")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(APIService::class.java)
        }
    }
}