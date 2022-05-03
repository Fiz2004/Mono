package com.fiz.mono.domain.models

import androidx.recyclerview.widget.DiffUtil

sealed class TransactionsDataItem {
    data class InfoTransactionItem(val transaction: Transaction) : TransactionsDataItem()
    data class InfoDayHeaderItem(val infoDay: InfoDay) : TransactionsDataItem()

    companion object {
        fun getListTransactionsDataItem(allTransactionsForDay: List<Transaction>?): MutableList<TransactionsDataItem> {
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