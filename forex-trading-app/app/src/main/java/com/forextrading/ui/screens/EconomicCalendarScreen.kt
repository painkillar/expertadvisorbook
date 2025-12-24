package com.forextrading.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.forextrading.data.model.EconomicEvent
import com.forextrading.data.model.EventImportance
import com.forextrading.ui.theme.*
import com.forextrading.viewmodel.ForexViewModel
import com.forextrading.viewmodel.UiState

@Composable
fun EconomicCalendarScreen(viewModel: ForexViewModel) {
    val calendarState by viewModel.economicCalendar.collectAsState()

    when (calendarState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is UiState.Success -> {
            val calendar = (calendarState as UiState.Success).data
            EventsList(calendar.events)
        }
        is UiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (calendarState as UiState.Error).message,
                    color = BearishRed
                )
            }
        }
    }
}

@Composable
fun EventsList(events: List<EconomicEvent>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(events) { event ->
            EconomicEventCard(event)
        }
    }
}

@Composable
fun EconomicEventCard(event: EconomicEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Importance indicator
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .padding(end = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.fillMaxHeight(),
                    color = when (event.importance) {
                        EventImportance.HIGH -> HighImportance
                        EventImportance.MEDIUM -> MediumImportance
                        EventImportance.LOW -> LowImportance
                    },
                    shape = RoundedCornerShape(2.dp)
                ) {}
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Time and Currency
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = event.time,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        color = when (event.importance) {
                            EventImportance.HIGH -> HighImportance
                            EventImportance.MEDIUM -> MediumImportance
                            EventImportance.LOW -> LowImportance
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = event.currency,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Event name
                Text(
                    text = event.event,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Data: Actual, Forecast, Previous
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Forecast",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = event.forecast ?: "N/A",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(
                            text = "Previous",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = event.previous ?: "N/A",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    if (event.actual != null) {
                        Column {
                            Text(
                                text = "Actual",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = event.actual,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = BullishGreen
                            )
                        }
                    }
                }
            }
        }
    }
}
