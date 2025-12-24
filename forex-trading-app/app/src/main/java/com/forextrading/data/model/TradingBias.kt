package com.forextrading.data.model

data class TradingBias(
    val pair: CurrencyPair,
    val date: String,
    val londonOpenTime: String = "08:00 GMT",
    val bias: Bias,
    val confidence: Double, // 0.0 to 1.0
    val reasoning: List<String>,
    val technicalFactors: TechnicalFactors,
    val fundamentalFactors: FundamentalFactors,
    val entryZone: PriceRange?,
    val stopLoss: Double?,
    val takeProfit: List<Double> = emptyList()
)

enum class Bias(val displayName: String) {
    BULLISH("Bullish"),
    BEARISH("Bearish"),
    NEUTRAL("Neutral"),
    RANGE_BOUND("Range Bound");
}

data class TechnicalFactors(
    val trend: String, // "Uptrend", "Downtrend", "Sideways"
    val support: Double?,
    val resistance: Double?,
    val keyLevels: List<Double>,
    val momentum: String, // "Strong Bullish", "Weak Bullish", etc.
    val volatility: VolatilityLevel
)

enum class VolatilityLevel {
    LOW,
    MODERATE,
    HIGH,
    EXTREME
}

data class FundamentalFactors(
    val cotSentiment: COTSentiment,
    val newsImpact: NewsSentiment,
    val economicEvents: List<String>,
    val interestRateDifferential: Double?,
    val riskSentiment: String // "Risk On", "Risk Off", "Mixed"
)

data class PriceRange(
    val low: Double,
    val high: Double
)

data class DailyBiasReport(
    val biases: List<TradingBias>,
    val marketOverview: String,
    val generatedAt: Long,
    val londonOpenTime: String = "08:00 GMT"
)
