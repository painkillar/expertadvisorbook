package com.forextrading.data.model

enum class CurrencyPair(val symbol: String, val displayName: String, val isYenCross: Boolean = false) {
    // Major Pairs
    EUR_USD("EURUSD", "EUR/USD"),
    GBP_USD("GBPUSD", "GBP/USD"),
    USD_JPY("USDJPY", "USD/JPY"),
    USD_CHF("USDCHF", "USD/CHF"),
    AUD_USD("AUDUSD", "AUD/USD"),
    NZD_USD("NZDUSD", "NZD/USD"),
    USD_CAD("USDCAD", "USD/CAD"),

    // Yen Crosses
    EUR_JPY("EURJPY", "EUR/JPY", true),
    GBP_JPY("GBPJPY", "GBP/JPY", true),
    AUD_JPY("AUDJPY", "AUD/JPY", true),
    NZD_JPY("NZDJPY", "NZD/JPY", true),
    CHF_JPY("CHFJPY", "CHF/JPY", true),
    CAD_JPY("CADJPY", "CAD/JPY", true);

    companion object {
        fun getMajorPairs() = values().filter { !it.isYenCross }
        fun getYenCrosses() = values().filter { it.isYenCross }
        fun fromSymbol(symbol: String) = values().find { it.symbol == symbol }
    }
}

data class PairQuote(
    val pair: CurrencyPair,
    val bid: Double,
    val ask: Double,
    val timestamp: Long,
    val dailyHigh: Double,
    val dailyLow: Double,
    val dailyChange: Double,
    val dailyChangePercent: Double
)
