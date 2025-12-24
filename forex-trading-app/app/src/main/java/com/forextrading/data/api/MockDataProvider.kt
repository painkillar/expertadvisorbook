package com.forextrading.data.api

import com.forextrading.data.model.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Mock data provider for development and testing
 * Replace with real API calls when integrating with actual forex data providers
 */
object MockDataProvider {

    fun getMockEconomicEvents(): List<EconomicEvent> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val today = sdf.format(Date())

        return listOf(
            EconomicEvent(
                id = "1",
                date = today,
                time = "08:30",
                currency = "USD",
                event = "Non-Farm Payrolls",
                importance = EventImportance.HIGH,
                actual = null,
                forecast = "200K",
                previous = "185K",
                country = "United States",
                impactScore = 9.5
            ),
            EconomicEvent(
                id = "2",
                date = today,
                time = "09:00",
                currency = "EUR",
                event = "ECB Interest Rate Decision",
                importance = EventImportance.HIGH,
                actual = null,
                forecast = "4.50%",
                previous = "4.50%",
                country = "Eurozone",
                impactScore = 9.0
            ),
            EconomicEvent(
                id = "3",
                date = today,
                time = "12:30",
                currency = "GBP",
                event = "GDP Growth Rate",
                importance = EventImportance.MEDIUM,
                actual = null,
                forecast = "0.3%",
                previous = "0.2%",
                country = "United Kingdom",
                impactScore = 7.5
            ),
            EconomicEvent(
                id = "4",
                date = today,
                time = "05:00",
                currency = "AUD",
                event = "Employment Change",
                importance = EventImportance.MEDIUM,
                actual = "15.9K",
                forecast = "20.0K",
                previous = "12.5K",
                country = "Australia",
                impactScore = 6.8
            ),
            EconomicEvent(
                id = "5",
                date = today,
                time = "13:00",
                currency = "CAD",
                event = "Bank of Canada Rate Statement",
                importance = EventImportance.HIGH,
                actual = null,
                forecast = null,
                previous = "5.00%",
                country = "Canada",
                impactScore = 8.2
            )
        )
    }

    fun getMockNews(): List<NewsItem> {
        val timestamp = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)

        return listOf(
            NewsItem(
                id = "n1",
                title = "Federal Reserve Signals Potential Rate Cuts in Q2",
                description = "Fed Chairman indicated that inflation is cooling and rate cuts may be appropriate in the coming months.",
                source = "Reuters",
                url = "https://reuters.com/markets/forex",
                publishedAt = sdf.format(Date(timestamp - 3600000)),
                timestamp = timestamp - 3600000,
                sentiment = NewsSentiment.BULLISH,
                affectedCurrencies = listOf("USD"),
                category = NewsCategory.CENTRAL_BANK
            ),
            NewsItem(
                id = "n2",
                title = "EUR/USD Reaches 2-Month High on Strong Eurozone Data",
                description = "The euro rallied against the dollar following better-than-expected manufacturing PMI data.",
                source = "Bloomberg",
                url = "https://bloomberg.com/forex",
                publishedAt = sdf.format(Date(timestamp - 7200000)),
                timestamp = timestamp - 7200000,
                sentiment = NewsSentiment.BULLISH,
                affectedCurrencies = listOf("EUR", "USD"),
                category = NewsCategory.ECONOMIC_DATA
            ),
            NewsItem(
                id = "n3",
                title = "Bank of Japan Maintains Ultra-Loose Monetary Policy",
                description = "BoJ keeps interest rates unchanged at -0.1%, continuing its dovish stance.",
                source = "Financial Times",
                url = "https://ft.com/currencies",
                publishedAt = sdf.format(Date(timestamp - 10800000)),
                timestamp = timestamp - 10800000,
                sentiment = NewsSentiment.BEARISH,
                affectedCurrencies = listOf("JPY"),
                category = NewsCategory.CENTRAL_BANK
            ),
            NewsItem(
                id = "n4",
                title = "GBP Volatility Expected Ahead of GDP Release",
                description = "Traders are positioning for potential sterling movement as UK economic data looms.",
                source = "CNBC",
                url = "https://cnbc.com/forex",
                publishedAt = sdf.format(Date(timestamp - 14400000)),
                timestamp = timestamp - 14400000,
                sentiment = NewsSentiment.NEUTRAL,
                affectedCurrencies = listOf("GBP"),
                category = NewsCategory.MARKET_ANALYSIS
            ),
            NewsItem(
                id = "n5",
                title = "Australian Dollar Climbs on Strong Employment Data",
                description = "AUD/USD surges after Australia reports robust job growth exceeding forecasts.",
                source = "MarketWatch",
                url = "https://marketwatch.com/forex",
                publishedAt = sdf.format(Date(timestamp - 18000000)),
                timestamp = timestamp - 18000000,
                sentiment = NewsSentiment.BULLISH,
                affectedCurrencies = listOf("AUD"),
                category = NewsCategory.ECONOMIC_DATA
            )
        )
    }

    fun getMockCOTData(): List<COTData> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val reportDate = sdf.format(Date(System.currentTimeMillis() - 86400000 * 3))

        return listOf(
            COTData(
                currency = "EUR",
                reportDate = reportDate,
                timestamp = System.currentTimeMillis(),
                nonCommercialLong = 250000,
                nonCommercialShort = 180000,
                commercialLong = 150000,
                commercialShort = 220000,
                totalOpenInterest = 800000,
                netPositions = 70000,
                weeklyChange = 15000,
                sentiment = COTSentiment.BULLISH
            ),
            COTData(
                currency = "GBP",
                reportDate = reportDate,
                timestamp = System.currentTimeMillis(),
                nonCommercialLong = 180000,
                nonCommercialShort = 200000,
                commercialLong = 120000,
                commercialShort = 100000,
                totalOpenInterest = 600000,
                netPositions = -20000,
                weeklyChange = -5000,
                sentiment = COTSentiment.BEARISH
            ),
            COTData(
                currency = "JPY",
                reportDate = reportDate,
                timestamp = System.currentTimeMillis(),
                nonCommercialLong = 100000,
                nonCommercialShort = 150000,
                commercialLong = 200000,
                commercialShort = 150000,
                totalOpenInterest = 600000,
                netPositions = -50000,
                weeklyChange = -10000,
                sentiment = COTSentiment.BEARISH
            ),
            COTData(
                currency = "CHF",
                reportDate = reportDate,
                timestamp = System.currentTimeMillis(),
                nonCommercialLong = 80000,
                nonCommercialShort = 75000,
                commercialLong = 90000,
                commercialShort = 95000,
                totalOpenInterest = 340000,
                netPositions = 5000,
                weeklyChange = 2000,
                sentiment = COTSentiment.NEUTRAL
            ),
            COTData(
                currency = "AUD",
                reportDate = reportDate,
                timestamp = System.currentTimeMillis(),
                nonCommercialLong = 120000,
                nonCommercialShort = 80000,
                commercialLong = 70000,
                commercialShort = 110000,
                totalOpenInterest = 380000,
                netPositions = 40000,
                weeklyChange = 8000,
                sentiment = COTSentiment.BULLISH
            ),
            COTData(
                currency = "NZD",
                reportDate = reportDate,
                timestamp = System.currentTimeMillis(),
                nonCommercialLong = 60000,
                nonCommercialShort = 55000,
                commercialLong = 40000,
                commercialShort = 45000,
                totalOpenInterest = 200000,
                netPositions = 5000,
                weeklyChange = 1000,
                sentiment = COTSentiment.NEUTRAL
            ),
            COTData(
                currency = "CAD",
                reportDate = reportDate,
                timestamp = System.currentTimeMillis(),
                nonCommercialLong = 90000,
                nonCommercialShort = 110000,
                commercialLong = 80000,
                commercialShort = 60000,
                totalOpenInterest = 340000,
                netPositions = -20000,
                weeklyChange = -3000,
                sentiment = COTSentiment.BEARISH
            )
        )
    }

    fun getMockQuotes(): List<PairQuote> {
        return CurrencyPair.values().map { pair ->
            val basePrice = when (pair) {
                CurrencyPair.EUR_USD -> 1.0850
                CurrencyPair.GBP_USD -> 1.2650
                CurrencyPair.USD_JPY -> 149.50
                CurrencyPair.USD_CHF -> 0.8750
                CurrencyPair.AUD_USD -> 0.6580
                CurrencyPair.NZD_USD -> 0.6120
                CurrencyPair.USD_CAD -> 1.3520
                CurrencyPair.EUR_JPY -> 162.20
                CurrencyPair.GBP_JPY -> 189.10
                CurrencyPair.AUD_JPY -> 98.35
                CurrencyPair.NZD_JPY -> 91.50
                CurrencyPair.CHF_JPY -> 170.85
                CurrencyPair.CAD_JPY -> 110.60
            }

            val spread = basePrice * 0.0001
            val dailyChange = basePrice * (Math.random() * 0.02 - 0.01)

            PairQuote(
                pair = pair,
                bid = basePrice - spread / 2,
                ask = basePrice + spread / 2,
                timestamp = System.currentTimeMillis(),
                dailyHigh = basePrice + Math.abs(dailyChange),
                dailyLow = basePrice - Math.abs(dailyChange),
                dailyChange = dailyChange,
                dailyChangePercent = (dailyChange / basePrice) * 100
            )
        }
    }
}
