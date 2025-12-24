package com.forextrading.engine

import com.forextrading.data.model.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * Advanced trading bias recommendation engine for London open
 * Analyzes COT data, economic events, news sentiment, and technical factors
 */
class BiasEngine {

    companion object {
        private const val LONDON_OPEN_HOUR = 8 // 08:00 GMT
        private const val HIGH_CONFIDENCE_THRESHOLD = 0.75
        private const val MEDIUM_CONFIDENCE_THRESHOLD = 0.50
    }

    /**
     * Generate daily bias recommendations for all currency pairs
     */
    fun generateDailyBias(
        quotes: List<PairQuote>,
        cotData: List<COTData>,
        economicEvents: List<EconomicEvent>,
        news: List<NewsItem>
    ): DailyBiasReport {

        val biases = CurrencyPair.values().map { pair ->
            generatePairBias(pair, quotes, cotData, economicEvents, news)
        }

        val marketOverview = generateMarketOverview(cotData, economicEvents, news)

        return DailyBiasReport(
            biases = biases,
            marketOverview = marketOverview,
            generatedAt = System.currentTimeMillis(),
            londonOpenTime = "08:00 GMT"
        )
    }

    /**
     * Generate bias for a specific currency pair
     */
    private fun generatePairBias(
        pair: CurrencyPair,
        quotes: List<PairQuote>,
        cotData: List<COTData>,
        economicEvents: List<EconomicEvent>,
        news: List<NewsItem>
    ): TradingBias {

        val quote = quotes.find { it.pair == pair }

        // Analyze fundamental factors
        val fundamentalFactors = analyzeFundamentalFactors(pair, cotData, economicEvents, news)

        // Analyze technical factors
        val technicalFactors = analyzeTechnicalFactors(pair, quote)

        // Calculate overall bias and confidence
        val (bias, confidence, reasoning) = calculateBias(
            pair, fundamentalFactors, technicalFactors, economicEvents
        )

        // Generate trade setup
        val (entryZone, stopLoss, takeProfit) = generateTradeSetup(
            pair, quote, bias, technicalFactors
        )

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        return TradingBias(
            pair = pair,
            date = sdf.format(Date()),
            londonOpenTime = "08:00 GMT",
            bias = bias,
            confidence = confidence,
            reasoning = reasoning,
            technicalFactors = technicalFactors,
            fundamentalFactors = fundamentalFactors,
            entryZone = entryZone,
            stopLoss = stopLoss,
            takeProfit = takeProfit
        )
    }

    /**
     * Analyze fundamental factors: COT, news, economic events
     */
    private fun analyzeFundamentalFactors(
        pair: CurrencyPair,
        cotData: List<COTData>,
        economicEvents: List<EconomicEvent>,
        news: List<NewsItem>
    ): FundamentalFactors {

        // Get base and quote currency from pair
        val (baseCurrency, quoteCurrency) = getCurrenciesFromPair(pair)

        // COT sentiment
        val baseCOT = cotData.find { it.currency == baseCurrency }
        val quoteCOT = cotData.find { it.currency == quoteCurrency }
        val cotSentiment = determineCOTSentiment(baseCOT, quoteCOT)

        // News sentiment
        val relevantNews = news.filter {
            it.affectedCurrencies.contains(baseCurrency) ||
            it.affectedCurrencies.contains(quoteCurrency)
        }
        val newsImpact = determineNewsSentiment(relevantNews, baseCurrency, quoteCurrency)

        // Economic events
        val upcomingEvents = economicEvents.filter {
            it.currency == baseCurrency || it.currency == quoteCurrency
        }.map { "${it.currency}: ${it.event} (${it.importance.displayName})" }

        // Risk sentiment
        val riskSentiment = determineRiskSentiment(news)

        // Interest rate differential (simplified)
        val interestRateDifferential = calculateInterestRateDifferential(baseCurrency, quoteCurrency)

        return FundamentalFactors(
            cotSentiment = cotSentiment,
            newsImpact = newsImpact,
            economicEvents = upcomingEvents,
            interestRateDifferential = interestRateDifferential,
            riskSentiment = riskSentiment
        )
    }

    /**
     * Analyze technical factors
     */
    private fun analyzeTechnicalFactors(
        pair: CurrencyPair,
        quote: PairQuote?
    ): TechnicalFactors {

        if (quote == null) {
            return TechnicalFactors(
                trend = "Unknown",
                support = null,
                resistance = null,
                keyLevels = emptyList(),
                momentum = "Unknown",
                volatility = VolatilityLevel.MODERATE
            )
        }

        val midPrice = (quote.bid + quote.ask) / 2
        val dailyRange = quote.dailyHigh - quote.dailyLow
        val atr = dailyRange // Simplified ATR

        // Determine trend based on price position
        val trend = when {
            quote.dailyChangePercent > 0.3 -> "Strong Uptrend"
            quote.dailyChangePercent > 0.1 -> "Uptrend"
            quote.dailyChangePercent < -0.3 -> "Strong Downtrend"
            quote.dailyChangePercent < -0.1 -> "Downtrend"
            else -> "Sideways"
        }

        // Calculate key levels
        val support = quote.dailyLow
        val resistance = quote.dailyHigh
        val pivotPoint = (quote.dailyHigh + quote.dailyLow + midPrice) / 3

        val keyLevels = listOf(
            support,
            pivotPoint - atr,
            pivotPoint,
            pivotPoint + atr,
            resistance
        ).sorted()

        // Momentum
        val momentum = when {
            quote.dailyChangePercent > 0.5 -> "Strong Bullish"
            quote.dailyChangePercent > 0.2 -> "Bullish"
            quote.dailyChangePercent < -0.5 -> "Strong Bearish"
            quote.dailyChangePercent < -0.2 -> "Bearish"
            else -> "Neutral"
        }

        // Volatility
        val avgPrice = (quote.dailyHigh + quote.dailyLow) / 2
        val volatilityPercent = (dailyRange / avgPrice) * 100
        val volatility = when {
            volatilityPercent > 2.0 -> VolatilityLevel.EXTREME
            volatilityPercent > 1.0 -> VolatilityLevel.HIGH
            volatilityPercent > 0.5 -> VolatilityLevel.MODERATE
            else -> VolatilityLevel.LOW
        }

        return TechnicalFactors(
            trend = trend,
            support = support,
            resistance = resistance,
            keyLevels = keyLevels,
            momentum = momentum,
            volatility = volatility
        )
    }

    /**
     * Calculate overall bias and confidence based on all factors
     */
    private fun calculateBias(
        pair: CurrencyPair,
        fundamentalFactors: FundamentalFactors,
        technicalFactors: TechnicalFactors,
        economicEvents: List<EconomicEvent>
    ): Triple<Bias, Double, List<String>> {

        var score = 0.0
        val reasoning = mutableListOf<String>()

        // COT analysis (weight: 30%)
        when (fundamentalFactors.cotSentiment) {
            COTSentiment.BULLISH -> {
                score += 0.3
                reasoning.add("COT data shows institutional long positioning")
            }
            COTSentiment.BEARISH -> {
                score -= 0.3
                reasoning.add("COT data shows institutional short positioning")
            }
            COTSentiment.NEUTRAL -> {
                reasoning.add("COT positioning is neutral")
            }
        }

        // News sentiment (weight: 25%)
        when (fundamentalFactors.newsImpact) {
            NewsSentiment.BULLISH -> {
                score += 0.25
                reasoning.add("News sentiment is positive for ${pair.displayName}")
            }
            NewsSentiment.BEARISH -> {
                score -= 0.25
                reasoning.add("News sentiment is negative for ${pair.displayName}")
            }
            NewsSentiment.NEUTRAL -> {}
        }

        // Technical trend (weight: 25%)
        when {
            technicalFactors.trend.contains("Strong Uptrend", ignoreCase = true) -> {
                score += 0.25
                reasoning.add("Strong bullish technical trend")
            }
            technicalFactors.trend.contains("Uptrend", ignoreCase = true) -> {
                score += 0.15
                reasoning.add("Bullish technical trend")
            }
            technicalFactors.trend.contains("Strong Downtrend", ignoreCase = true) -> {
                score -= 0.25
                reasoning.add("Strong bearish technical trend")
            }
            technicalFactors.trend.contains("Downtrend", ignoreCase = true) -> {
                score -= 0.15
                reasoning.add("Bearish technical trend")
            }
            else -> {
                reasoning.add("Price is ranging")
            }
        }

        // Economic events (weight: 20%)
        val highImpactEvents = fundamentalFactors.economicEvents.filter { it.contains("High") }
        if (highImpactEvents.isNotEmpty()) {
            reasoning.add("High-impact events today: ${highImpactEvents.size}")
            // Reduce confidence due to event risk
            score *= 0.85
        }

        // Risk sentiment
        when (fundamentalFactors.riskSentiment) {
            "Risk On" -> {
                if (isRiskCurrency(pair)) {
                    reasoning.add("Risk-on sentiment favors ${pair.displayName}")
                    score += 0.1
                } else {
                    reasoning.add("Risk-on sentiment may pressure ${pair.displayName}")
                    score -= 0.1
                }
            }
            "Risk Off" -> {
                if (isSafeCurrency(pair)) {
                    reasoning.add("Risk-off sentiment favors safe-haven currencies")
                    score += 0.1
                } else {
                    reasoning.add("Risk-off sentiment pressures risk currencies")
                    score -= 0.1
                }
            }
        }

        // Determine bias
        val bias = when {
            abs(score) < 0.15 -> Bias.RANGE_BOUND
            score > 0.4 -> Bias.BULLISH
            score < -0.4 -> Bias.BEARISH
            score > 0 -> Bias.BULLISH
            score < 0 -> Bias.BEARISH
            else -> Bias.NEUTRAL
        }

        // Calculate confidence (0-1 scale)
        val confidence = (abs(score).coerceIn(0.0, 1.0) * 100).toInt() / 100.0

        // Add volatility warning
        if (technicalFactors.volatility == VolatilityLevel.EXTREME) {
            reasoning.add("⚠️ Extreme volatility - use smaller position sizes")
        } else if (technicalFactors.volatility == VolatilityLevel.HIGH) {
            reasoning.add("High volatility expected")
        }

        return Triple(bias, confidence, reasoning)
    }

    /**
     * Generate trade setup with entry, stop loss, and take profit levels
     */
    private fun generateTradeSetup(
        pair: CurrencyPair,
        quote: PairQuote?,
        bias: Bias,
        technicalFactors: TechnicalFactors
    ): Triple<PriceRange?, Double?, List<Double>> {

        if (quote == null || bias == Bias.NEUTRAL || bias == Bias.RANGE_BOUND) {
            return Triple(null, null, emptyList())
        }

        val currentPrice = (quote.bid + quote.ask) / 2
        val atr = quote.dailyHigh - quote.dailyLow

        val entryZone: PriceRange
        val stopLoss: Double
        val takeProfit: List<Double>

        when (bias) {
            Bias.BULLISH -> {
                // Entry on pullback
                entryZone = PriceRange(
                    low = currentPrice - (atr * 0.3),
                    high = currentPrice + (atr * 0.1)
                )
                // Stop below support
                stopLoss = (technicalFactors.support ?: currentPrice) - (atr * 0.5)
                // Multiple targets
                takeProfit = listOf(
                    currentPrice + atr,
                    currentPrice + (atr * 2),
                    technicalFactors.resistance ?: (currentPrice + (atr * 3))
                )
            }
            Bias.BEARISH -> {
                // Entry on pullback
                entryZone = PriceRange(
                    low = currentPrice - (atr * 0.1),
                    high = currentPrice + (atr * 0.3)
                )
                // Stop above resistance
                stopLoss = (technicalFactors.resistance ?: currentPrice) + (atr * 0.5)
                // Multiple targets
                takeProfit = listOf(
                    currentPrice - atr,
                    currentPrice - (atr * 2),
                    technicalFactors.support ?: (currentPrice - (atr * 3))
                )
            }
            else -> {
                return Triple(null, null, emptyList())
            }
        }

        return Triple(entryZone, stopLoss, takeProfit)
    }

    private fun getCurrenciesFromPair(pair: CurrencyPair): Pair<String, String> {
        val symbol = pair.symbol
        return when {
            symbol.length == 6 -> Pair(symbol.substring(0, 3), symbol.substring(3, 6))
            else -> Pair("", "")
        }
    }

    private fun determineCOTSentiment(baseCOT: COTData?, quoteCOT: COTData?): COTSentiment {
        if (baseCOT == null && quoteCOT == null) return COTSentiment.NEUTRAL

        val baseScore = when (baseCOT?.sentiment) {
            COTSentiment.BULLISH -> 1
            COTSentiment.BEARISH -> -1
            else -> 0
        }

        val quoteScore = when (quoteCOT?.sentiment) {
            COTSentiment.BULLISH -> -1 // Bullish quote = bearish for pair
            COTSentiment.BEARISH -> 1  // Bearish quote = bullish for pair
            else -> 0
        }

        val totalScore = baseScore + quoteScore
        return when {
            totalScore > 0 -> COTSentiment.BULLISH
            totalScore < 0 -> COTSentiment.BEARISH
            else -> COTSentiment.NEUTRAL
        }
    }

    private fun determineNewsSentiment(
        news: List<NewsItem>,
        baseCurrency: String,
        quoteCurrency: String
    ): NewsSentiment {
        if (news.isEmpty()) return NewsSentiment.NEUTRAL

        var score = 0
        news.forEach { item ->
            when (item.sentiment) {
                NewsSentiment.BULLISH -> {
                    if (item.affectedCurrencies.contains(baseCurrency)) score++
                    if (item.affectedCurrencies.contains(quoteCurrency)) score--
                }
                NewsSentiment.BEARISH -> {
                    if (item.affectedCurrencies.contains(baseCurrency)) score--
                    if (item.affectedCurrencies.contains(quoteCurrency)) score++
                }
                NewsSentiment.NEUTRAL -> {}
            }
        }

        return when {
            score > 0 -> NewsSentiment.BULLISH
            score < 0 -> NewsSentiment.BEARISH
            else -> NewsSentiment.NEUTRAL
        }
    }

    private fun determineRiskSentiment(news: List<NewsItem>): String {
        val geopoliticalNews = news.filter { it.category == NewsCategory.GEOPOLITICAL }
        if (geopoliticalNews.any { it.sentiment == NewsSentiment.BEARISH }) {
            return "Risk Off"
        }

        val bullishCount = news.count { it.sentiment == NewsSentiment.BULLISH }
        val bearishCount = news.count { it.sentiment == NewsSentiment.BEARISH }

        return when {
            bullishCount > bearishCount * 1.5 -> "Risk On"
            bearishCount > bullishCount * 1.5 -> "Risk Off"
            else -> "Mixed"
        }
    }

    private fun isRiskCurrency(pair: CurrencyPair): Boolean {
        val riskCurrencies = listOf("AUD", "NZD", "CAD")
        val symbol = pair.symbol
        return riskCurrencies.any { symbol.startsWith(it) }
    }

    private fun isSafeCurrency(pair: CurrencyPair): Boolean {
        val safeCurrencies = listOf("JPY", "CHF", "USD")
        val symbol = pair.symbol
        return safeCurrencies.any { symbol.contains(it) }
    }

    private fun calculateInterestRateDifferential(base: String, quote: String): Double {
        // Simplified interest rate map (would be updated from real data)
        val rates = mapOf(
            "USD" to 5.25,
            "EUR" to 4.50,
            "GBP" to 5.00,
            "JPY" to -0.10,
            "CHF" to 1.75,
            "AUD" to 4.35,
            "NZD" to 5.50,
            "CAD" to 5.00
        )

        val baseRate = rates[base] ?: 0.0
        val quoteRate = rates[quote] ?: 0.0
        return baseRate - quoteRate
    }

    private fun generateMarketOverview(
        cotData: List<COTData>,
        economicEvents: List<EconomicEvent>,
        news: List<NewsItem>
    ): String {
        val bullishCOT = cotData.count { it.sentiment == COTSentiment.BULLISH }
        val bearishCOT = cotData.count { it.sentiment == COTSentiment.BEARISH }

        val highImpactEvents = economicEvents.filter { it.importance == EventImportance.HIGH }

        val bullishNews = news.count { it.sentiment == NewsSentiment.BULLISH }
        val bearishNews = news.count { it.sentiment == NewsSentiment.BEARISH }

        return buildString {
            appendLine("📊 Market Overview for London Open (08:00 GMT)")
            appendLine()
            appendLine("COT Positioning: $bullishCOT currencies bullish, $bearishCOT bearish")
            appendLine("High-Impact Events Today: ${highImpactEvents.size}")
            if (highImpactEvents.isNotEmpty()) {
                highImpactEvents.forEach {
                    appendLine("  • ${it.currency}: ${it.event} at ${it.time}")
                }
            }
            appendLine()
            appendLine("News Sentiment: $bullishNews positive, $bearishNews negative articles")
            appendLine()
            appendLine("⚠️ Always use proper risk management and position sizing")
        }
    }
}
