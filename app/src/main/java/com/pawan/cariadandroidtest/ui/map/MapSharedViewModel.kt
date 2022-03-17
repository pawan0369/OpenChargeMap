package com.pawan.cariadandroidtest.ui.map

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.Marker
import com.pawan.cariadandroidtest.api.ApiResult
import com.pawan.cariadandroidtest.api.StationRepository
import com.pawan.cariadandroidtest.base.BaseViewModel
import com.pawan.cariadandroidtest.model.ChargingStationResponse
import com.pawan.cariadandroidtest.model.ChargingStationResponseItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * [MapSharedViewModel] View Model to share data between map and detail screen
 * */
@HiltViewModel
class MapSharedViewModel @Inject constructor(private val repository: StationRepository) :
    BaseViewModel() {

    var clickedMarker: Marker? = null

    private val timeInterval = 30000L
    lateinit var job: Job

    private val _stationMutableLiveData: MutableLiveData<ChargingStationResponse?> =
        MutableLiveData()
    val stationMutableList = _stationMutableLiveData

    private val _stationDetailsLiveData = MutableLiveData<ChargingStationResponseItem?>()
    val stationDetailsData = _stationDetailsLiveData

    private val _detailsIsDismissed = MutableLiveData(false)
    val detailsIsDismissedLiveData = _detailsIsDismissed

    fun startRepeatingJob() {
        isLoading.value = true
        job = repeatingJob()
        job.start()
    }

    fun stopRepeatingJob() {
        job.cancel()
    }

    private fun repeatingJob(): Job =
        CoroutineScope(Dispatchers.Default).launch {
            try {
                while (isActive) {
                    handleAllStationData(repository.getAllStations())
                    delay(timeInterval)
                }
            } catch (e: CancellationException) {
                stopRepeatingJob()
            }
        }

    private fun handleAllStationData(result: ApiResult<ChargingStationResponse, String>) {
        when (result) {
            is ApiResult.NetworkError -> {
                stopLoader()
                networkError.postValue(true)
            }
            is ApiResult.OnSuccess -> {
                stopLoader()
                _stationMutableLiveData.postValue(result.response)
            }
            is ApiResult.OnFailure -> {
                stopLoader()
                displayError.postValue(result.exception)
            }
        }
    }

    fun setClickedMarkerData(item: ChargingStationResponseItem?) {
        _stationDetailsLiveData.value = item
    }

    fun isDetailsDialogDismissed(isDismissed: Boolean) = _detailsIsDismissed.postValue(isDismissed)

    private fun stopLoader() = isLoading.postValue(false)

    override fun onCleared() {
        super.onCleared()
        stopRepeatingJob()
    }

}