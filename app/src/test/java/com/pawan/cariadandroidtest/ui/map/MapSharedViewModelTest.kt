package com.pawan.cariadandroidtest.ui.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pawan.cariadandroidtest.LiveDataTestUtil
import com.pawan.cariadandroidtest.MyCoroutineRule
import com.pawan.cariadandroidtest.api.ApiResult
import com.pawan.cariadandroidtest.api.StationFakeRepository
import com.pawan.cariadandroidtest.base.BaseViewModel
import com.pawan.cariadandroidtest.model.ChargingStationResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * [MapSharedViewModelTest] To test view model
 */
@ExperimentalCoroutinesApi
class MapSharedViewModelTest : BaseViewModel() {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutineRule = MyCoroutineRule()

    private lateinit var viewModel: MapSharedViewModel

    private lateinit var repository: StationFakeRepository

    @Before
    fun setUp(){
        repository = StationFakeRepository()
        viewModel = MapSharedViewModel(repository)
    }

    @Test
    fun `check if job is activated`() {
        with(viewModel) {
            startRepeatingJob()
            assert(job.isActive)
            stopRepeatingJob()
        }
    }

    @Test
    fun `check if job is canceled`(){
        with(viewModel){
            startRepeatingJob()
            stopRepeatingJob()
            assert(!job.isActive)
        }
    }

    @Test
    fun `check if loader is running`(){
        with(viewModel){
            coroutineRule.runBlockingTest {
                startRepeatingJob()
                assertEquals(true, getIsLoadingLiveData().value)
            }
            stopRepeatingJob()
        }
    }

    @Test
    fun `network Not available`(){
        with(viewModel){
            repository.shouldReturnNetworkError(true)
            coroutineRule.runBlockingTest {
                startRepeatingJob()
            }
            val networkNotAvailable = LiveDataTestUtil.getValue(networkError())
            assertNotNull(networkNotAvailable)
            assertEquals(true, networkNotAvailable)
            stopRepeatingJob()
        }
    }

    @Test
    fun `check if response returned from server is failure`(){
        with(viewModel){
            var resultFailure = ""
            repository.shouldReturnResultFailure(true)
            coroutineRule.runBlockingTest {
                when(val result = repository.getAllStations()){
                    is ApiResult.OnFailure -> {
                        resultFailure = result.exception
                    }
                }
                startRepeatingJob()
            }
            val displayError = LiveDataTestUtil.getValue(getDisplayErrorLiveData())
            assertNotNull(displayError)
            assertEquals(resultFailure, displayError)
            stopRepeatingJob()
        }
    }

    @Test
    fun `check if response returned from server is success`(){
        with(viewModel){
            var stationList = ChargingStationResponse()
            repository.shouldReturnNetworkError(false)
            coroutineRule.runBlockingTest {
                when(val result = repository.getAllStations()){
                    is ApiResult.OnSuccess ->{
                        stationList = result.response
                    }
                }
                startRepeatingJob()
            }
            val fetchedData = LiveDataTestUtil.getValue(stationMutableList)
            assertNotNull(fetchedData)
            assertEquals(stationList, fetchedData)
            stopRepeatingJob()
        }
    }

    @Test
    fun `check if marker has data`(){
        with(viewModel){
            coroutineRule.runBlockingTest {
                startRepeatingJob()
            }
            val expected = repository.fakeStation().first()
            setClickedMarkerData(expected)
            val result = LiveDataTestUtil.getValue(stationDetailsData)
            assertEquals(expected, result)
            stopRepeatingJob()
        }
    }

    @After
    fun clearData(){
        viewModel.stopRepeatingJob()
    }
}