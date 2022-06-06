package com.fiz.mono.domain.models

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