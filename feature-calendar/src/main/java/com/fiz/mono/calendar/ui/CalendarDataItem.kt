package com.fiz.mono.calendar.ui

import androidx.recyclerview.widget.DiffUtil
import org.threeten.bp.DayOfWeek
import org.threeten.bp.format.TextStyle
import java.util.*

sealed class CalendarDataItem {
    data class DayWeekItem(val dayWeek: String) : CalendarDataItem()
    data class DayItem(val transactionsDay: TransactionsDay) : CalendarDataItem()

    companion object {
        fun getListCalendarDataItem(transactionsForDaysCurrentMonth: List<TransactionsDay>): List<CalendarDataItem> {
            return getDayWeekItemsNameDaysOfWeekTwoCharsCapitalize() +
                    getDayItems(transactionsForDaysCurrentMonth)
        }

        private fun getDayWeekItemsNameDaysOfWeekTwoCharsCapitalize(): List<DayWeekItem> {
            fun String.capitalizeNow(): String {
                return this.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
            }

            val nameDaysOfWeek =
                DayOfWeek.values()
                    .map { it.getDisplayName(TextStyle.SHORT, Locale.getDefault()) }

            val dayWeekItemsNameDaysOfWeekTwoCharsCapitalize = nameDaysOfWeek.map {
                DayWeekItem(it.take(2).capitalizeNow())
            }

            return dayWeekItemsNameDaysOfWeekTwoCharsCapitalize

        }

        private fun getDayItems(list: List<TransactionsDay>): List<DayItem> {
            return list.map { DayItem(it.copy()) }
        }
    }
}

object CalendarDataItemDataItemDiff : DiffUtil.ItemCallback<CalendarDataItem>() {
    override fun areItemsTheSame(oldItem: CalendarDataItem, newItem: CalendarDataItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: CalendarDataItem,
        newItem: CalendarDataItem
    ): Boolean {
        return oldItem == newItem
    }
}