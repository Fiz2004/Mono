package com.fiz.mono.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiz.mono.calendar.domain.getAllTransactionsForDay
import com.fiz.mono.calendar.domain.getTransactionsForDaysCurrentMonth
import com.fiz.mono.domain.models.TransactionsDataItem
import com.fiz.mono.domain.repositories.SettingsRepository
import com.fiz.mono.domain.repositories.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    var viewState = MutableStateFlow(CalendarViewState())
        private set

    var viewEffects = MutableSharedFlow<CalendarViewEffect>()
        private set

    init {

        settingsRepository.observeDate()
            .onEach { date ->
                viewState.value = viewState.value
                    .copy(date = date)
            }.launchIn(viewModelScope)

        settingsRepository.currency.load()
            .onEach { currency ->
                viewState.value = viewState.value
                    .copy(currency = currency)
            }.launchIn(viewModelScope)

        settingsRepository.observeDate().flatMapLatest { date ->
            transactionRepository.allTransactions
        }.onEach { allTransactions ->
            viewState.value = viewState.value
                .copy(
                    isAllTransactionsLoaded = true,
                    calendarDataItem = CalendarDataItem.getListCalendarDataItem(
                        getTransactionsForDaysCurrentMonth(allTransactions, viewState.value.date)
                    ),
                    transactionsDataItem = TransactionsDataItem.getListTransactionsDataItem(
                        getAllTransactionsForDay(allTransactions, viewState.value.date)
                    )
                )
        }.launchIn(viewModelScope)

    }

    fun setMonth(month: Int) {
        viewModelScope.launch {
            val date = settingsRepository.getDate()

            val newDate = date.withMonth(month)

            settingsRepository.setDate(newDate)
        }
    }

    fun setDate(day: Int) {
        viewModelScope.launch {
            if (day == 0) return@launch

            val date = settingsRepository.getDate()

            val newDate = date.withDayOfMonth(day)

            settingsRepository.setDate(newDate)
        }
    }

    fun clickBackButton() {
        viewModelScope.launch {
            viewEffects.emit(CalendarViewEffect.MoveReturn)
        }
    }

    fun changeData(date: LocalDate) {
        viewState.value = viewState.value
            .copy(date = date)
    }

    fun onAllTransactionsLoaded() {
        viewState.value = viewState.value
            .copy(isAllTransactionsLoaded = false)
    }
}