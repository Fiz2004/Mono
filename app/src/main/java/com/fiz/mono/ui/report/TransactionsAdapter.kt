package com.fiz.mono.ui.report

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fiz.mono.R
import com.fiz.mono.data.TransactionItem
import com.fiz.mono.databinding.ItemTransactionBinding
import com.fiz.mono.databinding.ItemTransactionDateExpenseIncomeBinding
import com.fiz.mono.ui.getCurrencyFormat
import com.fiz.mono.util.getColorCompat

class TransactionsAdapter(private val currency: String) :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(DataItemDiff) {

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
            is DataItem.InfoDayHeaderItem -> ITEM_VIEW_TYPE_HEADER
            is DataItem.InfoTransactionItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is InfoDayHeaderItemViewHolder -> {
                val infoDay = getItem(position) as DataItem.InfoDayHeaderItem
                holder.bind(infoDay.infoDay, currency)
            }
            is InfoTransactionItemViewHolder -> {
                val transactionItem = getItem(position) as DataItem.InfoTransactionItem
                holder.bind(transactionItem.transactionItem, currency)
            }
        }

    }

    class InfoTransactionItemViewHolder(
        private var binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transactionItem: TransactionItem, currency: String) {
            transactionItem.imgSrc?.let {
                binding.iconTransactionImageView.setImageResource(
                    it
                )
            }

            binding.categoryTransactionTextView.text = transactionItem.nameCategory
            if (transactionItem.note.isNotBlank())
                binding.noteTransactionTextView.text =
                    binding.root.context.getString(R.string.transaction_note, transactionItem.note)
            else
                binding.noteTransactionTextView.text = ""
            if (transactionItem.value > 0) {
                binding.valueTextView.setTextColor(binding.root.context.getColorCompat(R.color.blue))
                binding.valueTextView.text =
                    getCurrencyFormat(currency, transactionItem.value, true)
            } else {
                binding.valueTextView.setTextColor(binding.root.context.getColorCompat(R.color.red))
                binding.valueTextView.text =
                    getCurrencyFormat(currency, transactionItem.value, true)
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
            binding.date.text = infoDay.date
            if (infoDay.expense == 0.0) {
                binding.expense.visibility = View.GONE
            } else {
                binding.expense.visibility = View.VISIBLE
                binding.expense.text = getCurrencyFormat(currency, infoDay.expense, true)
            }
            if (infoDay.income == 0.0) {
                binding.income.visibility = View.GONE
            } else {
                binding.income.visibility = View.VISIBLE
                binding.income.text = getCurrencyFormat(currency, infoDay.income, true)
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

