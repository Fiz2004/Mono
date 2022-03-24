package com.fiz.mono.ui.calendar

import androidx.recyclerview.widget.DiffUtil
import java.text.SimpleDateFormat
import java.util.*

sealed class CalendarDataItem {
    data class DayWeekItem(val dayWeek: String) : CalendarDataItem()
    data class DayItem(val transactionsDay: TransactionsDay) : CalendarDataItem()

    companion object {
        fun getListDayWeekItem(): List<DayWeekItem> {
            val dayOfWeek = Calendar.getInstance()
            val listDayOfWeek = mutableListOf<String>()
            for (n in 0..6) {
                dayOfWeek.set(Calendar.DAY_OF_WEEK, n)
                var nameDay = SimpleDateFormat(
                    "EE",
                    Locale.getDefault()
                ).format(dayOfWeek.time)
                nameDay = nameDay.take(2)
                listDayOfWeek.add(nameDay)
            }
            val result = mutableListOf<String>()
            result.addAll(listDayOfWeek.drop(2))
            result.addAll(listDayOfWeek.take(2))
            return listDayOfWeek.map { DayWeekItem(it) }
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