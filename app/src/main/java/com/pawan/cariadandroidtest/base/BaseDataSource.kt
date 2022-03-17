package com.pawan.cariadandroidtest.base

import com.pawan.cariadandroidtest.api.ApiResult
import retrofit2.Response
import java.io.IOException

abstract class BaseDataSource {

    protected suspend fun <T> getResult(call: suspend () -> Response<T>): ApiResult<T, String> {
        try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) return ApiResult.OnSuccess(body)
            }
            return ApiResult.OnFailure(" ${response.code()} ${response.message()}")
        } catch (e: Exception) {
            if (e is IOException) ApiResult.NetworkError
            return ApiResult.OnFailure(e.message ?: e.toString())
        }
    }

}