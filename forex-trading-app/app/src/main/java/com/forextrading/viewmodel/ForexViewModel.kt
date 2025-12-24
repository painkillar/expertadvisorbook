package com.forextrading.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forextrading.data.model.*
import com.forextrading.data.repository.ForexRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing forex data and UI state
 */
class ForexViewModel : ViewModel() {

    private val repository = ForexRepository()

    // State for economic calendar
    private val _economicCalendar = MutableStateFlow<UiState<EconomicCalendar>>(UiState.Loading)
    val economicCalendar: StateFlow<UiState<EconomicCalendar>> = _economicCalendar.asStateFlow()

    // State for news
    private val _news = MutableStateFlow<UiState<NewsFeed>>(UiState.Loading)
    val news: StateFlow<UiState<NewsFeed>> = _news.asStateFlow()

    // State for COT report
    private val _cotReport = MutableStateFlow<UiState<COTReport>>(UiState.Loading)
    val cotReport: StateFlow<UiState<COTReport>> = _cotReport.asStateFlow()

    // State for quotes
    private val _quotes = MutableStateFlow<UiState<List<PairQuote>>>(UiState.Loading)
    val quotes: StateFlow<UiState<List<PairQuote>>> = _quotes.asStateFlow()

    // State for daily bias
    private val _dailyBias = MutableStateFlow<UiState<DailyBiasReport>>(UiState.Loading)
    val dailyBias: StateFlow<UiState<DailyBiasReport>> = _dailyBias.asStateFlow()

    // Selected currency pair for details
    private val _selectedPair = MutableStateFlow<CurrencyPair?>(null)
    val selectedPair: StateFlow<CurrencyPair?> = _selectedPair.asStateFlow()

    init {
        loadAllData()
    }

    fun loadAllData() {
        loadEconomicCalendar()
        loadNews()
        loadCOTReport()
        loadQuotes()
        loadDailyBias()
    }

    fun loadEconomicCalendar() {
        viewModelScope.launch {
            _economicCalendar.value = UiState.Loading
            val result = repository.getEconomicCalendar()
            _economicCalendar.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun loadNews() {
        viewModelScope.launch {
            _news.value = UiState.Loading
            val result = repository.getNews()
            _news.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun loadCOTReport() {
        viewModelScope.launch {
            _cotReport.value = UiState.Loading
            val result = repository.getCOTReport()
            _cotReport.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun loadQuotes() {
        viewModelScope.launch {
            _quotes.value = UiState.Loading
            val result = repository.getQuotes()
            _quotes.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun loadDailyBias() {
        viewModelScope.launch {
            _dailyBias.value = UiState.Loading
            val result = repository.getDailyBias()
            _dailyBias.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun selectPair(pair: CurrencyPair) {
        _selectedPair.value = pair
    }

    fun clearSelectedPair() {
        _selectedPair.value = null
    }

    fun refreshData() {
        loadAllData()
    }
}

/**
 * Sealed class representing UI state
 */
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
