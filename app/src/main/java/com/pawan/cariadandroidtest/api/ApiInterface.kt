package com.pawan.cariadandroidtest.api

import com.pawan.cariadandroidtest.model.ChargingStationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("v3/poi")
    suspend fun getAllChargingStations(@Query("latitude") lat: Double = 52.526,
    @Query("longitude") long: Double = 13.415, @Query("distance") distance: Int = 5)
    : Response<ChargingStationResponse>
}