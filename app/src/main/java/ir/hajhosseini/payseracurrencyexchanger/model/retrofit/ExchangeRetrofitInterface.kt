package ir.hajhosseini.payseracurrencyexchanger.model.retrofit

import ir.hajhosseini.payseracurrencyexchanger.model.retrofit.responsemodels.GetRatesResponseModel
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Api queries
 * used by retrofit
 */
interface ExchangeRetrofitInterface {
    @GET("/v1/latest")
    suspend fun getRates(@Query("access_key") accessKey: String , @Query("format") format: String ): GetRatesResponseModel
}