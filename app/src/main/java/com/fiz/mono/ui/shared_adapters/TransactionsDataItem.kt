package com.fiz.mono.ui.shared_adapters

import androidx.recyclerview.widget.DiffUtil
import com.fiz.mono.ui.models.TransactionUiState

sealed class TransactionsDataItem {
    data class InfoTransactionItem(val transaction: TransactionUiState) : TransactionsDataItem()
    data class InfoDayHeaderItem(val infoDay: InfoDay) : TransactionsDataItem()

    companion object {
        fun getListTransactionsDataItem(allTransactionsForDay: List<TransactionUiState>?): MutableList<TransactionsDataItem> {
            val items = mutableListOf<TransactionsDataItem>()
            items += allTransactionsForDay?.map {
                InfoTransactionItem(
                    it
                )
            } ?: listOf()
            return items
        }
    }
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