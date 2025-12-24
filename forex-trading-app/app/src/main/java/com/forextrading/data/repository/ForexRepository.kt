package com.forextrading.data.repository

import com.forextrading.data.api.MockDataProvider
import com.forextrading.data.api.RetrofitClient
import com.forextrading.data.model.*
import com.forextrading.engine.BiasEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repository for managing forex data
 * Uses mock data by default - replace with real API calls when integrating
 */
class ForexRepository {

    private val apiService = RetrofitClient.apiService
    private val biasEngine = BiasEngine()
    private val useMockData = true // Set to false when using real API

    suspend fun getEconomicCalendar(): Result<EconomicCalendar> = withContext(Dispatchers.IO) {
        try {
            if (useMockData) {
                val events = MockDataProvider.getMockEconomicEvents()
                Result.success(
                    EconomicCalendar(
                        events = events,
                        lastUpdated = System.currentTimeMillis()
                    )
                )
            } else {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val today = sdf.format(Date())
                val tomorrow = sdf.format(Date(System.currentTimeMillis() + 86400000))

                val response = apiService.getEconomicCalendar(today, tomorrow)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch economic calendar"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNews(): Result<NewsFeed> = withContext(Dispatchers.IO) {
        try {
            if (useMockData) {
                val news = MockDataProvider.getMockNews()
                Result.success(
                    NewsFeed(
                        items = news,
                        lastUpdated = System.currentTimeMillis()
                    )
                )
            } else {
                val response = apiService.getNews(limit = 50)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch news"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCOTReport(): Result<COTReport> = withContext(Dispatchers.IO) {
        try {
            if (useMockData) {
                val cotData = MockDataProvider.getMockCOTData()
                Result.success(
                    COTReport(
                        data = cotData,
                        lastUpdated = System.currentTimeMillis()
                    )
                )
            } else {
                val response = apiService.getCOTReport()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch COT report"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getQuotes(): Result<List<PairQuote>> = withContext(Dispatchers.IO) {
        try {
            if (useMockData) {
                val quotes = MockDataProvider.getMockQuotes()
                Result.success(quotes)
            } else {
                val pairs = CurrencyPair.values().joinToString(",") { it.symbol }
                val response = apiService.getQuotes(pairs)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch quotes"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDailyBias(): Result<DailyBiasReport> = withContext(Dispatchers.IO) {
        try {
            // Fetch all required data
            val quotesResult = getQuotes()
            val cotResult = getCOTReport()
            val calendarResult = getEconomicCalendar()
            val newsResult = getNews()

            // Check if all data was fetched successfully
            if (quotesResult.isFailure || cotResult.isFailure ||
                calendarResult.isFailure || newsResult.isFailure) {
                return@withContext Result.failure(
                    Exception("Failed to fetch required data for bias analysis")
                )
            }

            val quotes = quotesResult.getOrNull() ?: emptyList()
            val cotData = cotResult.getOrNull()?.data ?: emptyList()
            val events = calendarResult.getOrNull()?.events ?: emptyList()
            val news = newsResult.getOrNull()?.items ?: emptyList()

            // Generate bias using the engine
            val biasReport = biasEngine.generateDailyBias(quotes, cotData, events, news)

            Result.success(biasReport)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getQuoteForPair(pair: CurrencyPair): Result<PairQuote?> = withContext(Dispatchers.IO) {
        try {
            val quotesResult = getQuotes()
            if (quotesResult.isSuccess) {
                val quote = quotesResult.getOrNull()?.find { it.pair == pair }
                Result.success(quote)
            } else {
                Result.failure(quotesResult.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
