package com.fiz.mono.ui.shared_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fiz.mono.R
import com.fiz.mono.data.TransactionItem
import com.fiz.mono.data.getDrawableCategoryIcon
import com.fiz.mono.databinding.ItemTransactionBinding
import com.fiz.mono.databinding.ItemTransactionDateExpenseIncomeBinding
import com.fiz.mono.util.currentUtils.getCurrencyFormat
import com.fiz.mono.util.getColorCompat
import com.fiz.mono.util.setVisible

class TransactionsAdapter(
    private val currency: String,
    private val callback: (TransactionItem) -> Unit = {}
) :
    ListAdapter<TransactionsDataItem, RecyclerView.ViewHolder>(DataItemDiff) {

    private val ITEM_VIEW_TYPE_HEADER = 0
    private val ITEM_VIEW_TYPE_ITEM = 1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> InfoDayHeaderItemViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> InfoTransactionItemViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TransactionsDataItem.InfoDayHeaderItem -> ITEM_VIEW_TYPE_HEADER
            is TransactionsDataItem.InfoTransactionItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is InfoDayHeaderItemViewHolder -> {
                val infoDay = getItem(position) as TransactionsDataItem.InfoDayHeaderItem
                holder.bind(infoDay.infoDay, currency)
            }
            is InfoTransactionItemViewHolder -> {
                val transactionItem = getItem(position) as TransactionsDataItem.InfoTransactionItem
                holder.bind(transactionItem.transactionItem, currency, callback)
            }
        }

    }

    class InfoTransactionItemViewHolder(
        private var binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            transactionItem: TransactionItem,
            currency: String,
            callback: (TransactionItem) -> Unit
        ) {
            binding.apply {
                transactionItem.mapImgSrc.let {
                    iconTransactionImageView.setImageResource(
                        getDrawableCategoryIcon(it)
                    )
                }

                categoryTransactionTextView.text = transactionItem.nameCategory

                noteTransactionTextView.text =
                    if (transactionItem.note.isNotBlank())
                        root.context.getString(R.string.transaction_note, transactionItem.note)
                    else
                        ""

                valueTextView.setTextColor(
                    if (transactionItem.value > 0)
                        root.context.getColorCompat(R.color.blue)
                    else
                        root.context.getColorCompat(R.color.red)
                )

                valueTextView.text =
                    getCurrencyFormat(currency, transactionItem.value, true)

                root.setOnClickListener { callback(transactionItem) }
            }

        }

        companion object {
            fun from(parent: ViewGroup): InfoTransactionItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = ItemTransactionBinding.inflate(layoutInflater, parent, false)
                return InfoTransactionItemViewHolder(view)
            }
        }
    }

    class InfoDayHeaderItemViewHolder(
        private var binding: ItemTransactionDateExpenseIncomeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(infoDay: InfoDay, currency: String) {
            binding.apply {
                date.text = infoDay.date
                expense.setVisible(infoDay.expense != 0.0)
                expense.text =
                    if (infoDay.expense != 0.0)
                        getCurrencyFormat(currency, infoDay.expense, true)
                    else
                        ""
                income.setVisible(infoDay.income != 0.0)
                income.text =
                    if (infoDay.income != 0.0)
                        getCurrencyFormat(currency, infoDay.income, true)
                    else
                        ""
            }
        }

        companion object {
            fun from(parent: ViewGroup): InfoDayHeaderItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view =
                    ItemTransactionDateExpenseIncomeBinding.inflate(layoutInflater, parent, false)
                return InfoDayHeaderItemViewHolder(view)
            }
        }
    }
}

