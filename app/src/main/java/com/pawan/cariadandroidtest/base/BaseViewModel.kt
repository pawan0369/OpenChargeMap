package com.pawan.cariadandroidtest.base

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pawan.cariadandroidtest.R

/**
 * Created on 16/03/22.
 */
abstract class BaseViewModel: ViewModel() {

    protected val isLoading = MutableLiveData<Boolean>()
    protected val displayError = MutableLiveData<String>()
    protected val networkError = MutableLiveData<Boolean>()

    fun getIsLoadingLiveData(): LiveData<Boolean> {
        return isLoading
    }

    fun getDisplayErrorLiveData(): LiveData<String> {
        return displayError
    }

    fun networkError(): LiveData<Boolean>{
        return networkError
    }
}