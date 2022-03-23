package com.fiz.mono.ui.calendar

import androidx.recyclerview.widget.DiffUtil

sealed class CalendarDataItem {
    data class DayWeekItem(val dayWeek: String) : CalendarDataItem()
    data class DayItem(val transactionsDay: TransactionsDay) : CalendarDataItem()

    companion object {
        fun getListDayWeekItem(): List<DayWeekItem> {
            return listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su").map { DayWeekItem(it) }
        }

        fun getListDayItem(list: List<TransactionsDay>): List<DayItem> {
            return list.map { DayItem(it.copy()) }
        }
    }
}

object DataItemDiff : DiffUtil.ItemCallback<CalendarDataItem>() {
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