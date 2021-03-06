package com.fiz.mono.base.android.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fiz.mono.base.android.databinding.ItemTransactionBinding
import com.fiz.mono.base.android.databinding.ItemTransactionDateExpenseIncomeBinding
import com.fiz.mono.base.android.utils.getColorCompat
import com.fiz.mono.base.android.utils.setVisible
import com.fiz.mono.common.ui.resources.R
import com.fiz.mono.domain.models.InfoDay
import com.fiz.mono.domain.models.Transaction
import com.fiz.mono.domain.models.TransactionsDataItem
import com.fiz.mono.util.CurrentUtils.getCurrencyFormat

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class TransactionsAdapter(
    private val currency: String,
    private val isVisibleIcon: Boolean,
    private val callback: (Transaction) -> Unit = {}
) :
    ListAdapter<TransactionsDataItem, RecyclerView.ViewHolder>(DataItemDiff) {

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
                val infoDay =
                    getItem(position) as TransactionsDataItem.InfoDayHeaderItem
                holder.bind(infoDay.infoDay, currency)
            }
            is InfoTransactionItemViewHolder -> {
                val transactionItem =
                    getItem(position) as TransactionsDataItem.InfoTransactionItem
                holder.bind(
                    transactionItem.transaction,
                    isVisibleIcon,
                    currency,
                    callback
                )
            }
        }

    }

    class InfoTransactionItemViewHolder(
        private var binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            transaction: Transaction,
            isVisibleIcon: Boolean,
            currency: String,
            callback: (Transaction) -> Unit
        ) {
            binding.apply {
                iconTransactionImageView.setVisible(isVisibleIcon)
                if (isVisibleIcon) {
                    transaction.imgSrc.let {
                        iconTransactionImageView.setImageResource(
                            it
                        )
                    }
                }

                categoryTransactionTextView.text = transaction.nameCategory

                noteTransactionTextView.text =
                    if (transaction.note.isNotBlank())
                        root.context.getString(R.string.transaction_note, transaction.note)
                    else
                        ""

                valueTextView.setTextColor(
                    if (transaction.value > 0)
                        root.context.getColorCompat(R.color.blue)
                    else
                        root.context.getColorCompat(R.color.red)
                )

                valueTextView.text =
                    getCurrencyFormat(currency, transaction.value, true)

                root.setOnClickListener { callback(transaction) }
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

