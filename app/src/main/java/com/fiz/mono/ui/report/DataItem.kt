package com.fiz.mono.ui.report

import androidx.recyclerview.widget.DiffUtil
import com.fiz.mono.data.TransactionItem

sealed class DataItem {
    data class InfoTransactionItem(val transactionItem: TransactionItem) : DataItem()
    data class InfoDayHeaderItem(val infoDay: InfoDay) : DataItem()
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