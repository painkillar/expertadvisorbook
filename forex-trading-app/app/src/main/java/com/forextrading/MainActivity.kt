package com.forextrading

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.forextrading.ui.theme.ForexTradingAppTheme
import com.forextrading.ui.theme.BullishGreen
import com.forextrading.ui.theme.BearishRed

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ForexTradingAppTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    ForexTradingApp()
                }
            }
        }
    }
}

@Composable
fun ForexTradingApp() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Dashboard", "Daily Bias", "Calendar", "News", "COT")
    val icons = listOf(
        Icons.Default.Dashboard,
        Icons.Default.TrendingUp,
        Icons.Default.CalendarToday,
        Icons.Default.Article,
        Icons.Default.Assessment
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Forex Trading") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = title) },
                        label = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> DashboardTab()
                1 -> DailyBiasTab()
                2 -> CalendarTab()
                3 -> NewsTab()
                4 -> COTTab()
            }
        }
    }
}

@Composable
fun DashboardTab() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Major Currency Pairs",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        CurrencyPairCard("EUR/USD", "1.0850", "+0.25%", true)
        CurrencyPairCard("GBP/USD", "1.2650", "-0.15%", false)
        CurrencyPairCard("USD/JPY", "149.50", "+0.35%", true)
        CurrencyPairCard("AUD/USD", "0.6580", "-0.10%", false)

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Yen Crosses",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        CurrencyPairCard("EUR/JPY", "162.20", "+0.45%", true)
        CurrencyPairCard("GBP/JPY", "189.10", "+0.20%", true)
    }
}

@Composable
fun DailyBiasTab() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "London Open Bias (08:00 GMT)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        BiasCard("EUR/USD", "Bullish", 75, "COT: Institutional long positioning\nTechnical: Strong uptrend\nNews: Positive EU data")
        BiasCard("GBP/USD", "Bearish", 60, "COT: Net short positioning\nTechnical: Approaching resistance\nNews: Mixed UK data")

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "⚠️ Always use proper risk management",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun CalendarTab() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Today's Economic Events",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        EconomicEventCard("08:30", "USD", "Non-Farm Payrolls", "High")
        EconomicEventCard("09:00", "EUR", "ECB Rate Decision", "High")
        EconomicEventCard("12:30", "GBP", "GDP Growth Rate", "Medium")
    }
}

@Composable
fun NewsTab() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Forex News",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        NewsCard("Fed Signals Rate Cuts", "Bullish USD", "2h ago")
        NewsCard("EUR Reaches 2-Month High", "Bullish EUR", "4h ago")
        NewsCard("BoJ Maintains Policy", "Bearish JPY", "6h ago")
    }
}

@Composable
fun COTTab() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "COT Report",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Institutional Positioning",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(16.dp))

        COTCard("EUR", "Bullish", "+70,000")
        COTCard("GBP", "Bearish", "-20,000")
        COTCard("JPY", "Bearish", "-50,000")
    }
}

@Composable
fun CurrencyPairCard(pair: String, price: String, change: String, isPositive: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(pair, fontWeight = FontWeight.Bold)
                Text(price, style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                change,
                color = if (isPositive) BullishGreen else BearishRed,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BiasCard(pair: String, bias: String, confidence: Int, reasoning: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(pair, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    bias,
                    color = if (bias == "Bullish") BullishGreen else BearishRed,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Confidence: $confidence%", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(reasoning, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun EconomicEventCard(time: String, currency: String, event: String, importance: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(time, fontWeight = FontWeight.Bold)
                Text(currency, style = MaterialTheme.typography.bodySmall)
            }
            Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                Text(event)
            }
            Text(
                importance,
                color = if (importance == "High") BearishRed else MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun NewsCard(title: String, sentiment: String, time: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(sentiment, style = MaterialTheme.typography.bodySmall)
                Text(time, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun COTCard(currency: String, sentiment: String, netPosition: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(currency, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    sentiment,
                    color = if (sentiment == "Bullish") BullishGreen else BearishRed,
                    fontWeight = FontWeight.Bold
                )
                Text(netPosition, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
