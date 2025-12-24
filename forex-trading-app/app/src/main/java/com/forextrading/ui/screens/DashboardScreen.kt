package com.forextrading.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.forextrading.data.model.CurrencyPair
import com.forextrading.data.model.PairQuote
import com.forextrading.ui.theme.BearishRed
import com.forextrading.ui.theme.BullishGreen
import com.forextrading.ui.theme.CardDark
import com.forextrading.viewmodel.ForexViewModel
import com.forextrading.viewmodel.UiState

@Composable
fun DashboardScreen(viewModel: ForexViewModel) {
    val quotesState by viewModel.quotes.collectAsState()

    when (quotesState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is UiState.Success -> {
            val quotes = (quotesState as UiState.Success<List<PairQuote>>).data
            QuotesList(quotes)
        }
        is UiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (quotesState as UiState.Error).message,
                    color = BearishRed
                )
            }
        }
    }
}

@Composable
fun QuotesList(quotes: List<PairQuote>) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Major Pairs Section
        Text(
            text = "Major Currency Pairs",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val majorPairs = quotes.filter { !it.pair.isYenCross }
            items(majorPairs) { quote ->
                QuoteCard(quote)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Yen Crosses",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            val yenCrosses = quotes.filter { it.pair.isYenCross }
            items(yenCrosses) { quote ->
                QuoteCard(quote)
            }
        }
    }
}

@Composable
fun QuoteCard(quote: PairQuote) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quote.pair.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = "Bid: ${String.format("%.5f", quote.bid)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Ask: ${String.format("%.5f", quote.ask)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                val changeColor = if (quote.dailyChangePercent >= 0) BullishGreen else BearishRed
                val changeSign = if (quote.dailyChangePercent >= 0) "+" else ""

                Text(
                    text = "$changeSign${String.format("%.2f%%", quote.dailyChangePercent)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = changeColor
                )
                Text(
                    text = "$changeSign${String.format("%.5f", quote.dailyChange)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = changeColor
                )
            }
        }
    }
}
