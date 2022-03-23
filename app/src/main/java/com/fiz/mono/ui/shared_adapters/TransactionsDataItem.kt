package com.fiz.mono.ui.shared_adapters

import androidx.recyclerview.widget.DiffUtil
import com.fiz.mono.data.TransactionItem

sealed class TransactionsDataItem {
    data class InfoTransactionItem(val transactionItem: TransactionItem) : TransactionsDataItem()
    data class InfoDayHeaderItem(val infoDay: InfoDay) : TransactionsDataItem()
}


object DataItemDiff : DiffUtil.ItemCallback<TransactionsDataItem>() {
    override fun areItemsTheSame(
        oldItem: TransactionsDataItem,
        newItem: TransactionsDataItem
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: TransactionsDataItem,
        newItem: TransactionsDataItem
    ): Boolean {
        return oldItem == newItem
    }
}