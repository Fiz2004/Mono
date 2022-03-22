package com.fiz.mono.ui.calendar

import androidx.recyclerview.widget.DiffUtil

sealed class DataItem {
    data class DayWeekItem(val dayWeek: String) : DataItem()
    data class DayItem(val transactionsDay: TransactionsDay) : DataItem()
}

object DataItemDiff : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: DataItem,
        newItem: DataItem
    ): Boolean {
        return oldItem == newItem
    }
}