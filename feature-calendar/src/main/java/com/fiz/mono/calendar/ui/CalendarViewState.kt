package com.fiz.mono.calendar.ui

import com.fiz.mono.domain.models.TransactionsDataItem
import org.threeten.bp.LocalDate

data class CalendarViewState(
    val date: LocalDate = LocalDate.now(),
    val currency: String = "$",
    val isAllTransactionsLoaded: Boolean = false,
    val calendarDataItem: List<CalendarDataItem> = listOf(),
    val transactionsDataItem: List<TransactionsDataItem> = listOf()
)

