package com.pawan.cariadandroidtest.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pawan.cariadandroidtest.BuildConfig
import com.pawan.cariadandroidtest.api.ImpStationRepository
import com.pawan.cariadandroidtest.api.ApiInterface
import com.pawan.cariadandroidtest.api.StationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val HTTP_TIMEOUT = 30L
    private const val DEFAULT_KEY = "1e2cb9c6-a0e9-4a68-bc09-f3c97a6bd8e4"

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson) : Retrofit {
        val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val httpClient = OkHttpClient.Builder().apply {
            readTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
            connectTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
        }
        httpClient.addInterceptor { chain ->
            val ongoing = chain.request().newBuilder()
            ongoing.addHeader("X-API-Key", DEFAULT_KEY)
            val response = chain.proceed(ongoing.build())
            response
        }

        if (BuildConfig.DEBUG) {
            httpClient.addInterceptor(loggingInterceptor)
        }

        return Retrofit.Builder()
            .baseUrl("https://api.openchargemap.io/")
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    fun provideApiInterface(retrofit: Retrofit): ApiInterface = retrofit.create(ApiInterface::class.java)

    @Singleton
    @Provides
    fun provideStationsRepo(service: ApiInterface): StationRepository = ImpStationRepository(service)

}