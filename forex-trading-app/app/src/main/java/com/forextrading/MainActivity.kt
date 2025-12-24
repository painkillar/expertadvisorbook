@file:OptIn(ExperimentalMaterial3Api::class)

package com.forextrading

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.forextrading.ui.theme.ForexTradingAppTheme

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
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Forex Trading App") }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Dashboard, "Dashboard") },
                    label = { Text("Dashboard") },
                    selected = currentRoute == "dashboard",
                    onClick = { navController.navigate("dashboard") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.TrendingUp, "Bias") },
                    label = { Text("Daily Bias") },
                    selected = currentRoute == "bias",
                    onClick = { navController.navigate("bias") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.CalendarToday, "Calendar") },
                    label = { Text("Economic") },
                    selected = currentRoute == "economic",
                    onClick = { navController.navigate("economic") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Article, "News") },
                    label = { Text("News") },
                    selected = currentRoute == "news",
                    onClick = { navController.navigate("news") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.BarChart, "COT") },
                    label = { Text("COT") },
                    selected = currentRoute == "cot",
                    onClick = { navController.navigate("cot") }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(padding)
        ) {
            composable("dashboard") { DemoDashboard() }
            composable("bias") { DemoBias() }
            composable("economic") { DemoEconomic() }
            composable("news") { DemoNews() }
            composable("cot") { DemoCOT() }
        }
    }
}

@Composable
fun DemoDashboard() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Currency Pairs",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(7) { index ->
            val pairs = listOf("EUR/USD", "GBP/USD", "USD/JPY", "AUD/USD", "USD/CHF", "NZD/USD", "USD/CAD")
            val prices = listOf("1.0850", "1.2650", "149.50", "0.6580", "0.8750", "0.6120", "1.3520")
            val changes = listOf("+0.45%", "-0.23%", "+0.67%", "+0.12%", "-0.08%", "+0.34%", "-0.15%")

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = pairs[index],
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = prices[index],
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Text(
                        text = changes[index],
                        style = MaterialTheme.typography.titleMedium,
                        color = if (changes[index].startsWith("+")) Color(0xFF4CAF50) else Color(0xFFF44336),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DemoBias() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Daily Trading Bias - London Open",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(5) { index ->
            val pairs = listOf("EUR/USD", "GBP/USD", "USD/JPY", "AUD/USD", "EUR/JPY")
            val biases = listOf("BULLISH", "BEARISH", "BULLISH", "NEUTRAL", "BULLISH")
            val confidences = listOf(85, 72, 68, 45, 78)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = pairs[index],
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = biases[index],
                            style = MaterialTheme.typography.titleMedium,
                            color = when (biases[index]) {
                                "BULLISH" -> Color(0xFF4CAF50)
                                "BEARISH" -> Color(0xFFF44336)
                                else -> Color.Gray
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Confidence: ${confidences[index]}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun DemoEconomic() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Economic Calendar",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(5) { index ->
            val events = listOf(
                "Non-Farm Payrolls",
                "ECB Interest Rate Decision",
                "GDP Growth Rate",
                "Employment Change",
                "Bank of Canada Rate Statement"
            )
            val times = listOf("08:30", "09:00", "12:30", "05:00", "13:00")
            val currencies = listOf("USD", "EUR", "GBP", "AUD", "CAD")

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = times[index],
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF2196F3),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currencies[index],
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = events[index],
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun DemoNews() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Forex News",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(5) { index ->
            val titles = listOf(
                "Federal Reserve Signals Potential Rate Cuts in Q2",
                "EUR/USD Reaches 2-Month High on Strong Eurozone Data",
                "Bank of Japan Maintains Ultra-Loose Monetary Policy",
                "GBP Volatility Expected Ahead of GDP Release",
                "Australian Dollar Climbs on Strong Employment Data"
            )
            val sources = listOf("Reuters", "Bloomberg", "Financial Times", "CNBC", "MarketWatch")

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = titles[index],
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = sources[index],
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun DemoCOT() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "COT Report",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(7) { index ->
            val currencies = listOf("EUR", "GBP", "JPY", "CHF", "AUD", "NZD", "CAD")
            val sentiments = listOf("BULLISH", "BEARISH", "BEARISH", "NEUTRAL", "BULLISH", "NEUTRAL", "BEARISH")
            val netPositions = listOf("+70,000", "-20,000", "-50,000", "+5,000", "+40,000", "+5,000", "-20,000")

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = currencies[index],
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = sentiments[index],
                            style = MaterialTheme.typography.titleMedium,
                            color = when (sentiments[index]) {
                                "BULLISH" -> Color(0xFF4CAF50)
                                "BEARISH" -> Color(0xFFF44336)
                                else -> Color.Gray
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Net Positions: ${netPositions[index]}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
