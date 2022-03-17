package com.pawan.cariadandroidtest.api

import com.pawan.cariadandroidtest.model.ChargingStationResponse
import javax.inject.Inject

interface StationRepository {
    suspend fun getAllStations(): ApiResult<ChargingStationResponse, String>
}