package com.forextrading.data.model

import com.google.gson.annotations.SerializedName

data class NewsItem(
    val id: String,
    val title: String,
    val description: String?,
    val source: String,
    val url: String?,
    @SerializedName("published_at")
    val publishedAt: String,
    val timestamp: Long,
    val sentiment: NewsSentiment = NewsSentiment.NEUTRAL,
    val affectedCurrencies: List<String> = emptyList(),
    val category: NewsCategory = NewsCategory.GENERAL
)

enum class NewsSentiment {
    BULLISH,
    BEARISH,
    NEUTRAL
}

enum class NewsCategory {
    CENTRAL_BANK,
    ECONOMIC_DATA,
    GEOPOLITICAL,
    MARKET_ANALYSIS,
    GENERAL
}

data class NewsFeed(
    val items: List<NewsItem>,
    val lastUpdated: Long
)
