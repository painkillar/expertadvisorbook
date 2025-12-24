package com.forextrading.data.model

import com.google.gson.annotations.SerializedName

data class COTData(
    val currency: String,
    val reportDate: String,
    val timestamp: Long,
    @SerializedName("non_commercial_long")
    val nonCommercialLong: Long,
    @SerializedName("non_commercial_short")
    val nonCommercialShort: Long,
    @SerializedName("commercial_long")
    val commercialLong: Long,
    @SerializedName("commercial_short")
    val commercialShort: Long,
    @SerializedName("total_open_interest")
    val totalOpenInterest: Long,
    val netPositions: Long = nonCommercialLong - nonCommercialShort,
    val weeklyChange: Long = 0,
    val sentiment: COTSentiment = COTSentiment.NEUTRAL
)

enum class COTSentiment {
    BULLISH,
    BEARISH,
    NEUTRAL;

    companion object {
        fun fromNetPosition(netPosition: Long, threshold: Long = 10000): COTSentiment {
            return when {
                netPosition > threshold -> BULLISH
                netPosition < -threshold -> BEARISH
                else -> NEUTRAL
            }
        }
    }
}

data class COTReport(
    val data: List<COTData>,
    val lastUpdated: Long
)
