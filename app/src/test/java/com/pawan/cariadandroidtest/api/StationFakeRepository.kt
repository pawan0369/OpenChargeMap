package com.pawan.cariadandroidtest.api

import com.pawan.cariadandroidtest.model.AddressInfo
import com.pawan.cariadandroidtest.model.ChargingStationResponse
import com.pawan.cariadandroidtest.model.ChargingStationResponseItem
import com.pawan.cariadandroidtest.model.Country

/**
 * [StationFakeRepository] Fake repository to initialise view model and
 * test it on the real scenario without using mock
 */
class StationFakeRepository: StationRepository {

    private var networkError = false
    private var resultFailure = false

    fun shouldReturnNetworkError(value: Boolean){
        networkError = value
    }

    fun shouldReturnResultFailure(result: Boolean){
        resultFailure = result
    }

    override suspend fun getAllStations(): ApiResult<ChargingStationResponse, String> {
        return if (networkError){
            ApiResult.NetworkError
        }else if (resultFailure){
            ApiResult.OnFailure("Error")
        }else{
            try {
                ApiResult.OnSuccess(ChargingStationResponse())
            }catch (e: Exception){
                ApiResult.OnFailure("Error")
            }

        }
    }

    fun fakeStation(): ChargingStationResponse{
        val addressInfo = AddressInfo(
            "",
            "Berlin",
            Country("", 1, "", "Germany"),
            22,
            52.526,
            13.415,
            "Berlin",
            "Berlin",
            "Berlin"
        )

        val station = ChargingStationResponseItem(addressInfo, 123, 2)
        val fakeResponse = ChargingStationResponse()
        fakeResponse.add(station)
        return fakeResponse
    }
}