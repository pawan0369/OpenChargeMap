package com.pawan.cariadandroidtest.api

import com.pawan.cariadandroidtest.model.ChargingStationResponse
import com.pawan.cariadandroidtest.base.BaseDataSource
import kotlinx.coroutines.*
import javax.inject.Inject

class ImpStationRepository @Inject constructor(private val service: ApiInterface)
    : BaseDataSource(), StationRepository {

    override suspend fun getAllStations(): ApiResult<ChargingStationResponse, String> =
        coroutineScope { getResult { service.getAllChargingStations() } }
}