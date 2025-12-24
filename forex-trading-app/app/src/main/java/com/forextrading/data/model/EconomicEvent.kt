package com.forextrading.data.model

import com.google.gson.annotations.SerializedName

data class EconomicEvent(
    val id: String,
    val date: String,
    val time: String,
    val currency: String,
    val event: String,
    val importance: EventImportance,
    val actual: String?,
    val forecast: String?,
    val previous: String?,
    val country: String,
    @SerializedName("impact_score")
    val impactScore: Double? = null
)

enum class EventImportance(val displayName: String, val stars: Int) {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3);

    companion object {
        fun fromStars(stars: Int) = when (stars) {
            1 -> LOW
            2 -> MEDIUM
            3 -> HIGH
            else -> MEDIUM
        }
    }
}

data class EconomicCalendar(
    val events: List<EconomicEvent>,
    val lastUpdated: Long
)
