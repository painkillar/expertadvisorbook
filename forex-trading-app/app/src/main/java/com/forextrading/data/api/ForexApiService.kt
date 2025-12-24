package com.forextrading.data.api

import com.forextrading.data.model.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ForexApiService {

    @GET("economic-calendar")
    suspend fun getEconomicCalendar(
        @Query("from") fromDate: String,
        @Query("to") toDate: String,
        @Query("currencies") currencies: String? = null
    ): Response<EconomicCalendar>

    @GET("news")
    suspend fun getNews(
        @Query("limit") limit: Int = 50,
        @Query("currencies") currencies: String? = null
    ): Response<NewsFeed>

    @GET("cot-report")
    suspend fun getCOTReport(
        @Query("currencies") currencies: String? = null
    ): Response<COTReport>

    @GET("quotes")
    suspend fun getQuotes(
        @Query("pairs") pairs: String
    ): Response<List<PairQuote>>

    @GET("daily-bias")
    suspend fun getDailyBias(): Response<DailyBiasReport>
}
