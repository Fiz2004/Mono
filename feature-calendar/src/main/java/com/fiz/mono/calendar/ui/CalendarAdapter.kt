package com.fiz.mono.calendar.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fiz.mono.core.util.setVisible
import com.fiz.mono.feature.calendar.databinding.ItemCalendarBinding
import com.fiz.mono.feature.calendar.databinding.ItemCalendarDayWeekBinding
import com.fiz.mono.ui.calendar.TransactionsDay

private const val ITEM_VIEW_TYPE_DAY_WEEK = 0
private const val ITEM_VIEW_TYPE_DAY = 1

class CalendarAdapter(private val callback: (TransactionsDay) -> Unit) :
    ListAdapter<CalendarDataItem, RecyclerView.ViewHolder>(CalendarDataItemDataItemDiff) {

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
            is CalendarDataItem.DayWeekItem -> ITEM_VIEW_TYPE_DAY_WEEK
            is CalendarDataItem.DayItem -> ITEM_VIEW_TYPE_DAY
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DayWeekItemViewHolder -> {
                val dayWeek = getItem(position) as CalendarDataItem.DayWeekItem
                holder.bind(dayWeek.dayWeek)
            }
            is DayItemViewHolder -> {
                val transactionsDay = getItem(position) as CalendarDataItem.DayItem
                holder.bind(transactionsDay.transactionsDay, callback)
            }
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
    fun bind(transactionsDay: TransactionsDay, callback: (TransactionsDay) -> Unit) {
        binding.apply {
            dayOfMonthTextView.text = transactionsDay.getFormatDayOfMonthOrBlank()
            expenseImageView.setVisible(transactionsDay.expense)
            incomeImageView.setVisible(transactionsDay.income)
            cardMaterialCard.isActivated = transactionsDay.today
            cardMaterialCard.isCheckable = transactionsDay.selected

            root.setOnClickListener { callback(transactionsDay) }
        }

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