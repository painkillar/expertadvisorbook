# Forex Trading Android App

A comprehensive Android application for forex traders that displays economic data, news, COT (Commitment of Traders) reports, and provides daily trading bias recommendations for London open (08:00 GMT).

## Features

### 📊 Dashboard
- Real-time quotes for major currency pairs
- Yen crosses (EUR/JPY, GBP/JPY, AUD/JPY, NZD/JPY, CHF/JPY, CAD/JPY)
- Daily price changes and percentage movements
- Clean, modern UI with dark theme

### 📈 Daily Bias Recommendations
- **Advanced recommendation engine** analyzing multiple factors:
  - COT (Commitment of Traders) positioning
  - Economic calendar events
  - News sentiment analysis
  - Technical factors (trend, support/resistance, momentum)
  - Risk sentiment (Risk On/Risk Off)
- **London Open focus** (08:00 GMT) - optimal trading time
- Confidence scores for each recommendation
- Trade setups with entry zones, stop loss, and take profit levels
- Detailed reasoning for each bias

### 📅 Economic Calendar
- High-impact economic events
- Forecast vs previous data
- Real-time actual data when released
- Color-coded by importance (High/Medium/Low)
- Covers all major currencies

### 📰 Forex News
- Latest forex market news
- Sentiment analysis (Bullish/Bearish/Neutral)
- Categorized by type (Central Bank, Economic Data, Geopolitical, etc.)
- Affected currencies highlighted

### 📊 COT Report
- Commitment of Traders data for all major currencies
- Non-commercial (speculators) positioning
- Commercial (hedgers) positioning
- Net positions and weekly changes
- Sentiment indicators (Bullish/Bearish/Neutral)

## Supported Currency Pairs

### Major Pairs
- EUR/USD
- GBP/USD
- USD/JPY
- USD/CHF
- AUD/USD
- NZD/USD
- USD/CAD

### Yen Crosses
- EUR/JPY
- GBP/JPY
- AUD/JPY
- NZD/JPY
- CHF/JPY
- CAD/JPY

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Networking**: Retrofit + OkHttp
- **Coroutines**: Kotlin Coroutines for async operations
- **State Management**: StateFlow
- **Navigation**: Jetpack Navigation Compose
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## Project Structure

```
forex-trading-app/
├── app/
│   ├── src/main/
│   │   ├── java/com/forextrading/
│   │   │   ├── data/
│   │   │   │   ├── model/          # Data models
│   │   │   │   ├── api/            # API services
│   │   │   │   └── repository/     # Data repositories
│   │   │   ├── engine/
│   │   │   │   └── BiasEngine.kt   # Trading bias recommendation engine
│   │   │   ├── ui/
│   │   │   │   ├── screens/        # Compose screens
│   │   │   │   ├── components/     # Reusable UI components
│   │   │   │   └── theme/          # App theme
│   │   │   ├── viewmodel/          # ViewModels
│   │   │   └── MainActivity.kt
│   │   ├── AndroidManifest.xml
│   │   └── res/
│   └── build.gradle
└── build.gradle
```

## Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or later
- Android SDK 34
- Gradle 8.2+

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd forex-trading-app
```

2. Open the project in Android Studio

3. Sync Gradle files

4. Run the app on an emulator or physical device

## API Integration

Currently, the app uses **mock data** for development and testing. To integrate with real forex data APIs:

### Step 1: Choose Your Data Provider

Popular forex data APIs:
- **ForexFactory** - Economic calendar
- **OANDA** - Real-time quotes and historical data
- **Alpha Vantage** - Forex data and news
- **Forex.com API** - Comprehensive forex data
- **Twelve Data** - Financial market data
- **Finnhub** - News and economic calendar
- **Quandl** - COT data

### Step 2: Update API Configuration

Edit `app/src/main/java/com/forextrading/data/api/RetrofitClient.kt`:

```kotlin
object RetrofitClient {
    // Replace with your actual API base URL
    private const val BASE_URL = "https://your-api-provider.com/v1/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                // Add your API key
                .addHeader("Authorization", "Bearer YOUR_API_KEY")
                .build()
            chain.proceed(request)
        }
        .build()
    // ... rest of configuration
}
```

### Step 3: Disable Mock Data

Edit `app/src/main/java/com/forextrading/data/repository/ForexRepository.kt`:

```kotlin
class ForexRepository {
    private val useMockData = false // Change to false
    // ... rest of repository
}
```

### Step 4: Implement API Endpoints

Update the methods in `ForexApiService.kt` to match your API provider's endpoints:

```kotlin
interface ForexApiService {
    @GET("economic-calendar")
    suspend fun getEconomicCalendar(
        @Query("from") fromDate: String,
        @Query("to") toDate: String
    ): Response<EconomicCalendar>

    // Update other endpoints...
}
```

## Trading Bias Algorithm

The app uses a sophisticated multi-factor analysis engine for daily bias recommendations:

### Factors Analyzed (with weights):

1. **COT Data (30%)**: Institutional positioning from Commitment of Traders reports
2. **News Sentiment (25%)**: Sentiment analysis from recent forex news
3. **Technical Trend (25%)**: Price trend, momentum, and volatility
4. **Economic Events (20%)**: Impact of scheduled high-importance events
5. **Risk Sentiment (10%)**: Overall market risk appetite

### Bias Types:

- **Bullish**: Score > 0.4 - Recommend buying opportunities
- **Bearish**: Score < -0.4 - Recommend selling opportunities
- **Range Bound**: |Score| < 0.15 - Sideways market, avoid directional trades
- **Neutral**: Other cases

### Confidence Calculation:

Confidence is derived from the absolute value of the combined score, indicating how strong the signals align.

## Customization

### Adding New Currency Pairs

Edit `app/src/main/java/com/forextrading/data/model/CurrencyPair.kt`:

```kotlin
enum class CurrencyPair(val symbol: String, val displayName: String, val isYenCross: Boolean = false) {
    // Add your new pair
    EUR_GBP("EURGBP", "EUR/GBP"),
    // ...
}
```

### Adjusting Bias Algorithm

Modify weights and logic in `app/src/main/java/com/forextrading/engine/BiasEngine.kt`:

```kotlin
// COT analysis (adjust weight)
when (fundamentalFactors.cotSentiment) {
    COTSentiment.BULLISH -> {
        score += 0.3 // Change this value
        // ...
    }
}
```

### Changing Theme Colors

Edit `app/src/main/java/com/forextrading/ui/theme/Color.kt`:

```kotlin
val BullishGreen = Color(0xFF4CAF50) // Customize colors
val BearishRed = Color(0xFFF44336)
```

## Important Disclaimers

⚠️ **Trading Risk Warning**:
- Forex trading involves substantial risk of loss
- Past performance is not indicative of future results
- This app is for informational purposes only
- Always use proper risk management
- Never risk more than you can afford to lose
- Consult with a financial advisor before trading

⚠️ **App Limitations**:
- Currently uses mock data (requires API integration)
- Recommendations are algorithmic and not financial advice
- Market conditions can change rapidly
- Always verify information from multiple sources

## Development Roadmap

- [ ] Real-time price updates via WebSocket
- [ ] Price alerts and notifications
- [ ] Advanced charting with TradingView
- [ ] Trade journal functionality
- [ ] Performance analytics
- [ ] Multi-timeframe analysis
- [ ] Custom indicator support
- [ ] Backtesting engine
- [ ] Social trading features

## Building for Production

1. Update API keys and endpoints
2. Disable mock data
3. Test thoroughly with real data
4. Update ProGuard rules if needed
5. Generate signed APK:

```bash
./gradlew assembleRelease
```

## License

[Your License Here]

## Support

For questions or issues, please open an issue on GitHub.

## Contributing

Contributions are welcome! Please read the contributing guidelines before submitting PRs.

---

**Developed for forex traders who want data-driven insights for London open trading sessions.**
