package com.fiz.mono.ui.report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fiz.mono.R
import com.fiz.mono.data.TransactionItem
import com.fiz.mono.databinding.ItemTransactionBinding
import com.fiz.mono.util.getColorCompat
import kotlin.math.abs

class TransactionsAdapter(private val currency: String) :
    ListAdapter<TransactionItem, TransactionsAdapter.TransactionItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionsAdapter.TransactionItemViewHolder {
        return TransactionItemViewHolder(
            ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: TransactionItemViewHolder, position: Int) {
        val categoryItem = getItem(position)
        holder.bind(categoryItem)
    }

    inner class TransactionItemViewHolder(
        private var binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transactionItem: TransactionItem) {
            transactionItem.imgSrc?.let {
                binding.iconTransactionImageView.setImageResource(
                    it
                )
            }

            binding.categoryTransactionTextView.text = transactionItem.nameCategory
            binding.noteTransactionTextView.text = transactionItem.note
            if (transactionItem.value > 0) {
                binding.valueTextView.setTextColor(binding.root.context.getColorCompat(R.color.blue))
                binding.valueTextView.text = "+" + currency + "%.2f".format(transactionItem.value)
            } else {
                binding.valueTextView.setTextColor(binding.root.context.getColorCompat(R.color.red))
                binding.valueTextView.text =
                    "-" + currency + "%.2f".format(abs(transactionItem.value))
            }

        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<TransactionItem>() {
        override fun areItemsTheSame(oldItem: TransactionItem, newItem: TransactionItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: TransactionItem,
            newItem: TransactionItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}