package ir.hajhosseini.payseracurrencyexchanger.model.retrofit.responsemodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GetRatesResponseModel(
    @SerializedName("success")
    @Expose
    val success: Boolean,
    @SerializedName("timestamp")
    @Expose
    val timestamp: Long,
    @SerializedName("base")
    @Expose
    val base: String,
    @SerializedName("date")
    @Expose
    val date: String,
    @SerializedName("rates")
    @Expose
    val rates: Any,

)