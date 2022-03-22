package com.fiz.mono.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fiz.mono.databinding.ItemCalendarBinding
import com.fiz.mono.databinding.ItemCalendarDayWeekBinding

class CalendarAdapter :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(DataItemDiff) {

    private val ITEM_VIEW_TYPE_DAY_WEEK = 0
    private val ITEM_VIEW_TYPE_DAY = 1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_DAY_WEEK -> DayWeekItemViewHolder.from(parent)
            ITEM_VIEW_TYPE_DAY -> DayItemViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.DayWeekItem -> ITEM_VIEW_TYPE_DAY_WEEK
            is DataItem.DayItem -> ITEM_VIEW_TYPE_DAY
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DayWeekItemViewHolder -> {
                val dayWeek = getItem(position) as DataItem.DayWeekItem
                holder.bind(dayWeek.dayWeek)
            }
            is DayItemViewHolder -> {
                val transactionsDay = getItem(position) as DataItem.DayItem
                holder.bind(transactionsDay.transactionsDay)
            }
        }

    }

    class DayWeekItemViewHolder(
        private var binding: ItemCalendarDayWeekBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dayWeek: String) {
            binding.descriptionTextView.text = dayWeek
        }

        companion object {
            fun from(parent: ViewGroup): DayWeekItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = ItemCalendarDayWeekBinding.inflate(layoutInflater, parent, false)
                return DayWeekItemViewHolder(view)
            }
        }
    }

    class DayItemViewHolder(
        private var binding: ItemCalendarBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transactionsDay: TransactionsDay) {
            binding.descriptionTextView.text = transactionsDay.day.toString()
            binding.iconImageView1.visibility =
                if (transactionsDay.expense) View.VISIBLE else View.GONE
            binding.iconImageView2.visibility =
                if (transactionsDay.income) View.VISIBLE else View.GONE
        }

        companion object {
            fun from(parent: ViewGroup): DayItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view =
                    ItemCalendarBinding.inflate(layoutInflater, parent, false)
                return DayItemViewHolder(view)
            }
        }
    }
}